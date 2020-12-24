package com.bapplications.maplemobile.ui

import android.media.MediaPlayer
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bapplications.maplemobile.R
import com.bapplications.maplemobile.constatns.Configuration
import com.bapplications.maplemobile.ui.adapters.DownloaderAdapter
import com.bapplications.maplemobile.ui.etc.FileDownloader
import com.bapplications.maplemobile.ui.view_models.DownloadActivityViewModel


val files = arrayOf(
        "String.nx",
        "Map.nx",
        "Sound.nx",
        "Character.nx",
        "Mob.nx",
        "Item.nx",
)

val TAG = "DownloadManager"


class DownloadActivity : AppCompatActivity() {

    private val viewModel: DownloadActivityViewModel by viewModels()

    private val CHUNK_SIZE: Long = 8192
    private var keepToNextActivity = true

    // CR:: this fields are use only on setView() function, this is unnecessary
    private lateinit var backgroundIv: ImageView
    private var fileSize: Long = 1L

    private val client = okhttp3.OkHttpClient()
    private lateinit var bgm: MediaPlayer

    val adapter = DownloaderAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Configuration.WZ_DIRECTORY = getExternalFilesDir(null)!!.absolutePath
        Configuration.CACHE_DIRECTORY = cacheDir.absolutePath

        viewModel.files.value = mutableListOf()

        bgm = MediaPlayer.create(this, R.raw.download_bgm).apply { isLooping = true }
        setUpView()

        viewModel.files.observe(this, Observer {
            it.let {
                adapter.data = it
            }
        })

        val downloadingState = object : DownloadingState {
            override fun onDownloadFinished(fileDownloader: FileDownloader) {
                viewModel.files.value!!.remove(fileDownloader)
            }
        }

        files.forEach {
            viewModel.files.postValue(viewModel.files.value?.apply {
                val fileDownloader = FileDownloader(it, downloadingState)
                this.add(fileDownloader)
                fileDownloader.startDownload()
            })
        }

//            if (File(Configuration.WZ_DIRECTORY, it).exists()) {
//                downloadIfNeeded(it, getLocalMd5(it))
//            } else {
//                downloadFile("${Configuration.FILES_HOST}/$it", it)
//            }
//            if (!File("${Configuration.WZ_DIRECTORY}/$it").exists()) {
//                keepToNextActivity = false
//            }
//        }
//
    }

//    fun getLocalMd5(fileName: String): String {
//        val md5File = File(Configuration.WZ_DIRECTORY, "$fileName.md5")
//        return if (md5File.exists()) {
//            md5File.readText()
//        } else {
//            val md5 = generateMd5(File(Configuration.WZ_DIRECTORY, fileName))
//            md5File.writeText(md5)
//            md5
//        }
//    }


    override fun onResume() {
        super.onResume()
        bgm.start()
    }

    override fun onPause() {
        bgm.stop()
        super.onPause()
    }

    private fun setUpView() {
        // CR:: change to data binding setter instead of this
        // Than you can discard the use of findviewbyid functions and use binding.downloadingTv instead
        setContentView(R.layout.activity_download)
        backgroundIv = findViewById(R.id.background_iv)


        // CR:: try to find how I use the progress layout with viewmodel on the hp/mp progress
        // CR:: after using the viewmodel you dont going to use that but for knowing,
        // you dont need to call for settext / setprogress functions
        // Int kotlin you can use the '=' operator to values and kotlin call its own setters.
        // in that case downloadingTv.text = "..." will do the job
        // same with the progress

        // background animation
        backgroundAnimation(backgroundIv)

//        val adapter = DownloaderAdapter()
        val recyclerView = findViewById<RecyclerView>(R.id.downloader_recycler)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter


    }

    private fun backgroundAnimation(backgroundIv: ImageView) {
        val fadeOut: Animation = AnimationUtils.loadAnimation(this, R.anim.fade_out).apply { startOffset = 10000 }
        val fadeIn: Animation = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        backgroundIv.startAnimation(fadeOut)

        val backgroundsDrawableResources = listOf(R.drawable.wallpaper1,
                R.drawable.wallpaper2,
                R.drawable.wallpaper3,
                R.drawable.wallpaper4,
                R.drawable.wallpaper5)
        val randomWallpaper = { current: Int ->
            var drawable: Int
            do {
                drawable = backgroundsDrawableResources.random()
            } while (drawable == current)
            drawable
        }
        var currentBackground = backgroundsDrawableResources.random()
        backgroundIv.setImageDrawable(ContextCompat.getDrawable(this, currentBackground))
        fadeOut.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                currentBackground = randomWallpaper(currentBackground)
                backgroundIv.setImageDrawable(ContextCompat.getDrawable(this@DownloadActivity, currentBackground))
                backgroundIv.startAnimation(fadeIn)
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })
        fadeIn.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                backgroundIv.startAnimation(fadeOut)
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })
    }
}


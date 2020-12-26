package com.bapplications.maplemobile.ui

import android.content.Intent
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
import com.bapplications.maplemobile.databinding.ActivityDownloadBinding
import com.bapplications.maplemobile.ui.adapters.DownloaderAdapter
import com.bapplications.maplemobile.ui.etc.FileDownloader
import com.bapplications.maplemobile.ui.view_models.DownloadActivityViewModel
import java.io.File


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

    private lateinit var bgm: MediaPlayer

    val adapter = DownloaderAdapter()

    fun startGameActivity() {
        val intent = Intent(this, GameActivity::class.java)
        startActivity(intent)

    }

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
                if (viewModel.files.value!!.size == 0) {
                    startGameActivity()
                }
            }
        }

        files.forEach {
            viewModel.files.postValue(viewModel.files.value?.apply {
                if (!File(Configuration.WZ_DIRECTORY, it).exists()) {
                    val fileDownloader = FileDownloader(it, downloadingState)
                    this.add(fileDownloader)
                    fileDownloader.startDownload()
                }
            })
        }
        if (viewModel.files.value!!.size == 0) {
            startGameActivity()
        }

    }

    override fun onResume() {
        super.onResume()
        bgm.start()
    }

    override fun onPause() {
        bgm.stop()
        super.onPause()
    }

    private fun setUpView() {
        val binding: ActivityDownloadBinding = ActivityDownloadBinding.inflate(layoutInflater)

        setContentView(binding.rootLayout)
        setContentView(R.layout.activity_download)

        // background animation
        backgroundAnimation(binding.backgroundIv)
        binding.downloaderRecycler.layoutManager = LinearLayoutManager(this)
        binding.downloaderRecycler.adapter = adapter


    }

    private fun backgroundAnimation(backgroundIv: ImageView) {
        val fadeOut: Animation = AnimationUtils.loadAnimation(this, R.anim.fade_out).apply { startOffset = 10000 }
        val fadeIn: Animation = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        backgroundIv.startAnimation(fadeOut)

        val backgroundsDrawableResources = listOf(R.drawable.wallpaper1,
                R.drawable.wallpaper2,
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


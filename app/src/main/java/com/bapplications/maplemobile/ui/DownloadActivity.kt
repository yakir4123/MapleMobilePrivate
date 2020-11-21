package com.bapplications.maplemobile.ui

import android.app.DownloadManager
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.bapplications.maplemobile.R
import com.bapplications.maplemobile.constatns.Configuration
import com.bapplications.maplemobile.ui.adapters.DownloaderAdapter
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import okio.Buffer
import okio.BufferedSink
import okio.buffer
import okio.sink
import java.io.File
import java.io.IOException
import java.lang.reflect.Field
import kotlin.concurrent.thread


val files = arrayOf(
        "String.nx",
        "Map.nx",
        "Sound.nx",
        "Character.nx",
        "Mob.nx",
        "Item.nx",
//        "np"
)

val TAG = "DownloadManager"


class DownloadActivity : AppCompatActivity() {

    private val CHUNK_SIZE: Long = 8192
    private var keepToNextActivity = true

    // CR:: this fields are use only on setView() function, this is unnecessary
    private lateinit var currentFileTv: TextView
    private lateinit var downloadingTv: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var backgroundIv: ImageView
    private var fileSize: Long = 1L

    private val client = okhttp3.OkHttpClient()
    private lateinit var bgm: MediaPlayer
    val progress = MutableLiveData<Long>()
    val textProgress = MutableLiveData<Long>()
    val currentFile = MutableLiveData<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Configuration.WZ_DIRECTORY = getExternalFilesDir(null)!!.absolutePath
        Configuration.CACHE_DIRECTORY = cacheDir.absolutePath

        files.forEach {
//            val md5 : String = File(Configuration.WZ_DIRECTORY, "$it.md5").readText()
            val url = getMd5("http://137.135.90.47/$it")
            if (!File("${Configuration.WZ_DIRECTORY}/$it").exists()) {
                keepToNextActivity = false
            }
        }

        if (keepToNextActivity) {
            startActivity(Intent(this, GameActivity::class.java))
        } else {
            bgm = MediaPlayer.create(this, R.raw.download_bgm).apply { isLooping = true }
            setUpView()
//            downloadFiles()
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
        // CR:: change to data binding setter instead of this
        // Than you can discard the use of findviewbyid functions and use binding.downloadingTv instead
        setContentView(R.layout.activity_download)
        downloadingTv = findViewById(R.id.downloadingTv)
        progressBar = findViewById(R.id.progressBar)
        currentFileTv = findViewById(R.id.file_name_tv)
        backgroundIv = findViewById(R.id.background_iv)


        // set value for the view model
        progress.value = 0L
        textProgress.value = 0L

        // CR:: try to find how I use the progress layout with viewmodel on the hp/mp progress
        // CR:: after using the viewmodel you dont going to use that but for knowing,
        // you dont need to call for settext / setprogress functions
        // Int kotlin you can use the '=' operator to values and kotlin call its own setters.
        // in that case downloadingTv.text = "..." will do the job
        // same with the progress
        textProgress.observe(this, Observer { newProgress ->
            downloadingTv.setText("$newProgress / $fileSize")
            progressBar.setProgress((newProgress * 100 / fileSize).toInt())

        })

        currentFile.observe(this, Observer { file -> currentFileTv.setText(file) })

        // background animation
        backgroundAnimation(backgroundIv)

        val adapter = DownloaderAdapter()
        findViewById<RecyclerView>(R.id.downloader_recycler).adapter = adapter


    }

    private fun getMd5(fileName : String, localMd5 : String) {
        Log.d(TAG, "get md5 from file: $fileName")
        val request: okhttp3.Request = okhttp3.Request.Builder()
                .url("${Configuration.FILES_HOST}/$fileName")
                .build()
        var buffer = Buffer()

        client.newCall(request).enqueue(
                object :Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        Log.d(TAG, "onFail: " + e)
                    }

                    override fun onResponse(call: Call, response: Response) {
                        Log.d(TAG, "onResponse: ")

                        if (response.code != 200) {
                            Log.d(TAG, "onResponse: ")
                            val remoteFileMd5 = response.body!!.string()
                            if (remoteFileMd5 != localMd5) {
                                downloadFile("${Configuration.FILES_HOST}/$fileName", File(getExternalFilesDir(null)!!, fileName))
                            }
                        }
                    }
                }
        )
    }

//    private fun downloadFiles() {
//
//        // CR:: instead of thread use coroutine, it is network operation is IO operation so asyncIO is better than thread
//        thread(start = true) {
//            // CR:: instead of thread and a loop isn't that better to download all files seperatly?
//            files.forEach {
////                downloadFile("http://192.168.1.18/$it", File(getExternalFilesDir(null)!!, it))
//                // CR:: dont use magic
//                downloadFile("${Configuration.FILES_HOST}/$it", File(getExternalFilesDir(null)!!, it))
//            }
//
//        }
//    }

    fun downloadFile(url: String, path: File) {
        Log.d(TAG, "downloadFile: downloading: $url, size $fileSize")
        currentFile.postValue(url)
        val request: okhttp3.Request = okhttp3.Request.Builder()
                .url(url)
                .build()

        // we are using the synchronous
//         CR:: why synchrounes?
//        saveFile(client.newCall(request).execute(), path)
    }

    private fun saveFile(response: Response, path: File) {
        val sink: BufferedSink = path.sink().buffer()
        var byteCount: Long

        val source = response.body!!.source()
        fileSize = response.body!!.contentLength()
        val buffer = Buffer()

        while (source.read
                (buffer, CHUNK_SIZE).also {
                    byteCount = it
                    textProgress.postValue(textProgress.value!! + byteCount)
                    Log.d(TAG, "saveFile: $byteCount")
                } != -1L) {
            sink.write(buffer, byteCount)
            sink.flush()
        }

        sink.close()

        // reset the progress bar and the download information
        // CR:: Im going to change your layout that will have
        // recyclerview of progressviews will help you to call coroutine there
        // and update the value there
        textProgress.postValue(0)
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
            } while(drawable == current)
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


package com.bapplications.maplemobile.ui

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bapplications.maplemobile.R
import com.bapplications.maplemobile.constatns.Configuration
import com.bapplications.maplemobile.ui.adapters.DownloaderAdapter
import com.bapplications.maplemobile.ui.view_models.DownloadActivityViewModel
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import okio.Buffer
import okio.BufferedSink
import okio.buffer
import okio.sink
import java.io.File
import java.io.IOException
import java.math.BigInteger
import java.security.MessageDigest


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

    private val viewModel: DownloadActivityViewModel by viewModels()

    private val CHUNK_SIZE: Long = 8192
    private var keepToNextActivity = true

    // CR:: this fields are use only on setView() function, this is unnecessary
    private lateinit var backgroundIv: ImageView
    private var fileSize: Long = 1L

    private val client = okhttp3.OkHttpClient()
    private lateinit var bgm: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Configuration.WZ_DIRECTORY = getExternalFilesDir(null)!!.absolutePath
        Configuration.CACHE_DIRECTORY = cacheDir.absolutePath

        viewModel.progress.value = 0
        viewModel.textProgress.value = 0
        viewModel.currentFile.value = ""

        bgm = MediaPlayer.create(this, R.raw.download_bgm).apply { isLooping = true }

        files.forEach {
            if (File(Configuration.WZ_DIRECTORY, it).exists()) {
                downloadIfNeeded(it, getLocalMd5(it))
            } else {
                downloadFile("${Configuration.FILES_HOST}/$it", it)
            }
//            if (!File("${Configuration.WZ_DIRECTORY}/$it").exists()) {
//                keepToNextActivity = false
//            }
//        }
//
//        if (keepToNextActivity) {
//            startActivity(Intent(this, GameActivity::class.java))
//        } else {
            setUpView()
//            downloadFiles()
        }
    }

    fun getLocalMd5(fileName: String): String {
        val md5File = File(Configuration.WZ_DIRECTORY, "$fileName.md5")
        return if (md5File.exists()) {
            md5File.readText()
        } else {
            val md5 = generateMd5(File(Configuration.WZ_DIRECTORY, fileName))
            md5File.writeText(md5)
            md5
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
        backgroundIv = findViewById(R.id.background_iv)


        // CR:: try to find how I use the progress layout with viewmodel on the hp/mp progress
        // CR:: after using the viewmodel you dont going to use that but for knowing,
        // you dont need to call for settext / setprogress functions
        // Int kotlin you can use the '=' operator to values and kotlin call its own setters.
        // in that case downloadingTv.text = "..." will do the job
        // same with the progress

        // background animation
        backgroundAnimation(backgroundIv)

        val adapter = DownloaderAdapter()
        findViewById<RecyclerView>(R.id.downloader_recycler).adapter = adapter

    }

    private fun downloadIfNeeded(fileName: String, localMd5: String) {
        Log.d(TAG, "get md5 from file: $fileName")
        val request: okhttp3.Request = okhttp3.Request.Builder()
                .url("${Configuration.FILES_HOST}/$fileName.md5")
                .build()

        client.newCall(request).enqueue(
                object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        Log.d(TAG, "onFail: " + e)
                    }

                    override fun onResponse(call: Call, response: Response) {
                        Log.d(TAG, "onResponse: ")

                        if (response.code == 200) {
                            Log.d(TAG, "onResponse: ")
                            val remoteFileMd5 = response.body!!.string()
                            if (remoteFileMd5 != localMd5) {
                                downloadFile("${Configuration.FILES_HOST}/$fileName", fileName)
                            }
                        }
                    }
                }
        )
    }

    fun downloadFile(url: String, fileName: String) {
        Log.d(TAG, "downloadFile: downloading: $url, size $fileSize")
        viewModel.currentFile.postValue(url)
        val request: okhttp3.Request = okhttp3.Request.Builder()
                .url(url)
                .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "onFailure: ")
            }

            override fun onResponse(call: Call, response: Response) {
                saveFile(response, File(Configuration.WZ_DIRECTORY, fileName), File(Configuration.WZ_DIRECTORY, "$fileName.md5"))
            }

        })
    }

    private fun generateMd5(path: File): String {
        //todo: check if this need to be here
        val buff = ByteArray(8192)
        val digest = MessageDigest.getInstance("MD5")
        path.inputStream().buffered().use {
            it.read(buff)
            digest.update(buff)
        }
        val md5sum: ByteArray = digest.digest()
        val bigInt = BigInteger(1, md5sum)
        var output: String = bigInt.toString(16)
        // Fill to 32 chars
        output = String.format("%32s", output).replace(' ', '0')
        return output


    }

    private fun saveFile(response: Response, path: File, md5Path: File) {
        val sink: BufferedSink = path.sink().buffer()
        var byteCount: Long

        val source = response.body!!.source()
        fileSize = response.body!!.contentLength()
        val buffer = Buffer()

        while (source.read
                (buffer, CHUNK_SIZE).also {
                    byteCount = it
                    viewModel.textProgress.postValue(viewModel.textProgress.value!! + byteCount)
                    Log.d(TAG, "saveFile: $byteCount")
                } != -1L) {
            sink.write(buffer, byteCount)
            sink.flush()
        }

        sink.close()
        md5Path.writeText(generateMd5(path))


        // reset the progress bar and the download information
        // CR:: Im going to change your layout that will have
        // recyclerview of progressviews will help you to call coroutine there
        // and update the value there
        viewModel.textProgress.postValue(0)
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


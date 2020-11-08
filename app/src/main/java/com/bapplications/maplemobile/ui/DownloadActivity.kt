package com.bapplications.maplemobile.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.bapplications.maplemobile.R
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import okio.Buffer
import okio.BufferedSink
import okio.buffer
import okio.sink
import java.io.File
import java.io.IOException
import kotlin.concurrent.thread


val files = arrayOf(
        "String.nx",
        "Map.nx",
        "Sound.nx",
        "Character.nx",
        "Mob.nx",
        "Item.nx"
)

val TAG = "DownloadManager"


class DownloadActivity : AppCompatActivity() {

    private val CHUNK_SIZE: Long = 8192
    private var keepToNextActivity = true

    private lateinit var currentFileTv: TextView
    private lateinit var downloadingTv: TextView
    private lateinit var progressBar: ProgressBar
    private var fileSize: Long = 1L

    private val client = okhttp3.OkHttpClient()

    val progress = MutableLiveData<Long>()
    val textProgress = MutableLiveData<Long>()
    val currentFile = MutableLiveData<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download)

        downloadingTv = findViewById(R.id.downloadingTv)
        progressBar = findViewById(R.id.progressBar)
        currentFileTv = findViewById(R.id.file_name_tv)


        // set value for the view model
        progress.value = 0L
        textProgress.value = 0L

        textProgress.observe(this, Observer { newProgress ->
            downloadingTv.setText("$newProgress / $fileSize")
            progressBar.setProgress((newProgress * 100 / fileSize).toInt())

        })

        currentFile.observe(this, Observer { file -> currentFileTv.setText(file)})

        downloadFiles()

//        files.forEach {
//            if (!File("${this.getExternalFilesDir(null)}/$it").exists()) {
//                keepToNextActivity = false
//            }
//        }
//
//        if (keepToNextActivity) {
//            startActivity(Intent(this, GameActivity::class.java))
//        } else {
//            downloadFiles()
//        }
    }

    fun downloadFiles() {
        thread(start = true) {
//            downloadFile("http://137.135.90.47/Charecter.nx", File(getExternalFilesDir(null)!!, "Charecter.nx"))
            files.forEach {
                downloadFile("http://137.135.90.47/$it", File(getExternalFilesDir(null)!!, it))
            }
        }
//        downloadManager = this.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
//        com.bapplications.maplemobile.utils.files.forEach {
//            if (!File("${this.getExternalFilesDir(null)}$it").exists()) {
//                Log.d("DownloadingManager", "downloadAssets: $it: http://${Configuration.FILES_HOST}$it")
//                downloadFile("https://${Configuration.FILES_HOST}/$it", getExternalFilesDir(null)!!)
//                val request = DownloadManager.Request(
//                        Uri.parse("http://${Configuration.FILES_HOST}/$it"))
//                        .setTitle("MapleMobile")
//                        .setDescription("downloading $it")
//                        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
//                        .setDestinationUri(Uri.parse("file://${this.getExternalFilesDir(null)}/$it"))
//                downloadManager.enqueue(request)
//            }
//        }
    }

    fun downloadFile(url: String, path: File) {
        currentFile.postValue(url)
        val request: okhttp3.Request = okhttp3.Request.Builder()
                .url(url)
                .build()

        client.newCall(request).enqueue(
                object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        Log.d(TAG, "onFail: " + e)
                    }

                    override fun onResponse(call: Call, response: Response) {
                        Log.d(TAG, "onResponse: ")
                        saveFile(response, path)
                    }
                }
        )
//        val response: okhttp3.Response = client.newCall(request).execute()

    }

    private fun saveFile(response: Response, path: File) {
        val sink: BufferedSink = path.sink().buffer()
        var byteCount: Long

        val source = response.body!!.source()
        fileSize = response.body!!.contentLength()
        Log.d(TAG, "file size " + fileSize)
        val buffer = Buffer()

        while (source.read
                (buffer, CHUNK_SIZE).also {
                    byteCount = it
                    progress.value!!.plus(byteCount)
                    textProgress.postValue(textProgress.value!! + byteCount)
                } != -1L) {
            sink.write(buffer, byteCount)
            sink.flush()
        }

        sink.close()
    }
}
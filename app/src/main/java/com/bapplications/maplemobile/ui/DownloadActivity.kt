package com.bapplications.maplemobile.ui

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
        "Map.nx",
        "Sound.nx",
        "Character.nx",
        "String.nx",
        "Mob.nx",
        "Item.nx"
)

val TAG = "DownloadManager"


class DownloadActivity : AppCompatActivity() {

    private val CHUNK_SIZE: Long = 8192
    private var keepToNextActivity = true

    private lateinit var downloadingTv: TextView
    private lateinit var progressBar: ProgressBar
    private var fileSize: Long = 1L

    private val client = okhttp3.OkHttpClient()

    val progress = MutableLiveData<Long>()
    val textProgress = MutableLiveData<Long>()
//    private var prograss : Observable<Float>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download)

        downloadingTv = findViewById(R.id.downloadingTv)
        progressBar = findViewById(R.id.progressBar)


        // set value for the view model
        progress.value = 0L
        textProgress.value = 0L

//        progress.observe(this, Observer { newProgress ->
//            val progressValue = (newProgress / fileSize).toInt()
//            Log.d(TAG, "progress : " + progressValue)
//            progressBar.setProgress((newProgress / fileSize).toInt())
//        })


        textProgress.observe(this, Observer { newProgress ->
            downloadingTv.setText(newProgress.toString())
            progressBar.setProgress((newProgress * 1000 / fileSize).toInt())
            Log.d(TAG, "progress:  " + (newProgress * 1000 / fileSize).toInt())

        })
//        progressBar.setProgress(100)
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
            downloadFile("http://137.135.90.47/Charecter.nx", File(getExternalFilesDir(null)!!, "Charecter.nx"))
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
//                    textProgress.value!!.plus(byteCount)
                    textProgress.postValue(textProgress.value!! + byteCount)
                    Log.d(TAG, "text progress value : " + textProgress.value)
                } != -1L) {
            sink.write(buffer, byteCount)
            sink.flush()
        }

        sink.close()
    }
}
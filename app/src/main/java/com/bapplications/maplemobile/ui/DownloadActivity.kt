package com.bapplications.maplemobile.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
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

    private val  CHUNK_SIZE : Long = 8192
//    private lateinit var downloadManager: DownloadManager
    private var keepToNextActivity = true

    private val client = okhttp3.OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download)

        files.forEach {
            if (!File("${this.getExternalFilesDir(null)}/$it").exists()) {
                keepToNextActivity = false
            }
        }

        if (keepToNextActivity) {
            startActivity(Intent(this, GameActivity::class.java))
        } else {
            downloadFiles()
        }
    }

    fun downloadFiles() {
        thread (start=true){
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
                object :Callback {
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

    private fun saveFile(response: Response, path : File) {
        val sink: BufferedSink = path.sink().buffer()
        var byteCount : Long

        val source = response.body!!.source()
        val buffer = Buffer()

        while (source.read
                (buffer, CHUNK_SIZE).also {
                    byteCount = it
                    Log.d(TAG, "bytecount: " + byteCount)
                } != -1L) {
                sink.write(buffer, byteCount)
                sink.flush()
            }

        sink.close()
    }
}
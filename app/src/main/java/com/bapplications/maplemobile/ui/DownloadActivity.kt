package com.bapplications.maplemobile.ui

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bapplications.maplemobile.R
import com.bapplications.maplemobile.constatns.Configuration
import okhttp3.Response
import okio.BufferedSink
import okio.Okio
import okio.`-DeprecatedOkio`.sink
import okio.buffer
import okio.sink
import java.io.File
import java.io.IOException
import kotlin.concurrent.thread
import kotlin.jvm.Throws

val files = arrayOf(
        "Map.nx",
        "Sound.nx",
        "Character.nx",
        "String.nx",
        "Mob.nx",
        "Item.nx"
)



class DownloadActivity : AppCompatActivity() {
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
        val response: okhttp3.Response = client.newCall(request).execute()
        saveFileFromOkHttp(response, path)

    }

    private fun saveFileFromOkHttp(response: Response, path : File) {
        val sink: BufferedSink = path.sink().buffer()
        Log.d("DownloadingManager", "saveFileFromOkHttp:")
        sink.writeAll(response.body!!.source())
        sink.close()
    }
}
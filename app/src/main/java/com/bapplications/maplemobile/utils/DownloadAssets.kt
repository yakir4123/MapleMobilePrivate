package com.bapplications.maplemobile.utils

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.util.Log
import com.bapplications.maplemobile.constatns.Configuration.HOST
import java.io.File

val files = arrayOf(
        "Map.nx",
        "Sound.nx",
        "Character.nx",
        "String.nx",
        "Mob.nx",
        "Item.nx"
)

public fun downloadAssets(context: Context) {
    val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager?

    files.forEach {
        if (!File("${context.getExternalFilesDir(null)}/$it").exists()) {
            Log.d("DownloadingManager", "downloadAssets: $it")
            val request = DownloadManager.Request(
                    Uri.parse("http://$HOST:443/$it"))
                    .setTitle("MapleMobile")
                    .setDescription("downloading $it")
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDestinationUri(Uri.parse("file://${context.getExternalFilesDir(null)}/$it"))
            downloadManager!!.enqueue(request)
        }
    }
}
package com.bapplications.maplemobile.utils

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import com.bapplications.maplemobile.constatns.Configuration.HOST


fun downloadAssets(context: Context) {
    val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager?
    hashMapOf<String, String>(
            "Map.nx"
    )
    val files = arrayOf(
            "Map.nx",
            "Sound.nx",
            "Character.nx",
            "String.nx",
            "Mob.nx",
            "Item.nx"
    ).forEach {
        request = DownloadManager.Request(
                Uri.parse("http://$HOST:443/$it"))
                .setTitle("MapleMobile")
                .se
    }

    for
}
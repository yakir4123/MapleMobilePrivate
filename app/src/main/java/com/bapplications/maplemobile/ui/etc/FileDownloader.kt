package com.bapplications.maplemobile.ui.etc

import okio.*
import java.io.File
import okhttp3.Call
import okhttp3.Callback
import android.util.Log
import okhttp3.Response
import java.io.IOException
import okhttp3.OkHttpClient
import java.util.zip.GZIPInputStream
import com.bapplications.maplemobile.ui.TAG
import com.bapplications.maplemobile.ui.DownloadingState
import com.bapplications.maplemobile.constatns.Configuration

class FileDownloader(val fileName: String, val downloadingState: DownloadingState) {

    val client: OkHttpClient = OkHttpClient()

    var progress: Long = 0
    var fileSize: Int = 0
    var downloading : Boolean = false


    fun startDownload() {
        downloading = true
        val compressFilePath = File(Configuration.WZ_DIRECTORY, "$fileName.gz")
        downloadFile("${Configuration.FILES_HOST}/$fileName.gz", compressFilePath)
    }

    fun downloadFile(url: String, filePath: File) {
        Log.i(TAG, "download file from $url to $fileName")
        val request: okhttp3.Request = okhttp3.Request.Builder()
                .url(url)
                .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "request failed, url: ${request.url}\n", e)
            }

            override fun onResponse(call: Call, response: Response) {
                Log.d(TAG, "response from ${response.request.url}, code: ${response.code}")
                saveFile(response, filePath)
            }

        })
    }

    fun ungzip(srcPath: File, dstPath: File) {
        Log.d(TAG, "unzip $srcPath to $dstPath")
        dstPath.outputStream().use { GZIPInputStream(srcPath.inputStream()).copyTo(it) }
    }

    private fun saveFile(response: Response, path: File) {
        val sink: BufferedSink = path.sink().buffer()
        var byteCount: Long

        val source = response.body!!.source()
        fileSize = response.body!!.contentLength().toInt()
        val buffer = Buffer()

        while (source.read
                (buffer, CHUNK_SIZE).also {
                    byteCount = it
                    progress += it
                } != -1L) {
            sink.write(buffer, byteCount)
            sink.flush()
        }

        sink.close()

        val nxFile = File(Configuration.WZ_DIRECTORY, fileName)
        ungzip(path, nxFile)
        path.delete()
        downloading = false
        downloadingState.onDownloadFinished(this)
    }
}

val CHUNK_SIZE: Long = 8192


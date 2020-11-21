package com.bapplications.maplemobile.ui.etc

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.bapplications.maplemobile.constatns.Configuration
import com.bapplications.maplemobile.ui.TAG
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Response
import okio.Buffer
import okio.BufferedSink
import okio.buffer
import okio.sink
import java.io.File
import java.io.IOException
import java.math.BigInteger
import java.security.MessageDigest

val CHUNK_SIZE: Long = 8192

class FileDownloader(val fileName: String) {


    // todo: ask yakir about that it crates new one every time
    val client: OkHttpClient = OkHttpClient()

    var progress : Long = 0
    var fileSize: Int = 0



    fun startDownload() {
        downloadIfNeeded(fileName, getLocalMd5(fileName))
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

    private fun saveFile(response: Response, path: File, md5Path: File) {
        val sink: BufferedSink = path.sink().buffer()
        var byteCount: Long

        val source = response.body!!.source()
        fileSize = response.body!!.contentLength().toInt()
        val buffer = Buffer()

        while (source.read
                (buffer, CHUNK_SIZE).also {
                    byteCount = it
                    Log.d(TAG, "saveFile: $byteCount")
                    progress += it
                } != -1L) {
            sink.write(buffer, byteCount)
            sink.flush()
        }

        sink.close()
        md5Path.writeText(generateMd5(path))
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
}

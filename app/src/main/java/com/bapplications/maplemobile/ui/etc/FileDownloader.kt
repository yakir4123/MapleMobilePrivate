package com.bapplications.maplemobile.ui.etc

import android.util.Log
import com.bapplications.maplemobile.constatns.Configuration
import com.bapplications.maplemobile.ui.DownloadingState
import com.bapplications.maplemobile.ui.TAG
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Response
import okio.*
import java.io.File
import java.io.IOException
import java.math.BigInteger
import java.security.MessageDigest
import java.util.zip.GZIPInputStream

class FileDownloader(val fileName: String, val downloadingState: DownloadingState) {


    // todo: ask yakir about that it crates new one every time
    val client: OkHttpClient = OkHttpClient()

    var progress: Long = 0
    var fileSize: Int = 0


    fun startDownload() {
        downloadIfNeeded(fileName, getLocalMd5(fileName))
    }

    private fun downloadIfNeeded(fileName: String, localMd5: String) {
        Log.d(TAG, "get md5 from file: $fileName")
        val request: okhttp3.Request = okhttp3.Request.Builder()
                .url("${Configuration.FILES_HOST}/$fileName.gz.md5")
                .build()

        client.newCall(request).enqueue(
                object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        Log.d(TAG, "onFail: " + e)
                    }

                    override fun onResponse(call: Call, response: Response) {
                        Log.d(TAG, "response from: ${response.request.url} with code:${response.code}")

                        when (response.code) {
                            200 -> {
                                val remoteFileMd5 = response.body!!.string()
                                if (remoteFileMd5 != localMd5) {
                                    downloadFile("${Configuration.FILES_HOST}/$fileName", fileName)
                                }
                            }

                            else -> {
                                if (!File(fileName).exists()) {
                                    downloadFile("${Configuration.FILES_HOST}/$fileName.gz", "$fileName.gz")
                                } else {
                                    Log.d(TAG, "skipping downloading $fileName")
                                }
                            }
                        }
                    }

                }
        )
    }

    fun downloadFile(url: String, fileName: String) {
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
                saveFile(response, File(Configuration.WZ_DIRECTORY, fileName))
            }

        })
    }

    fun ungzip(srcPath: File, dstPath: File) {
        Log.d(TAG, "unzip $srcPath to $dstPath")
        val sourceBuffer = GZIPInputStream(srcPath.inputStream()).source()
        val dstSinkBuffer : BufferedSink = dstPath.sink().buffer()
        dstSinkBuffer.writeAll(sourceBuffer)
//        val buffer = Buffer()
//        while (sourceBuffer.read
//                (buffer, CHUNK_SIZE) != -1L) {
//            dstSinkBuffer.writeAll(buffer)
//        }
//            sink.write(buffer, byteCount)


//        while (
//        dstPath.writeBytes(GZIPInputStream(srcPath.inputStream()).)
    }

//    fun ungzip(srcPath: File, dstPath: File) {
//        fileSize = srcPath.length().toInt()
//
//        val sink: FileOutputStream = dstPath.outputStream()
//        var gzipInput = GZIPInputStream(srcPath.inputStream())
//        var byteCount: Long
//        var buffer: ByteArray = ByteArray(CHUNK_SIZE.toInt())
//
//        while (gzipInput.read(buffer).also {
//                    byteCount = it.toLong()
//                    Log.d(TAG, "saveFile: $byteCount")
//                    progress += it
//                } != -1) {
//            sink.write(buffer)
//        }
//        sink.close()
//    }

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
        File("$nxFile.md5").writeText(generateMd5(path))
        downloadingState.onDownloadFinished(this)
    }

    private fun generateMd5(path: File): String {
        //todo: check if this need to be here
        if (!path.exists())
            return ""

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

val CHUNK_SIZE: Long = 8192


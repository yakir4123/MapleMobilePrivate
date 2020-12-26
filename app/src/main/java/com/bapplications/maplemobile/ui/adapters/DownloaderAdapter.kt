package com.bapplications.maplemobile.ui.adapters

import android.os.Handler
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bapplications.maplemobile.R
import com.bapplications.maplemobile.ui.adapters.holders.FileDownloaderItemViewHolder
import com.bapplications.maplemobile.ui.etc.FileDownloader
import java.text.NumberFormat
import java.util.*

val updateProgressTime : Long = 100

class DownloaderAdapter : RecyclerView.Adapter<FileDownloaderItemViewHolder>() {

    lateinit var handler : Handler
    var data =  mutableListOf<FileDownloader>()

        set(value) {
            field = value
            notifyDataSetChanged()
        }


    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: FileDownloaderItemViewHolder, position: Int) {
        holder.downloadProgressTV.text = "${getFormatedNumber(data[position].progress.toInt())} / ${getFormatedNumber(data[position].fileSize)}"
        holder.downloadPB.progress = data[position].progress.toInt()
        holder.downloadPB.max = data[position].fileSize
        holder.fileNameTV.text = data[position].fileName
    }

    private fun getFormatedNumber(amount: Int): String? {
        return NumberFormat.getNumberInstance(Locale.US).format(amount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileDownloaderItemViewHolder {
        handler = Handler()
        handler.postDelayed( object : Runnable {
            override fun run() {
                notifyDataSetChanged()
                handler.postDelayed(this, updateProgressTime)

            }
        }, 1000)

        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater
                .inflate(R.layout.file_downloader_item_recyclerview, parent, false)

        return FileDownloaderItemViewHolder(view)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun getFileDownloader(position: Int): FileDownloader {
        return data[position]
    }
}

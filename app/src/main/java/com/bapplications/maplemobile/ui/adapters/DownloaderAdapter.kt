package com.bapplications.maplemobile.ui.adapters

import android.os.Handler
import android.util.Log
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.bapplications.maplemobile.R
import androidx.recyclerview.widget.RecyclerView
import com.bapplications.maplemobile.ui.adapters.holders.FileDownloaderItemViewHolder
import com.bapplications.maplemobile.ui.etc.FileDownloader
import okhttp3.Callback

class DownloaderAdapter : RecyclerView.Adapter<FileDownloaderItemViewHolder>() {

    var data =  mutableListOf<FileDownloader>()

        set(value) {
            field = value
            notifyDataSetChanged()
        }


    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: FileDownloaderItemViewHolder, position: Int) {
        holder.downloadProgressTV.text = "${data[position].progress} / ${data[position].fileSize}"
        holder.downloadPB.progress = data[position].progress.toInt()
        holder.downloadPB.max = data[position].fileSize
        holder.fileNameTV.text = data[position].fileName
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileDownloaderItemViewHolder {
        Handler().postDelayed({
            notifyDataSetChanged()
            Log.d("TAG", "chandeddddd: ")
        }, 15000)
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

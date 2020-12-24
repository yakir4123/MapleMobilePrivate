package com.bapplications.maplemobile.ui.adapters.holders

import android.view.View
import android.widget.TextView
import android.widget.ProgressBar
import com.bapplications.maplemobile.R
import androidx.recyclerview.widget.RecyclerView

class FileDownloaderItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val fileNameTV: TextView = itemView.findViewById(R.id.file_name_tv)
    val downloadPB: ProgressBar = itemView.findViewById(R.id.download_pb)
    val downloadProgressTV: TextView = itemView.findViewById(R.id.download_progress_tv)

}
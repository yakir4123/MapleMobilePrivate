package com.bapplications.maplemobile.ui

import com.bapplications.maplemobile.ui.etc.FileDownloader

interface DownloadingState {
    fun onDownloadFinished(fileDownloader: FileDownloader)
}
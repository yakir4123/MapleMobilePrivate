package com.bapplications.maplemobile.ui.view_models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bapplications.maplemobile.ui.etc.FileDownloader

class DownloadActivityViewModel : ViewModel() {
    val files = MutableLiveData<MutableList<FileDownloader>>()
    val wifiConnection = MutableLiveData<Boolean>(false)
}

package com.bapplications.maplemobile.ui.view_models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bapplications.maplemobile.ui.etc.FileDownloader

class DownloadActivityViewModel : ViewModel() {
    val files = MutableLiveData<MutableList<FileDownloader>>()






//    val progress = MutableLiveData<Int>()
//    var textProgress = MutableLiveData<Long>()
//    val currentFile = MutableLiveData<String>()

    // todo: ask yakir about it
//    init {
//        progress.postValue(0)
//        textProgress.postValue(0)
//        currentFile.value = ""
//    }

}

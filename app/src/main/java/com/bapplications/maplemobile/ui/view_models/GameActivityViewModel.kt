package com.bapplications.maplemobile.ui.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import com.bapplications.maplemobile.ui.GameActivityUIManager.WindowState

class GameActivityViewModel : ViewModel() {

    val windowState = MutableLiveData(WindowState.GONE)
    fun setWindowState(state: WindowState) {
        if (windowState.value != state) windowState.postValue(state)
    }
}
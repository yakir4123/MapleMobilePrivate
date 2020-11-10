package com.bapplications.maplemobile.ui.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import com.bapplications.maplemobile.gameplay.player.inventory.InventorySlot

class ItemInfoViewModel : ViewModel() {

    var inventorySlot =  MutableLiveData<InventorySlot>()

}
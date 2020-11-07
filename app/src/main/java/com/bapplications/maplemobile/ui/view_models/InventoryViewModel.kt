package com.bapplications.maplemobile.ui.view_models

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bapplications.maplemobile.gameplay.player.inventory.InventoryType
import com.bapplications.maplemobile.gameplay.player.inventory.InventorySlot
import java.util.*

class InventoryViewModel : ViewModel() {


    val itemInventory by lazy { MediatorLiveData<ArrayList<InventorySlot>>() }

    var selectedInventoryType = MutableLiveData(InventoryType.Id.EQUIP)
    fun setSelectedInventoryType(value : InventoryType.Id) {
        selectedInventoryType.postValue(value)
    }

    var inventorySlot =  MutableLiveData<InventorySlot>()
    fun setInventorySlot(value : InventorySlot?) = inventorySlot.postValue(value)

}
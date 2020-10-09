package com.bapplications.maplemobile.views.view_models

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bapplications.maplemobile.gameplay.player.inventory.InventoryType
import com.bapplications.maplemobile.gameplay.player.inventory.Item
import com.bapplications.maplemobile.gameplay.player.inventory.Slot
import java.util.*

class InventoryViewModel : ViewModel() {


    val itemInventory by lazy { MediatorLiveData<ArrayList<Slot>>() }

    var selectedInventoryType = MutableLiveData(InventoryType.Id.EQUIP)
    fun setSelectedInventoryType(value : InventoryType.Id) {
        selectedInventoryType.postValue(value)
    }

    var slot =  MutableLiveData<Slot>()
    fun setSlot(value : Slot?) = slot.postValue(value)

}
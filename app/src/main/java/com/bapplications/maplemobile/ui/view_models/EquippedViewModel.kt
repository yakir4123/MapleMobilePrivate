package com.bapplications.maplemobile.ui.view_models

import java.util.*
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import com.bapplications.maplemobile.gameplay.player.inventory.EquipSlot
import com.bapplications.maplemobile.gameplay.player.inventory.InventorySlot
import com.bapplications.maplemobile.gameplay.player.inventory.EquippedInventory

class EquippedViewModel : ViewModel() {

    val equippedInventory = ArrayList<MutableLiveData<InventorySlot>>(EquipSlot.Id.values().size)

    fun getEquipBySlot(equipSlot: EquipSlot.Id) : LiveData<InventorySlot> {
        return equippedInventory[equipSlot.ordinal]
    }

    fun setEquipOnSlot(item: InventorySlot, equipSlot: EquipSlot.Id) {
        equippedInventory[equipSlot.ordinal].postValue(item)
    }

    fun setEquippedInventory(equippedInventory: EquippedInventory) {
        for(equipSlot in EquipSlot.Id.values()) {
            this.equippedInventory[equipSlot.ordinal].postValue(equippedInventory[equipSlot])
        }
    }

    init {
        for(equipSlot in EquipSlot.Id.values()) {
            this.equippedInventory.add(MutableLiveData())
        }

    }
}
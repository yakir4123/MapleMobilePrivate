package com.bapplications.maplemobile.ui.windows

import android.util.Log
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bapplications.maplemobile.input.EventsQueue
import com.bapplications.maplemobile.gameplay.player.Player
import com.bapplications.maplemobile.input.events.DropItemEvent
import com.bapplications.maplemobile.input.events.EquipItemEvent
import com.bapplications.maplemobile.ui.view_models.ItemInfoViewModel
import com.bapplications.maplemobile.databinding.FragmentItemInfoBinding
import com.bapplications.maplemobile.gameplay.player.inventory.InventorySlot
import com.bapplications.maplemobile.gameplay.player.inventory.InventoryType
import com.bapplications.maplemobile.input.events.UnequipItemEvent

class ItemInfoFragment(val player: Player) : Fragment() {

    private val viewModel: ItemInfoViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding : FragmentItemInfoBinding = FragmentItemInfoBinding.inflate(inflater, container, false)
        binding.setLifecycleOwner { lifecycle }
        binding.viewModel = viewModel
        viewModel.inventorySlot.value = null

        binding.itemInfoDropBt.setOnClickListener {

            val dropped = player.inventory
                    .getInventory(viewModel.inventorySlot.value?.inventoryType)
                    .popItem(viewModel.inventorySlot.value?.slotId!!)
            EventsQueue.instance
                    .enqueue(DropItemEvent(dropped.itemId, player.position,
                            0, viewModel.inventorySlot.value?.slotId!!,
                            player.map.mapId))
            viewModel.inventorySlot.value = null

        }

        binding.itemInfoEquipBt.setOnClickListener {

            if(viewModel.inventorySlot.value?.inventoryType == InventoryType.Id.EQUIP) {
                if(player.canWearItem(viewModel.inventorySlot.value!!.item)) {
                    EventsQueue.instance
                            .enqueue(EquipItemEvent(0, viewModel.inventorySlot.value?.slotId!!,
                                    viewModel.inventorySlot.value!!.itemId))
                }
            } else { // Equipped inventory
                // unequip item need to check if it can be added to the inventory
                if(player.canPickupItem(viewModel.inventorySlot.value?.item)) {
                    EventsQueue.instance
                            .enqueue(UnequipItemEvent(0, viewModel.inventorySlot.value?.slotId!!,
                                    viewModel.inventorySlot.value?.item?.itemId!!))
                }
            }
            viewModel.inventorySlot.value = null

        }

        return binding.root
    }

    fun setItem(slot: InventorySlot?) {
        viewModel.inventorySlot.value = slot
    }

}
package com.bapplications.maplemobile.ui.windows

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.bapplications.maplemobile.R
import com.bapplications.maplemobile.databinding.FragmentInventoryBinding
import com.bapplications.maplemobile.databinding.FragmentItemInfoBinding
import com.bapplications.maplemobile.gameplay.player.Player
import com.bapplications.maplemobile.gameplay.player.inventory.InventorySlot
import com.bapplications.maplemobile.gameplay.player.inventory.InventoryType
import com.bapplications.maplemobile.input.EventsQueue
import com.bapplications.maplemobile.input.events.DropItemEvent
import com.bapplications.maplemobile.input.events.EquipItemEvent
import com.bapplications.maplemobile.ui.GameActivity
import com.bapplications.maplemobile.ui.view_models.ItemInfoViewModel

class ItemInfoFragment(val player: Player) : Fragment() {

    private val viewModel: ItemInfoViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding : FragmentItemInfoBinding = FragmentItemInfoBinding.inflate(inflater, container, false)
        binding.setLifecycleOwner { lifecycle }
        binding.viewModel = viewModel
        viewModel.inventorySlot.postValue(null)
        binding.returnBt.setOnClickListener{parentFragmentManager.popBackStack()}

        binding.itemInfoDropBt.setOnClickListener {
            val dropped = player.inventory
                    .getInventory(viewModel.inventorySlot.value?.inventoryType)
                    .popItem(viewModel.inventorySlot.value?.slotId!!)
            EventsQueue.instance
                    .enqueue(DropItemEvent(dropped.itemId, player.position,
                            0, viewModel.inventorySlot.value?.slotId!!,
                            player.map?.mapId!!))
        }

        binding.itemInfoEquipBt.setOnClickListener{
            EventsQueue.instance
                    .enqueue(EquipItemEvent(0, viewModel.inventorySlot.value?.slotId!!))
        }

        return binding.root
    }

    fun setItem(slot: InventorySlot) {
        viewModel.inventorySlot.value = slot
    }

}
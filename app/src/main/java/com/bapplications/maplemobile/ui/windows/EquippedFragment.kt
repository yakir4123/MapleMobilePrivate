package com.bapplications.maplemobile.ui.windows

import android.view.View
import android.os.Bundle
import android.view.ViewGroup
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import com.bapplications.maplemobile.R
import androidx.fragment.app.viewModels
import androidx.databinding.DataBindingUtil
import com.bapplications.maplemobile.ui.view_models.EquippedViewModel
import com.bapplications.maplemobile.databinding.FragmentEquippedBinding
import com.bapplications.maplemobile.gameplay.player.inventory.EquippedInventory
import com.bapplications.maplemobile.input.EventsQueue
import com.bapplications.maplemobile.input.events.*

class EquippedFragment(val inventory: EquippedInventory) : Fragment(), EventListener {

    private val viewModel: EquippedViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val binding : FragmentEquippedBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_equipped, container, false)

        EventsQueue.instance.registerListener(EventType.ItemDropped, this)
        EventsQueue.instance.registerListener(EventType.EquipItem, this)
        EventsQueue.instance.registerListener(EventType.UnequipItem, this)

        binding.viewModel = viewModel
        binding.setLifecycleOwner { lifecycle }

        viewModel.setEquippedInventory(inventory)
        return binding.root
    }

    override fun onDestroyView() {
        EventsQueue.instance.unregisterListener(EventType.ItemDropped, this)
        EventsQueue.instance.unregisterListener(EventType.EquipItem, this)
        EventsQueue.instance.unregisterListener(EventType.UnequipItem, this)
        super.onDestroyView()
    }

    override fun onEventReceive(event: Event) {
        activity?.runOnUiThread {
            when (event) {
                is ItemDroppedEvent, is EquipItemEvent,  is UnequipItemEvent -> {
                    viewModel.setEquippedInventory(inventory)}
            }
        }
    }


}
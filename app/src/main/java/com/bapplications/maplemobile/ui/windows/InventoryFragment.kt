package com.bapplications.maplemobile.ui.windows

import android.util.Log

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.bapplications.maplemobile.databinding.FragmentInventoryBinding
import com.bapplications.maplemobile.gameplay.player.inventory.Inventory
import com.bapplications.maplemobile.input.EventsQueue
import com.bapplications.maplemobile.input.events.*
import com.bapplications.maplemobile.ui.adapters.PageViewerInventoryAdapter


class InventoryFragment(inventory: Inventory) : Fragment(), EventListener {

    private val inventoryPagerAdapter = PageViewerInventoryAdapter(inventory)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding : FragmentInventoryBinding = FragmentInventoryBinding.inflate(inflater, container, false)

        EventsQueue.instance.registerListener(EventType.ItemDropped, this)
        EventsQueue.instance.registerListener(EventType.EquipItem, this)
        EventsQueue.instance.registerListener(EventType.UnequipItem, this)
        EventsQueue.instance.registerListener(EventType.PickupItem, this)

        binding.inventoryPager.adapter = inventoryPagerAdapter

        TabLayoutMediator(binding.selectedInventoryTab, binding.inventoryPager) { tab, position ->
            tab.text = when(position) {
                0 -> "Equip"
                1 -> "Use"
                2 -> "Set-Up"
                3 -> "Etc"
                4 -> "Cash"
                else -> ""
            }
        }.attach()

        return binding.root
    }

    override fun onEventReceive(event: Event) {
        activity?.runOnUiThread {
            // no matter what event is it just update the list
            inventoryPagerAdapter.notifyDataSetChanged()
        }
    }
}
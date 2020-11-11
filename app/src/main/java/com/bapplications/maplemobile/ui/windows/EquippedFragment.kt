package com.bapplications.maplemobile.ui.windows

import android.util.Log
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import com.bapplications.maplemobile.R
import androidx.fragment.app.viewModels
import androidx.databinding.DataBindingUtil
import androidx.viewpager2.widget.ViewPager2
import com.bapplications.maplemobile.input.events.*
import com.bapplications.maplemobile.input.EventsQueue
import com.bapplications.maplemobile.ui.view_models.EquippedViewModel
import com.bapplications.maplemobile.ui.adapters.PageViewerToolsAdapter
import com.bapplications.maplemobile.databinding.FragmentEquippedBinding
import com.bapplications.maplemobile.gameplay.player.inventory.EquipSlot
import com.bapplications.maplemobile.gameplay.player.inventory.EquippedInventory

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

        val slots =  listOf(binding.helmetSlot, binding.glassesSlot,
                binding.earringsSlot, binding.armorSlot,
                binding.pantsSlot, binding.capeSlot,
                binding.weaponSlot, binding.shoesSlot,
                binding.shieldSlot, binding.glovesSlot)

        slots.forEach {
            it.setOnClickListener{
                val slot = viewModel.getEquipBySlot(getSlotByValue(it)).value
                if(slot?.item != null) {
                    (activity?.supportFragmentManager
                            ?.findFragmentByTag("f" + 2) as ItemInfoFragment)
                            .setItem(slot)
                    activity?.findViewById<ViewPager2>(R.id.tools_window)?.currentItem = PageViewerToolsAdapter.WindowTool.ITEM_INFO.ordinal
                }
            }
        }
        return binding.root
    }

    private fun getSlotByValue(it: View): EquipSlot.Id {
        return when(it.id) {
            R.id.helmet_slot -> EquipSlot.Id.HAT
            R.id.glasses_slot -> EquipSlot.Id.EYEACC
            R.id.earrings_slot -> EquipSlot.Id.EARRINGS
            R.id.armor_slot -> EquipSlot.Id.TOP
            R.id.pants_slot -> EquipSlot.Id.BOTTOM
            R.id.cape_slot -> EquipSlot.Id.CAPE
            R.id.weapon_slot -> EquipSlot.Id.WEAPON
            R.id.shoes_slot -> EquipSlot.Id.SHOES
            R.id.shield_slot -> EquipSlot.Id.SHIELD
            R.id.gloves_slot -> EquipSlot.Id.GLOVES
            else -> EquipSlot.Id.NONE
        }
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
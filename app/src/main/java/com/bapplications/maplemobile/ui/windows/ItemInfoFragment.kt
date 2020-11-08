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
import com.bapplications.maplemobile.ui.GameActivity
import com.bapplications.maplemobile.ui.view_models.ItemInfoViewModel

class ItemInfoFragment(val player: Player, val inventorySlot: InventorySlot) : Fragment() {

    companion object {
        fun newInstance(player: Player, inventorySlot: InventorySlot) = ItemInfoFragment(player, inventorySlot)
    }

    private val viewModel: ItemInfoViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding : FragmentItemInfoBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_item_info, container, false)
        binding.viewModel = viewModel

        viewModel.inventorySlot.value = inventorySlot

        binding.returnBt.setOnClickListener{parentFragmentManager.popBackStack()}

        binding.itemInfoDropBt.setOnClickListener {
            (activity as GameActivity).runOnGLThread {
                if(player.dropItem(viewModel.inventorySlot.value!!)) {
                    (activity as GameActivity).runOnUiThread {
//                        adapter.notifyDataSetChanged()
//                        returnToInventory(binding)
                    }
                }
            }
        }

        binding.itemInfoEquipBt.setOnClickListener{
            (activity as GameActivity).runOnGLThread {
                if(player.changeEquip(viewModel.inventorySlot.value!!)) {
                    (activity as GameActivity).runOnUiThread {
//                        adapter.notifyDataSetChanged()
//                        returnToInventory(binding)
                    }
                }
            }
        }

        return binding.root
    }

}
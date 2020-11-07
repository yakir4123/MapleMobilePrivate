package com.bapplications.maplemobile.ui.windows

import onItemClick
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import com.bapplications.maplemobile.R
import androidx.fragment.app.viewModels
import androidx.databinding.DataBindingUtil
import com.bapplications.maplemobile.ui.GameActivity
import com.bapplications.maplemobile.utils.BindingUtils
import kotlinx.android.synthetic.main.fragment_inventory.*
import com.bapplications.maplemobile.gameplay.player.Player
import com.bapplications.maplemobile.ui.adapters.InventoryAdapter
import com.bapplications.maplemobile.gameplay.player.inventory.InventorySlot
import com.bapplications.maplemobile.databinding.FragmentInventoryBinding
import com.bapplications.maplemobile.ui.view_models.InventoryViewModel
import com.bapplications.maplemobile.gameplay.player.inventory.InventoryType

class InventoryFragment(private var player: Player) : Fragment() {

    private val viewModel: InventoryViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding : FragmentInventoryBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_inventory, container, false)
        binding.viewModel = viewModel
        binding.setLifecycleOwner { lifecycle }
        
        // for updates  in case i pick items and the inventory is open
        player.inventoryViewModel = viewModel
        viewModel.itemInventory.postValue(player.inventory.getInventory(viewModel.selectedInventoryType.value).items)
        viewModel.itemInventory.addSource(viewModel.selectedInventoryType) {
            value -> viewModel.itemInventory.postValue(player.inventory.getInventory(value).items)
        }
        viewModel.itemInventory

        val adapter = InventoryAdapter()
        binding.inventoryItemsRecycler.adapter = adapter

        viewModel.itemInventory.observe(viewLifecycleOwner, {
            loading_tv.visibility = View.GONE
            adapter.data = it
        })
        binding.inventoryItemsRecycler.onItemClick { recview, position, v ->
            val slot = (binding.inventoryItemsRecycler.adapter as InventoryAdapter).getSlot(position)
            if(slot.item != null) {
                openItem(binding, slot)
                binding.inventoryScreen.visibility = View.INVISIBLE
                binding.itemInfoScreen.visibility = View.VISIBLE
            }
        }

        binding.itemInfoReturnToInventory.setOnClickListener {
            returnToInventory(binding)
        }

        binding.itemInfoEquipBt.setOnClickListener{
            (activity as GameActivity).runOnGLThread {
                if(player.changeEquip(viewModel.inventorySlot.value!!)) {
                    (activity as GameActivity).runOnUiThread {
                        adapter.notifyDataSetChanged()
                        returnToInventory(binding)
                    }
                }
            }
        }

        binding.itemInfoDropBt.setOnClickListener {
            (activity as GameActivity).runOnGLThread {
                if(player.dropItem(viewModel.inventorySlot.value!!)) {
                    (activity as GameActivity).runOnUiThread {
                        adapter.notifyDataSetChanged()
                        returnToInventory(binding)
                    }
                }
            }
        }

        // swipes
        binding.inventoryItemsRecycler.setOnHorizonSwipe { isLeft -> inventorySwipe(isLeft) }
        viewModel.selectedInventoryType.observe(viewLifecycleOwner, {
            binding.inventoryTypeButtonsGroup.check(BindingUtils.inventoryTypeToButtonId(it))
        })
        return binding.root
    }

    private fun returnToInventory(binding: FragmentInventoryBinding) {
        binding.itemInfoScreen.visibility = View.GONE
        binding.inventoryScreen.visibility = View.VISIBLE
        closeItem(binding)
    }

    private fun openItem(binding: FragmentInventoryBinding, inventorySlot: InventorySlot) {
        binding.viewModel?.setInventorySlot(inventorySlot)
    }

    private fun closeItem(binding: FragmentInventoryBinding) {
        binding.viewModel?.setInventorySlot(null)
    }

    private fun inventorySwipe(isLeft : Boolean) {
        val nextType = InventoryType.Id.values()[viewModel.selectedInventoryType.value?.ordinal?.plus(if(isLeft) -1 else 1)!!]
        viewModel.setSelectedInventoryType(when(nextType) {
            InventoryType.Id.NONE -> InventoryType.Id.CASH
            InventoryType.Id.EQUIPPED -> InventoryType.Id.EQUIP
            else -> nextType
        })
    }

    override fun onDetach() {
        player.inventoryViewModel = null
        super.onDetach()
    }

    companion object {
        @JvmStatic
        fun newInstance(player: Player) =
                InventoryFragment(player)
    }
}
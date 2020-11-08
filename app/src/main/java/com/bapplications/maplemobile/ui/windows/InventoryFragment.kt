package com.bapplications.maplemobile.ui.windows

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bapplications.maplemobile.R
import com.bapplications.maplemobile.databinding.FragmentInventoryBinding
import com.bapplications.maplemobile.gameplay.player.Player
import com.bapplications.maplemobile.gameplay.player.inventory.InventoryType
import com.bapplications.maplemobile.ui.adapters.PageViewerInventoryAdapter
import com.bapplications.maplemobile.ui.view_models.InventoryViewModel
import com.google.android.material.tabs.TabLayoutMediator


class InventoryFragment(private var player: Player) : Fragment() {

    private val viewModel: InventoryViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding : FragmentInventoryBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_inventory, container, false)
        binding.viewModel = viewModel
        binding.setLifecycleOwner { lifecycle }
        
        // for updates  in case i pick items and the inventory is open
        player.inventoryViewModel = viewModel
        viewModel.itemInventory
                .postValue(player.inventory
                        .getInventory(viewModel.selectedInventoryType.value).items)
        viewModel.itemInventory.addSource(viewModel.selectedInventoryType) { value -> viewModel.itemInventory.postValue(player.inventory.getInventory(value).items)
        }
        viewModel.itemInventory

        binding.inventoryPager.apply {
            adapter = PageViewerInventoryAdapter(player.inventory)
        }

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

//        // Iterate over all tabs and set the custom view
//        for (i in 0 until binding.selectedInventoryTab.tabCount) {
//            val tab = binding.selectedInventoryTab.getTabAt(i)
//            tab?.customView = LayoutInflater.from(context)
//                    .inflate(R.layout.inventory_tab, null).apply {
//                        this.findViewById<TextView>(R.id.tab_title).text = tab?.text
//                    }
//        }

//        val adapter = RecyclerInventoryAdapter()
//        binding.inventoryItemsRecycler.adapter = adapter
//
//        viewModel.itemInventory.observe(viewLifecycleOwner, {
//            loading_tv.visibility = View.GONE
//            adapter.data = it
//        })
//        binding.inventoryItemsRecycler.onItemClick { recview, position, v ->
//            val slot = (binding.inventoryItemsRecycler.adapter as RecyclerInventoryAdapter).getSlot(position)
//            if(slot.item != null) {
//                val newFragment = ItemInfoFragment.newInstance(player, slot)
//                val transaction = parentFragmentManager.beginTransaction()
//                transaction.replace(R.id.tools_window, newFragment)
//                transaction.addToBackStack(null)
//                transaction.commit()
//            }
//        }
//
//        // swipes
//        binding.inventoryItemsRecycler.setOnHorizonSwipe { isLeft -> inventorySwipe(isLeft) }
//        viewModel.selectedInventoryType.observe(viewLifecycleOwner, {
//            binding.inventoryTypeButtonsGroup.check(BindingUtils.inventoryTypeToButtonId(it))
//        })
        return binding.root
    }

    private fun inventorySwipe(isLeft: Boolean) {
        val nextType = InventoryType.Id.values()[viewModel.selectedInventoryType.value?.ordinal?.plus(if (isLeft) -1 else 1)!!]
        viewModel.setSelectedInventoryType(when (nextType) {
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
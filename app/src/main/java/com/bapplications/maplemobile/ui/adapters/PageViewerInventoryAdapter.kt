package com.bapplications.maplemobile.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.findFragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bapplications.maplemobile.R
import com.bapplications.maplemobile.gameplay.player.inventory.Inventory
import com.bapplications.maplemobile.gameplay.player.inventory.InventoryType
import com.bapplications.maplemobile.ui.adapters.holders.InventoryRecyclerHolder
import com.bapplications.maplemobile.ui.windows.InventoryFragment
import com.bapplications.maplemobile.ui.windows.ItemInfoFragment
import kotlinx.android.synthetic.main.inventory_recyclerview.view.*
import onItemClick


class PageViewerInventoryAdapter( val inventory: Inventory) : RecyclerView.Adapter<InventoryRecyclerHolder>() {
    var context: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InventoryRecyclerHolder {
        context = parent.context
        val activity = parent.findFragment<InventoryFragment>().activity

        val view = LayoutInflater.from(parent.context).inflate(R.layout.inventory_recyclerview, parent, false)
        view.inventory_items_recycler.apply {
            adapter = RecyclerInventoryAdapter()
        }.also{
            // switch fragment and choose the item
            it.onItemClick { recview, position, v ->
                val slot = (recview.adapter as RecyclerInventoryAdapter).getSlot(position)
                if(slot.item != null) {
                    (activity?.supportFragmentManager
                            ?.findFragmentByTag("f" + 2) as ItemInfoFragment)
                            .setItem(slot)
                    activity.findViewById<ViewPager2>(R.id.tools_window).currentItem = PageViewerToolsAdapter.WindowTool.ITEM_INFO.ordinal
                }
            }
        }
        return InventoryRecyclerHolder(view)
    }

    override fun getItemCount(): Int {
        return InventoryType.Id.values().size - 2 // without equipped and NONE
    }

    override fun onBindViewHolder(holder: InventoryRecyclerHolder, position: Int) {
        holder.itemView.inventory_items_recycler.apply {
            adapter = RecyclerInventoryAdapter().apply {
                data = inventory.getInventory(InventoryType.Id.values()[position + 1]).items
            }
        }
    }

}
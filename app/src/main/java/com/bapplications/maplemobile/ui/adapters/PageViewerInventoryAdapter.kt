package com.bapplications.maplemobile.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bapplications.maplemobile.R
import com.bapplications.maplemobile.gameplay.player.inventory.Inventory
import com.bapplications.maplemobile.gameplay.player.inventory.InventoryType
import com.bapplications.maplemobile.ui.adapters.holders.InventoryRecyclerHolder
import kotlinx.android.synthetic.main.inventory_recyclerview.view.*


class PageViewerInventoryAdapter( val inventory: Inventory) : RecyclerView.Adapter<InventoryRecyclerHolder>() {
    var context: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InventoryRecyclerHolder {
        context = parent.context
        val view = LayoutInflater.from(parent.context).inflate(R.layout.inventory_recyclerview, parent, false)
        view.inventory_items_recycler.apply {
            adapter = RecyclerInventoryAdapter()
        }
        return InventoryRecyclerHolder(view)
    }

    override fun getItemCount(): Int {
        return InventoryType.Id.values().size - 2 // without equipped
    }

    override fun onBindViewHolder(holder: InventoryRecyclerHolder, position: Int) {
        holder.itemView.inventory_items_recycler.apply {
            adapter = RecyclerInventoryAdapter().apply {
                data = inventory.getInventory(InventoryType.Id.values()[position + 1]).items
            }
        }
    }

}
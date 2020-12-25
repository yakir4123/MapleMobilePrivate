package com.bapplications.maplemobile.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bapplications.maplemobile.R
import com.bapplications.maplemobile.gameplay.player.inventory.InventorySlot
import com.bapplications.maplemobile.utils.BindingUtils
import com.bapplications.maplemobile.ui.adapters.holders.ImageItemViewHolder
import com.bapplications.maplemobile.utils.setViewByItemId

class RecyclerInventoryAdapter: RecyclerView.Adapter<ImageItemViewHolder>() {
    var data =  mutableListOf<InventorySlot>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ImageItemViewHolder, position: Int) {
        val inventorySlot : InventorySlot = data[position]
        if (inventorySlot.itemId != 0) {
            setViewByItemId(holder.itemImage, BindingUtils.ItemTypeStat.ICON, inventorySlot.itemId)
            holder.itemCountText.text = inventorySlot.count.toString()
            holder.isCashIcon.visibility = if(inventorySlot.isCash) View.VISIBLE else View.GONE
        } else {
            holder.itemImage.setImageBitmap(null)
            holder.itemCountText.text = ""
            holder.isCashIcon.visibility = View.GONE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        val view = layoutInflater
                .inflate(R.layout.inventory_item_recyclerview, parent, false)

        return ImageItemViewHolder(view)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun getSlot(position: Int): InventorySlot {
        return data[position]
    }


}

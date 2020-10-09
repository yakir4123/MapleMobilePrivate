package com.bapplications.maplemobile.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bapplications.maplemobile.R
import com.bapplications.maplemobile.gameplay.player.inventory.Slot
import com.bapplications.maplemobile.views.BindingUtils
import com.bapplications.maplemobile.views.adapters.holders.ImageItemViewHolder
import com.bapplications.maplemobile.views.setViewByItemId

class InventoryAdapter: RecyclerView.Adapter<ImageItemViewHolder>() {
    var data =  mutableListOf<Slot>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ImageItemViewHolder, position: Int) {
        val slot : Slot = data[position]
        if (slot.itemId != 0) {
            setViewByItemId(holder.itemImage, BindingUtils.ItemTypeStat.ICON, slot.itemId)
            holder.itemCountText.text = slot.count.toString()
            holder.isCashIcon.visibility = if(slot.isCash) View.VISIBLE else View.GONE
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

    fun getSlot(position: Int): Slot {
        return data[position]
    }


}

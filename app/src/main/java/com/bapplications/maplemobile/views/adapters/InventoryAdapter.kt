package com.bapplications.maplemobile.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bapplications.maplemobile.R
import com.bapplications.maplemobile.gameplay.player.ItemData
import com.bapplications.maplemobile.gameplay.player.inventory.Slot
import com.bapplications.maplemobile.views.adapters.holders.ImageItemViewHolder

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
        val item : Slot = data[position]
        if (item.itemId != 0) {
            holder.itemImage.setImageBitmap(ItemData.get(item.itemId).icon(false))
            holder.itemCountText.text = item.count.toString()
            holder.isCashIcon.visibility = if(item.isCash) View.VISIBLE else View.GONE
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


}

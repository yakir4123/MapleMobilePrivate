package com.bapplications.maplemobile.views.adapters.holders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bapplications.maplemobile.R

class ImageItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

    val itemImage: ImageView = itemView.findViewById(R.id.game_item)
    val isCashIcon: ImageView = itemView.findViewById(R.id.is_cash_iv)
    val itemCountText: TextView = itemView.findViewById(R.id.item_count_tv)

}
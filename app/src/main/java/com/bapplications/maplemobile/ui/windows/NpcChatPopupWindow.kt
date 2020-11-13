package com.bapplications.maplemobile.ui.windows

import android.content.Context
import android.graphics.Bitmap
import android.media.Image
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.*
import com.bapplications.maplemobile.R
import com.bapplications.maplemobile.gameplay.map.map_objects.Npc
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class NpcChatPopupWindow(context: Context, npc: Npc) {

    init {
        val popupView = LayoutInflater.from(context).inflate(R.layout.popup_npc_chat, null)

        popupView.findViewById<ImageView>(R.id.npc_icon_window).setImageBitmap(npc.icon)
        popupView.findViewById<TextView>(R.id.npc_name_tv).text = npc.name
        // create the popup window
        val width = LinearLayout.LayoutParams.MATCH_PARENT
        val height = LinearLayout.LayoutParams.MATCH_PARENT
        val focusable = true // lets taps outside the popup also dismiss it

        val popupWindow = PopupWindow(popupView, width, height, focusable)

        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0)

        popupView.findViewById<Button>(R.id.cancel_button).setOnClickListener{ _ -> popupWindow.dismiss()}

    }
}
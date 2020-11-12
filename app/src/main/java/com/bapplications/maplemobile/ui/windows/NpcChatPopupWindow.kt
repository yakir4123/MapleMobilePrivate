package com.bapplications.maplemobile.ui.windows

import android.content.Context
import android.view.Gravity
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.LinearLayout
import android.view.LayoutInflater
import com.bapplications.maplemobile.R


class NpcChatPopupWindow(context: Context, npcid) {

    init {
        val popupView = LayoutInflater.from(context).inflate(R.layout.popup_npc_chat, null)

        // create the popup window
        val width = LinearLayout.LayoutParams.MATCH_PARENT
        val height = LinearLayout.LayoutParams.MATCH_PARENT
        val focusable = true // lets taps outside the popup also dismiss it

        val popupWindow = PopupWindow(popupView, width, height, focusable)

        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0)
    }
}
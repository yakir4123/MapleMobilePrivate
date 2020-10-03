package com.bapplications.maplemobile.views.popup

import EqualSpacingItemDecoration
import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnTouchListener
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.RadioGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.bapplications.maplemobile.R
import com.bapplications.maplemobile.StaticUtils
import com.bapplications.maplemobile.databinding.PopupInventoryBinding
import com.bapplications.maplemobile.gameplay.player.inventory.InventoryType
import com.bapplications.maplemobile.gameplay.player.inventory.Slot
import com.bapplications.maplemobile.views.GameActivity
import com.bapplications.maplemobile.views.GameActivityViewModel
import com.bapplications.maplemobile.views.adapters.InventoryAdapter


class InventoryPopup {

    private var popupView : View? = null
    private var popupWindow : PopupWindow? = null

    @SuppressLint("RtlHardcoded")
    fun showPopupWindow(view: View, activity: GameActivity) {



        //Create a View object yourself through inflater
        val inflater = view.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val binding: PopupInventoryBinding = PopupInventoryBinding.inflate(inflater)
        binding.viewModel = ViewModelProvider(activity)
                .get(GameActivityViewModel::class.java)
        popupView = binding.root

        //Specify the length and width through constants
        val width = LinearLayout.LayoutParams.WRAP_CONTENT
        val height = LinearLayout.LayoutParams.MATCH_PARENT

        //Make Inactive Items Outside Of PopupWindow
        val focusable = true

        //Create a window with our parameters
        popupWindow = PopupWindow(popupView, width, height, focusable)

        //Set the location of the window on the screen
        val location = StaticUtils.locateView(view);

        // todo:: change it spawn on a specific view
        val marginEnd = StaticUtils.convertDpToPixel(-360f, view.context)
        popupWindow?.showAtLocation(view, Gravity.NO_GRAVITY, (location.left + marginEnd).toInt(), location.top)

        popupView?.setOnTouchListener(OnTouchListener { _, _ -> //Close the window when clicked
            popupWindow?.dismiss()
            true
        })

        val manager = GridLayoutManager(popupView?.context, 6)
        binding.inventoryItemsRecycler.layoutManager = manager
        val adapter = InventoryAdapter()
        binding.inventoryItemsRecycler.adapter = adapter

        val spacing = StaticUtils.convertDpToPixel(8f, popupView?.context).toInt()
        binding.inventoryItemsRecycler.addItemDecoration(EqualSpacingItemDecoration(spacing, EqualSpacingItemDecoration.GRID))
        adapter.data = binding.viewModel?.itemInventory?.value as MutableList<Slot>
        binding.viewModel?.itemInventory?.observe(activity, Observer {
            it?.let {
                adapter.data = it
            }
        })
    }

    fun setOnClickListener(onClickListener: View.OnClickListener?) {
    }

    fun dismiss() = popupWindow?.dismiss()
}
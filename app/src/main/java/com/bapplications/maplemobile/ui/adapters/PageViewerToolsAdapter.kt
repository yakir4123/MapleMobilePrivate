package com.bapplications.maplemobile.ui.adapters


import android.widget.Button
import androidx.fragment.app.Fragment
import com.bapplications.maplemobile.R
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bapplications.maplemobile.gameplay.player.Player
import com.bapplications.maplemobile.ui.windows.EquippedFragment
import com.bapplications.maplemobile.ui.windows.InventoryFragment
import com.bapplications.maplemobile.ui.windows.ItemInfoFragment


class PageViewerToolsAdapter(activity: FragmentActivity, val player: Player) : FragmentStateAdapter(activity) {

    override fun createFragment(position: Int): Fragment {
        return when(WindowTool.values()[position]) {
            WindowTool.NONE -> Fragment()
            WindowTool.INVENTORY -> InventoryFragment(player.inventory)
            WindowTool.INVENTORY_ITEM_INFO -> ItemInfoFragment(player)
            WindowTool.EQUIPPED -> EquippedFragment(player.getEquippedInventory())
            WindowTool.EQUIPPED_ITEM_INFO -> ItemInfoFragment(player).apply {
                view?.findViewById<Button>(R.id.item_info_equip_bt)?.text = "Unequip"
            }
//            5 -> skill
//            6 -> stats
            else -> EquippedFragment(player.getEquippedInventory())
        }
    }


    override fun getItemCount(): Int = 7


    enum class WindowTool {
        NONE, INVENTORY, INVENTORY_ITEM_INFO, EQUIPPED, EQUIPPED_ITEM_INFO, SKILLS, STATS
    }
}
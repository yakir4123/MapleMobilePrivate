package com.bapplications.maplemobile.ui.adapters


import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bapplications.maplemobile.gameplay.player.Player
import com.bapplications.maplemobile.ui.windows.BlankFragment
import com.bapplications.maplemobile.ui.windows.EquippedFragment
import com.bapplications.maplemobile.ui.windows.InventoryFragment
import com.bapplications.maplemobile.ui.windows.ItemInfoFragment

class PageViewerToolsAdapter(activity: FragmentActivity, val player: Player) : FragmentStateAdapter(activity) {

    override fun createFragment(position: Int): Fragment {
        return when(WindowTool.values()[position]) {
            WindowTool.NONE -> BlankFragment()
            WindowTool.INVENTORY -> InventoryFragment(player.inventory)
            WindowTool.ITEM_INFO -> ItemInfoFragment(player)
            WindowTool.EQUIPPED -> EquippedFragment(player.getEquippedInventory())
            WindowTool.SKILLS -> EquippedFragment(player.getEquippedInventory())
            WindowTool.STATS -> EquippedFragment(player.getEquippedInventory())
        }
    }

    override fun getItemCount(): Int = WindowTool.values().size

    enum class WindowTool {
        NONE, INVENTORY, ITEM_INFO, EQUIPPED, SKILLS, STATS
    }

}
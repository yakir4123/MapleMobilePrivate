package com.bapplications.maplemobile.ui

import android.graphics.Bitmap
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseMethod
import com.bapplications.maplemobile.R
import com.bapplications.maplemobile.gameplay.player.PlayerStats
import com.bapplications.maplemobile.gameplay.player.inventory.EquipData
import com.bapplications.maplemobile.gameplay.player.inventory.EquipStat
import com.bapplications.maplemobile.gameplay.player.inventory.InventoryType
import com.bapplications.maplemobile.gameplay.player.inventory.ItemData

object BindingUtils {
    @InverseMethod("buttonIdToInventoryType")
    @JvmStatic fun inventoryTypeToButtonId(typeId: InventoryType.Id?): Int {
        return when (typeId) {
            InventoryType.Id.USE -> R.id.use_btn
            InventoryType.Id.ETC -> R.id.etc_btn
            InventoryType.Id.CASH -> R.id.cash_btn
            InventoryType.Id.SETUP -> R.id.setup_btn
            else -> R.id.equip_btn
        }
    }

    @JvmStatic fun buttonIdToInventoryType(selectedButtonId: Int): InventoryType.Id {
        return when (selectedButtonId) {
            R.id.use_btn -> InventoryType.Id.USE
            R.id.etc_btn -> InventoryType.Id.ETC
            R.id.cash_btn -> InventoryType.Id.CASH
            R.id.setup_btn -> InventoryType.Id.SETUP
            else -> InventoryType.Id.EQUIP
        }
    }


    enum class ItemTypeStat {
        NAME, DESC, ICON, REQ_LVL, REQ_STR, REQ_DEX, REQ_INT, REQ_LUK, REQ_FAME, CATEGORY, STR, DEX, INT, LUK, HP, MP, WATK, MAGIC, WDEF, MDEF, ACC, AVOID, HANDS, SPEED, JUMP
    }
}

@BindingAdapter("android:visibility")
fun setVisibility(view: View, visible: Boolean) {
    view.visibility = if (visible) View.VISIBLE else View.GONE
}

@BindingAdapter(value = ["itemTypeStat", "itemId"])
fun setViewByItemId(view: View, itemTypeStat: BindingUtils.ItemTypeStat, itemId: Int) {
//    val itemTypeStat = BindingUtils.ItemTypeStat.values()[itemTypeStatOrdinal]
    val raw: Any? = when(itemTypeStat) {
        BindingUtils.ItemTypeStat.NAME -> ItemData.get(itemId)?.name
        BindingUtils.ItemTypeStat.DESC -> ItemData.get(itemId)?.desc
        BindingUtils.ItemTypeStat.ICON -> ItemData.get(itemId)?.icon(false)
        BindingUtils.ItemTypeStat.REQ_LVL -> EquipData.get(itemId)?.getRequirment(PlayerStats.Id.LEVEL)
        BindingUtils.ItemTypeStat.REQ_STR -> EquipData.get(itemId)?.getRequirment(PlayerStats.Id.STR)
        BindingUtils.ItemTypeStat.REQ_DEX -> EquipData.get(itemId)?.getRequirment(PlayerStats.Id.DEX)
        BindingUtils.ItemTypeStat.REQ_INT -> EquipData.get(itemId)?.getRequirment(PlayerStats.Id.INT)
        BindingUtils.ItemTypeStat.REQ_LUK -> EquipData.get(itemId)?.getRequirment(PlayerStats.Id.LUK)
        BindingUtils.ItemTypeStat.REQ_FAME -> EquipData.get(itemId)?.getRequirment(PlayerStats.Id.FAME)
        BindingUtils.ItemTypeStat.CATEGORY -> view.context.resources.getString(R.string.item_info_category) + EquipData.get(itemId)?.category
        else -> setItemBonus(view, itemId, when(itemTypeStat) {
            BindingUtils.ItemTypeStat.STR -> R.string.item_info_str
            BindingUtils.ItemTypeStat.DEX -> R.string.item_info_dex
            BindingUtils.ItemTypeStat.INT -> R.string.item_info_int
            BindingUtils.ItemTypeStat.LUK -> R.string.item_info_luk
            BindingUtils.ItemTypeStat.HP -> R.string.item_info_hp
            BindingUtils.ItemTypeStat.MP -> R.string.item_info_mp
            BindingUtils.ItemTypeStat.WATK -> R.string.item_info_watk
            BindingUtils.ItemTypeStat.MAGIC -> R.string.item_info_magic
            BindingUtils.ItemTypeStat.WDEF -> R.string.item_info_wdef
            BindingUtils.ItemTypeStat.MDEF -> R.string.item_info_mdef
            BindingUtils.ItemTypeStat.ACC -> R.string.item_info_acc
            BindingUtils.ItemTypeStat.AVOID -> R.string.item_info_avoid
            BindingUtils.ItemTypeStat.HANDS -> R.string.item_info_hands
            BindingUtils.ItemTypeStat.SPEED -> R.string.item_info_speed
            else -> R.string.item_info_jump
        }, itemTypeStat)
    }

    when(raw) {
        is String, is Int -> (view as TextView).text = raw.toString()
        is Bitmap -> (view as ImageView).setImageBitmap(raw)
    }
}

fun setItemBonus(view :View, itemId : Int, @StringRes stringId : Int, stat: BindingUtils.ItemTypeStat) : String{
    val equipStat : EquipStat = EquipStat.values()[stat.ordinal - BindingUtils.ItemTypeStat.STR.ordinal]
    val value : Short? = EquipData.get(itemId)?.getDefaultStat(equipStat)
    if(value == 0.toShort()) {
        view.visibility = View.GONE
    } else {
        view.visibility = View.VISIBLE
        return view.context.resources.getString(stringId) + EquipData.get(itemId)?.getDefaultStat(equipStat)
    }
    return ""
}

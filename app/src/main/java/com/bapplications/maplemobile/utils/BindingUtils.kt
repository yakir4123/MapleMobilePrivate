package com.bapplications.maplemobile.utils

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.core.graphics.drawable.toBitmap
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.viewpager.widget.ViewPager
import com.bapplications.maplemobile.R
import com.bapplications.maplemobile.gameplay.player.PlayerViewModel
import com.bapplications.maplemobile.gameplay.player.inventory.EquipData
import com.bapplications.maplemobile.gameplay.player.inventory.EquipStat
import com.bapplications.maplemobile.gameplay.player.inventory.ItemData

object BindingUtils {

    enum class ItemTypeStat {
        NAME, DESC, ICON, REQ_LVL, REQ_STR, REQ_DEX, REQ_INT, REQ_LUK, REQ_FAME, CATEGORY, STR, DEX, INT, LUK, HP, MP, WATK, MAGIC, WDEF, MDEF, ACC, AVOID, HANDS, SPEED, JUMP
    }


    @JvmStatic fun jobIdToName(jobId: Int) : String{
        return when(jobId) {
            0   -> "Beginner"
            100 -> "Warrior"
            110 -> "Fighter"
            120 -> "Page"
            130 -> "Spearman"
            111 -> "Crusader"
            121 -> "White Knight"
            131 -> "Dragon Knight"
            112 -> "Hero"
            122 -> "Paladin"
            132 -> "Dark Knight"
            200 -> "Magician"
            210 -> "Fire/Poison Wizard"
            220 -> "Ice/Lightning Wizard"
            230 -> "Cleric"
            211 -> "Fire/Poison Mage"
            221 -> "Ice/Lightning Mage"
            231 -> "Priest"
            212 -> "Fire/Poison Arch Mage"
            222 -> "Ice/Lightning Arch Mage"
            232 -> "Bishop"
            300 -> "Bowman"
            310 -> "Hunter"
            320 -> "Crossbowman"
            311 -> "Ranger"
            321 -> "Sniper"
            312 -> "Bow Master"
            322 -> "Crossbow Master"
            400 -> "Thief"
            410 -> "Assassin"
            420 -> "Bandit"
            411 -> "Hermit"
            421 -> "Chief Bandit"
            412 -> "Nights Lord"
            422 -> "Shadower"
            500 -> "Pirate"
            510 -> "Brawler"
            520 -> "Gunslinger"
            511 -> "Marauder"
            521 -> "Outlaw"
            512 -> "Buccaneer"
            522 -> "Corsair"
            else -> "Whaaat?!"
        }
    }
}

@BindingAdapter("android:visibility")
fun setVisibility(view: View, visible: Boolean) {
    view.visibility = if (visible) View.VISIBLE else View.GONE
}

@BindingAdapter("itemTypeStat", "itemId", "itemDataDefault", requireAll = false)
fun setViewByItemId(view: View, itemTypeStat: BindingUtils.ItemTypeStat, itemId: Int, default: Any? = null) {
    val raw: Any? = when(itemTypeStat) {
        BindingUtils.ItemTypeStat.NAME -> ItemData.get(itemId)?.name
        BindingUtils.ItemTypeStat.DESC -> ItemData.get(itemId)?.desc
        BindingUtils.ItemTypeStat.ICON -> {
            if (itemId != 0)
                ItemData.get(itemId)?.icon(false)
            else
                (default as Drawable?)?.toBitmap()
        }
        BindingUtils.ItemTypeStat.REQ_LVL -> EquipData.get(itemId)?.getRequirment(PlayerViewModel.Id.LEVEL)
        BindingUtils.ItemTypeStat.REQ_STR -> EquipData.get(itemId)?.getRequirment(PlayerViewModel.Id.STR)
        BindingUtils.ItemTypeStat.REQ_DEX -> EquipData.get(itemId)?.getRequirment(PlayerViewModel.Id.DEX)
        BindingUtils.ItemTypeStat.REQ_INT -> EquipData.get(itemId)?.getRequirment(PlayerViewModel.Id.INT)
        BindingUtils.ItemTypeStat.REQ_LUK -> EquipData.get(itemId)?.getRequirment(PlayerViewModel.Id.LUK)
        BindingUtils.ItemTypeStat.REQ_FAME -> EquipData.get(itemId)?.getRequirment(PlayerViewModel.Id.FAME)
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

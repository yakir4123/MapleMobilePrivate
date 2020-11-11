package com.bapplications.maplemobile.gameplay.player.inventory

open class Item(val itemId: Int, private val expiration: Long, private val owner: String?, private val flags: Short) {
    val isEquip: Boolean
        get() = this is Equip
}
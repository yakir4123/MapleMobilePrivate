package com.bapplications.maplemobile.gameplay.player.inventory

class EquippedInventory : InventoryType(Id.EQUIPPED, EquipSlot.Id.values().size) {

    private var isInWearingProcess : Boolean = false

    fun equipItem(item: Equip): Equip? {
        isInWearingProcess = true
        val equipSlot = EquipData.get(item.itemId).eqSlot
        val removedEquip: Equip? = unequipItem(equipSlot)
        add(item, equipSlot.ordinal,  1)
        isInWearingProcess = false
        return removedEquip
    }

    fun unequipItem(equipSlot : EquipSlot.Id): Equip? {
        if(!isEmptySlot(equipSlot.ordinal))
            return popItem(equipSlot.ordinal).item as Equip
        return null
    }

    override fun add(item: Item?, count: Short): Int {
        if(!isInWearingProcess){
            throw IllegalAccessException("Not allowing Use add() In EquippedInventory")
        }
        return super.add(item, count)
    }

    operator fun get(equipSlot : EquipSlot.Id): InventorySlot? {
        return inventory[equipSlot.ordinal]
    }

}
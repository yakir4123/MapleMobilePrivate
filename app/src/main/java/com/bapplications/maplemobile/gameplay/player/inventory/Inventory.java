package com.bapplications.maplemobile.gameplay.player.inventory;

import com.bapplications.maplemobile.constatns.Configuration;

import java.util.EnumMap;

public class Inventory {

    private long gold;

    private EnumMap<InventoryType.Id, InventoryType> inventories = new EnumMap<>(InventoryType.Id.class);


    public Inventory() {
        gold = 0;
        inventories.put(InventoryType.Id.EQUIP, new InventoryType(InventoryType.Id.EQUIP, Configuration.INVENTORY_MAX_SLOTS));
        inventories.put(InventoryType.Id.USE, new InventoryType(InventoryType.Id.USE, Configuration.INVENTORY_MAX_SLOTS));
        inventories.put(InventoryType.Id.SETUP, new InventoryType(InventoryType.Id.SETUP, Configuration.INVENTORY_MAX_SLOTS));
        inventories.put(InventoryType.Id.ETC, new InventoryType(InventoryType.Id.ETC, Configuration.INVENTORY_MAX_SLOTS));
        inventories.put(InventoryType.Id.CASH, new InventoryType(InventoryType.Id.CASH, Configuration.INVENTORY_MAX_SLOTS));
        inventories.put(InventoryType.Id.EQUIPPED, new EquippedInventory());
    }

    public long getGold() {
        return gold;
    }

    public int addItem(Item item, short count)
    {
        return inventories.get(InventoryType.by_item_id(item.getItemId())).add(item, count);
    }

    public InventoryType getInventory(InventoryType.Id type) {
        return inventories.get(type);
    }

    public EquippedInventory getEquippedInventory() {
        return (EquippedInventory) inventories.get(InventoryType.Id.EQUIPPED);
    }

    public boolean equipItem(Equip item) {
        Equip equip = getEquippedInventory().equipItem(item);
        inventories.get(InventoryType.Id.EQUIP).add(equip);
        return true;
    }

    public boolean unequipItem(Equip item) {
        Equip equip = getEquippedInventory().unequipItem(EquipData.get(item.getItemId()).getEqSlot());
        inventories.get(InventoryType.Id.EQUIP).add(equip);
        return true;
    }


}

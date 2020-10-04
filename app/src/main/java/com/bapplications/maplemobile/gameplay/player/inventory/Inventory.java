package com.bapplications.maplemobile.gameplay.player.inventory;

import com.bapplications.maplemobile.constatns.Configuration;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.lifecycle.LiveData;

public class Inventory {

    private long gold;
    private Integer running_uid;

    private Map<Integer, Item> items = new HashMap<>();
    private Map<Integer, Equip> equips = new HashMap<>();
    private EnumMap<InventoryType.Id, InventoryType> inventories = new EnumMap<>(InventoryType.Id.class);

    public Inventory() {
        gold = 0;
        running_uid = 0;
        for(InventoryType.Id type: InventoryType.Id.values()){
            inventories.put(type, new InventoryType(type, Configuration.INVENTORY_MAX_SLOTS));
        }
    }

    public long getGold() {
        return gold;
    }


    public void addItem(InventoryType.Id invType, short slot, int itemId, long expire, short count, String owner, short flags)
    {
        items.put(addSlot(invType, slot, itemId, count),
                new Item(itemId, expire, owner, flags));
    }

    int addSlot(InventoryType.Id type, short slot, int itemId, short count)
    {
        inventories.get(type).put(slot, new Slot(slot, itemId, count));
        return slot;
    }

    public InventoryType getInventory(InventoryType.Id type) {
        return inventories.get(type);
    }


}

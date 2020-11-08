package com.bapplications.maplemobile.gameplay.player.inventory;

import java.util.ArrayList;

public class InventoryType {


    protected int maxSlots;
    protected InventoryType.Id type;
    protected ArrayList<InventorySlot> inventory;


    public InventoryType(InventoryType.Id type, int maxSlots) {
        this.type = type;
        inventory = new ArrayList<>();
        addSlots(maxSlots);
    }

    public void addSlots(int add) {
        for(int i = 0; i < add ; i++){
            inventory.add(maxSlots + i, new InventorySlot(type, i));
        }
        this.maxSlots += add;
    }

    public int add(Item item){
        return add(item, (short)1);
    }

    public int add(Item item, short count)
    {
        return add(item, getEmptySlot(), count);
    }

    protected int add(Item item, int slotid, short count) {
        if(slotid == -1 || count < 1) {
            return slotid;
        }
        InventorySlot inventorySlot = inventory.get(slotid);
        inventorySlot.item = item;
        inventorySlot.count = count;
        return slotid;
    }

    public ArrayList<InventorySlot> getItems() {
        return inventory;
    }

    public int getEmptySlot() {
        for(int i = 0; i < inventory.size() ; i++) {
            if (isEmptySlot(i)) {
                return i;
            }
        }
        return -1;
    }

    public boolean isEmptySlot(int slot){
        return inventory.get(slot).item == null;
    }

    public InventorySlot popItem(int slot) {
        InventorySlot res = inventory.get(slot);
        inventory.set(slot, new InventorySlot(type, res.slotid));
        return res;
    }

    public enum Id {
        // do not change the order!

        // I repeat DO NOT change the order
        // it is necessary for determine
        // the item type  if its id prefix with 1 its equip 2 for use and etc..
        NONE,
        EQUIP,
        USE,
        SETUP,
        ETC,
        CASH,
        EQUIPPED,
    }

    // Return the inventory type by item id
    public static Id by_item_id(int item_id) {

        int prefix = item_id / 1000000;

        return (prefix > Id.NONE.ordinal() && prefix <= Id.CASH.ordinal()) ? by_value(prefix) : Id.NONE;
    }

    // Return the inventory type by value
    public static Id by_value(int value) {

        return Id.values()[value];
    }

}

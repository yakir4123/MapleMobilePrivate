package com.bapplications.maplemobile.gameplay.player.inventory;

public class Slot {
    int unique_id;
    int itemId;
    short count;
    boolean cash;


    public Slot() {

    }

    public Slot(int unique_id, int itemId, short count, boolean cash) {
        this.unique_id = unique_id;
        this.itemId = itemId;
        this.count = count;
        this.cash = cash;
    }

    public int getItemId() {
        return itemId;
    }
}

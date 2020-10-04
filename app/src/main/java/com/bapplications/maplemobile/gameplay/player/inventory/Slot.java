package com.bapplications.maplemobile.gameplay.player.inventory;

import android.view.View;

import com.bapplications.maplemobile.gameplay.player.ItemData;

public class Slot {
    int unique_id;
    int itemId;
    short count;


    public Slot() {

    }

    public Slot(int unique_id, int itemId, short count) {
        this.unique_id = unique_id;
        this.itemId = itemId;
        this.count = count;
    }

    public int getItemId() {
        return itemId;
    }

    public short getCount() {
        return count;
    }

    public boolean isCash() {
        return ItemData.get(itemId).isCash();
    }
}

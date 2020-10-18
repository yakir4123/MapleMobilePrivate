package com.bapplications.maplemobile.gameplay.player.inventory;

public class Slot {
    int slotid;
    Item item;
    short count;

    public Slot(int slotid) { this.slotid = slotid;}

    public int getItemId() {
        if(item == null)
            return 0;
        return item.getItemId();
    }

    public short getCount() { return count;}

    public boolean isCash() {
        return ItemData.get(getItemId()).isCash();
    }

    public Item getItem() {
        return item;
    }

    public int getSlotId() {
        return slotid;
    }

    public boolean isEmpty() {
        return count == 0;
    }
}

package com.bapplications.maplemobile.gameplay.player.inventory;

public class InventorySlot {
    int slotid;
    Item item;
    short count;
    InventoryType.Id invType;

    public InventorySlot(InventoryType.Id invType, int slotid) {
        this.slotid = slotid;
        this.invType = invType;
    }

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

    public InventoryType.Id getInventoryType() {
        return invType;
    }
}

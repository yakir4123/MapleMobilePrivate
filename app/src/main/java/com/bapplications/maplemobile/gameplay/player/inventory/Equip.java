package com.bapplications.maplemobile.gameplay.player.inventory;

public class Equip extends Item{

    private byte slots;
    private byte upgrades;

    public Equip(int item_id, long expiration, String owner, short flags, byte slots, byte upgrades) {
        super(item_id, expiration, owner, flags);
        this.slots = slots;
        this.upgrades = upgrades;
    }

}

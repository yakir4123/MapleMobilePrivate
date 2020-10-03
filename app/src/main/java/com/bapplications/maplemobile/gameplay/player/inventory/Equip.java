package com.bapplications.maplemobile.gameplay.player.inventory;

public class Equip {

    private byte level;
    private byte slots;
    private int item_id;
    private short flags;
    private String owner;
    private long expiration;

    public Equip(int item_id, long expiration, String owner, short flags, byte slots, byte level) {
        this.owner = owner;
        this.flags = flags;
        this.slots = slots;
        this.level = level;
        this.item_id = item_id;
        this.expiration = expiration;
    }

}

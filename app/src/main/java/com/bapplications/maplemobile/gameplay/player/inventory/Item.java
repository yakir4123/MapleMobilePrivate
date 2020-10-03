package com.bapplications.maplemobile.gameplay.player.inventory;

public class Item {

    private int itemId;
    private short flags;
    private String owner;
    private long expiration;

    Item(int itemid, long expiration, String owner, short flags) {
        this.flags = flags;
        this.owner = owner;
        this.itemId = itemid;
        this.expiration = expiration;
    }
}

package com.bapplications.maplemobile.gameplay.player.inventory;

import android.view.View;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class InventoryType extends ViewModel {


    private int maxSlots;
    private InventoryType.Id type;
    private ArrayList<Slot> inventory;


    public InventoryType(InventoryType.Id type, int maxSlots) {
        this.type = type;
        inventory = new ArrayList<>();
        addSlots(maxSlots);
    }

    public void addSlots(int add) {
        for(int i = 0; i < add ; i++){
            inventory.add(maxSlots + i,new Slot());
        }
        this.maxSlots += add;
    }

    public void put(short slotId, Slot item) {
        inventory.set(slotId, item);
    }

    public ArrayList<Slot> getItems() {
        return inventory;
    }

    public enum Id {
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

    class  InventoryPosition
    {
        InventoryType.Id type;
        short slot;

        public InventoryPosition(InventoryType.Id type, short slot){
            this.type = type;
            this.slot = slot;
        }
    }
}

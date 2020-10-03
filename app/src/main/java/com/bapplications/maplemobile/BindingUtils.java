package com.bapplications.maplemobile;

import android.util.Log;
import android.view.View;

import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseMethod;
import androidx.recyclerview.widget.RecyclerView;
import com.bapplications.maplemobile.gameplay.player.inventory.InventoryType;

public class BindingUtils {

    @InverseMethod("buttonIdToInventoryType")
    public static int inventoryTypeToButtonId(InventoryType.Id typeId) {
        switch (typeId) {
            case USE:
                return R.id.use_btn;
            case ETC:
                return R.id.etc_btn;
            case CASH:
                return R.id.cash_btn;
            case SETUP:
                return R.id.setup_btn;
        }
        return R.id.equip_btn;
    }

    public static InventoryType.Id buttonIdToInventoryType(int selectedButtonId) {

        switch (selectedButtonId) {
            case R.id.use_btn:
                return InventoryType.Id.USE;
            case R.id.etc_btn:
                return InventoryType.Id.ETC;
            case R.id.cash_btn:
                return InventoryType.Id.CASH;
            case R.id.setup_btn:
                return InventoryType.Id.SETUP;
        }
        return InventoryType.Id.EQUIP;
    }

    @BindingAdapter("inventoryType")
    public static void setInventoryType(RecyclerView view, InventoryType.Id typeId){
    }
}

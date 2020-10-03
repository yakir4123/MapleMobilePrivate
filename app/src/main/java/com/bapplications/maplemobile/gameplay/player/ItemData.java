package com.bapplications.maplemobile.gameplay.player;


import android.graphics.Bitmap;

import com.bapplications.maplemobile.BoolPair;
import com.bapplications.maplemobile.constatns.Loaded;
import com.bapplications.maplemobile.gameplay.textures.Texture;
import com.bapplications.maplemobile.pkgnx.NXNode;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class ItemData {


    private int price;
    private int itemid;
    private String name;
    private String desc;
    private byte gender;
    private boolean valid;
    private boolean unique;
    private String category;
    private boolean cashitem;
    private boolean untradable;
    private boolean unsellable;
    private BoolPair<Bitmap> icons = new BoolPair<>();

    private static Map<Integer, ItemData> cache = new HashMap<>();

    // Return a ref to the game object with the specified id.
    // If the object is not in cache, it is created.
    public static ItemData get(int id) {
        ItemData res = cache.get(id);

        if (res == null){
            res = new ItemData(id);
            cache.put(id, res);
        }
        return res;
    }


    private ItemData(int id)
    {
        itemid = id;
        unique = false;
        cashitem = false;
        untradable = false;
        unsellable = false;
        gender = 0;

        NXNode src = null;
        NXNode strsrc = null;

        String strprefix = "0" + getItemPrefix(itemid);
        String strid = "0" + itemid;
        int prefix = getPrefix(itemid);

        switch (prefix)
        {
            case 1:
                category = getEqCategory(itemid);
                src = Loaded.getFile(Loaded.WzFileName.CHARACTER).getRoot()
                        .getChild(category).getChild(strid + ".img").getChild("info");
                strsrc = Loaded.getFile(Loaded.WzFileName.CHARACTER).getRoot()
                        .getChild("Eqp.img").getChild("Eqp").getChild(category)
                        .getChild("" + itemid);
                break;
            case 2:
//                category = "Consume";
//                src = nl::nx::item["Consume"][strprefix + ".img"][strid]["info"];
//                strsrc = nl::nx::string["Consume.img"][std::to_string(itemid)];
                break;
            case 3:
//                category = "Install";
//                src = nl::nx::item["Install"][strprefix + ".img"][strid]["info"];
//                strsrc = nl::nx::string["Ins.img"][std::to_string(itemid)];
                break;
            case 4:
//                category = "Etc";
//                src = nl::nx::item["Etc"][strprefix + ".img"][strid]["info"];
//                strsrc = nl::nx::string["Etc.img"]["Etc"][std::to_string(itemid)];
                break;
            case 5:
//                category = "Cash";
//                src = nl::nx::item["Cash"][strprefix + ".img"][strid]["info"];
//                strsrc = nl::nx::string["Cash.img"][std::to_string(itemid)];
                break;
        }

        if (src == null || src.isNotExist()) {
            valid = false;
            return;
        }
        icons.setOnFalse(src.getChild("icon").get(null));
        icons.setOnTrue(src.getChild("iconRaw").get(null));
        price = src.getChild("price").get(1L).intValue();
        untradable = src.getChild("tradeBlock").getBool();
        unique = src.getChild("only").getBool();
        unsellable = src.getChild("notSale").getBool();
        cashitem = src.getChild("cash").getBool();
        gender = getItemGender(itemid);

        name = strsrc.getChild("name").get("");
        desc = strsrc.getChild("desc").get("");

        valid = true;
    }

    private static String[] categorynames =
        {
                "Cap",
                "Accessory",
                "Accessory",
                "Accessory",
                "Coat",
                "Longcoat",
                "Pants",
                "Shoes",
                "Glove",
                "Shield",
                "Cape",
                "Ring",
                "Accessory",
                "Accessory",
                "Accessory"
        };
    private String getEqCategory(int id) {

        int index = getItemPrefix(id) - 100;
        if (index < 15)
            return categorynames[index];
        else if (index >= 30 && index <= 70)
            return "Weapon";
        else
            return "";
    }

    private byte getItemGender(int id) {

		int item_prefix = getItemPrefix(id);

        if ((getPrefix(id) != 1 && item_prefix != 254) || item_prefix == 119 || item_prefix == 168)
            return 2;

		int gender_digit = id / 1000 % 10;

        return (byte) ((gender_digit > 1) ? 2 : gender_digit);
    }

    private int getPrefix(int id) {
        return id / 1000000;
    }

    private int getItemPrefix(int id) {
        return id / 10000;
    }

    public String getCategory() {
        return category;
    }

    @Nullable
    public Bitmap icon(boolean raw) {
        return icons.get(raw);
    }
}

package com.bapplications.maplemobile.gameplay.player.inventory;


import android.graphics.Bitmap;

import com.bapplications.maplemobile.utils.BoolPair;
import com.bapplications.maplemobile.constatns.Loaded;
import com.bapplications.maplemobile.pkgnx.NXNode;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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

    protected static Map<Integer, ItemData> cache = new HashMap<>();

    // Return a ref to the game object with the specified id.
    // If the object is not in cache, it is created.
    public static ItemData get(int id) {
        if(!isItem(id)){
            return null;
        }
        ItemData res = cache.get(id);
        if (res == null){
            if(getPrefix(id) == 1) {
                res = new EquipData(id);
            } else {
                res = new ItemData(id);
            }
            cache.put(id, res);
        }
        return res;
    }


    protected ItemData(int id)
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
                strsrc = Loaded.getFile(Loaded.WzFileName.STRING).getRoot()
                        .getChild("Eqp.img").getChild("Eqp").getChild(category)
                        .getChild(itemid);
                break;
            case 2:
                category = "Consume";
                src = Loaded.getFile(Loaded.WzFileName.ITEM).getRoot().getChild("Consume")
                        .getChild(strprefix + ".img").getChild(strid).getChild("info");
                strsrc = Loaded.getFile(Loaded.WzFileName.STRING).getRoot()
                        .getChild("Consume.img").getChild(itemid);
                break;
            case 3:
                category = "Install";
                src = Loaded.getFile(Loaded.WzFileName.ITEM).getRoot().getChild("Install")
                        .getChild(strprefix + ".img").getChild(strid).getChild("info");
                strsrc = Loaded.getFile(Loaded.WzFileName.STRING).getRoot()
                        .getChild("Ins.img").getChild(itemid);
                break;
            case 4:
                category = "Etc";
                src = Loaded.getFile(Loaded.WzFileName.ITEM).getRoot().getChild("Etc")
                        .getChild(strprefix + ".img").getChild(strid).getChild("info");
                strsrc = Loaded.getFile(Loaded.WzFileName.STRING).getRoot()
                        .getChild("Etc.img").getChild("Etc").getChild(itemid);
                break;
            case 5:
                if(isPet(itemid)){
                    category = "Pet";
                    src = Loaded.getFile(Loaded.WzFileName.ITEM).getRoot().getChild("Pet")
                            .getChild(itemid + ".img").getChild("info");
                    strsrc = Loaded.getFile(Loaded.WzFileName.STRING).getRoot()
                            .getChild("Pet.img").getChild(itemid);
                } else {
                    category = "Cash";
                    src = Loaded.getFile(Loaded.WzFileName.ITEM).getRoot().getChild("Cash")
                            .getChild(strprefix + ".img").getChild(strid).getChild("info");
                    strsrc = Loaded.getFile(Loaded.WzFileName.STRING).getRoot()
                            .getChild("Cash.img").getChild(itemid);
                }
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

        name = strsrc.getChild("name").get("").replace("\\n", Objects.requireNonNull(System.getProperty("line.separator")));
        desc = strsrc.getChild("desc").get("").replace("\\n", Objects.requireNonNull(System.getProperty("line.separator")));

        valid = true;
    }

    public boolean isPet(int itemid) {
        return itemid / 10000 == 500;
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

    private static int getPrefix(int id) {
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

    public boolean isCash() {
        return cashitem;
    }

    public int getPrice() {
        return price;
    }

    public int getItemid() {
        return itemid;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public byte getGender() {
        return gender;
    }

    public boolean isValid() {
        return valid;
    }

    public boolean isUnique() {
        return unique;
    }

    public boolean isCashitem() {
        return cashitem;
    }

    public boolean isUntradable() {
        return untradable;
    }

    public boolean isUnsellable() {
        return unsellable;
    }

    public BoolPair<Bitmap> getIcons() {
        return icons;
    }

    public static String[] getCategorynames() {
        return categorynames;
    }

    public static boolean isEquip(int itemId) {
        return getPrefix(itemId) == 1;
    }

    public static boolean isItem(int itemId) {
        int prefix = getPrefix(itemId);
        return prefix >= 1 && prefix <= 5;
    }
}

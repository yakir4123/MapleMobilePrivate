package com.bapplications.maplemobile.gameplay.player;

import com.bapplications.maplemobile.constatns.Loaded;
import com.bapplications.maplemobile.pkgnx.NXNode;

import java.util.HashMap;
import java.util.Map;

public class EquipData {

    private static Map<Integer, EquipData> cache = new HashMap<>();

    // Return a ref to the game object with the specified id.
    // If the object is not in cache, it is created.
    public static EquipData get(int id) {
        EquipData res = cache.get(id);

        if (res == null){
            res = new EquipData(id);
            cache.put(id, res);
        }
            return res;
    }

    private byte slots;
    private String type;
    private boolean cash;
    private ItemData itemData;
    private boolean tradeblock;
    private EquipSlot.Id eqSlot;

    private EquipData(int id)
    {
        itemData = ItemData.get(id);
        String strid = "0" + id;
        String category = itemData.getCategory();
        NXNode src = Loaded.getFile(Loaded.WzFileName.CHARACTER).getRoot()
                .getChild(category).getChild(strid + ".img").getChild("info");

        cash = src.getChild("cash").getBool();
        tradeblock = src.getChild("tradeBlock").getBool();
        slots = src.getChild("tuc").get(0L).byteValue();

//        reqstats[MapleStat.Id.LEVEL] = src["reqLevel"];
//        reqstats[MapleStat.Id.JOB] = src["reqJob"];
//        reqstats[MapleStat.Id.STR] = src["reqSTR"];
//        reqstats[MapleStat.Id.DEX] = src["reqDEX"];
//        reqstats[MapleStat.Id.INT] = src["reqINT"];
//        reqstats[MapleStat.Id.LUK] = src["reqLUK"];
//        defstats[EquipStat.Id.STR] = src["incSTR"];
//        defstats[EquipStat.Id.DEX] = src["incDEX"];
//        defstats[EquipStat.Id.INT] = src["incINT"];
//        defstats[EquipStat.Id.LUK] = src["incLUK"];
//        defstats[EquipStat.Id.WATK] = src["incPAD"];
//        defstats[EquipStat.Id.WDEF] = src["incPDD"];
//        defstats[EquipStat.Id.MAGIC] = src["incMAD"];
//        defstats[EquipStat.Id.MDEF] = src["incMDD"];
//        defstats[EquipStat.Id.HP] = src["incMHP"];
//        defstats[EquipStat.Id.MP] = src["incMMP"];
//        defstats[EquipStat.Id.ACC] = src["incACC"];
//        defstats[EquipStat.Id.AVOID] = src["incEVA"];
//        defstats[EquipStat.Id.HANDS] = src["incHANDS"];
//        defstats[EquipStat.Id.SPEED] = src["incSPEED"];
//        defstats[EquipStat.Id.JUMP] = src["incJUMP"];

        int WEAPON_TYPES = 20;
        int NON_WEAPON_TYPES = 15;
        int WEAPON_OFFSET = NON_WEAPON_TYPES + 15;
        int index = (id / 10000) - 100;

        if (index < NON_WEAPON_TYPES)
        {
            String[] types =
                {
                        "HAT",
                        "FACE ACCESSORY",
                        "EYE ACCESSORY",
                        "EARRINGS",
                        "TOP",
                        "OVERALL",
                        "BOTTOM",
                        "SHOES",
                        "GLOVES",
                        "SHIELD",
                        "CAPE",
                        "RING",
                        "PENDANT",
                        "BELT",
                        "MEDAL"
                };

            EquipSlot.Id[] equipslots =
            {
                EquipSlot.Id.HAT,
                EquipSlot.Id.FACE,
                EquipSlot.Id.EYEACC,
                EquipSlot.Id.EARACC,
                EquipSlot.Id.TOP,
                EquipSlot.Id.TOP,
                EquipSlot.Id.BOTTOM,
                EquipSlot.Id.SHOES,
                EquipSlot.Id.GLOVES,
                EquipSlot.Id.SHIELD,
                EquipSlot.Id.CAPE,
                EquipSlot.Id.RING1,
                EquipSlot.Id.PENDANT1,
                EquipSlot.Id.BELT,
                EquipSlot.Id.MEDAL
            };

            type = types[index];
            eqSlot = equipslots[index];
        }
        else if (index >= WEAPON_OFFSET && index < WEAPON_OFFSET + WEAPON_TYPES)
        {
            String[] types =
                {
                        "ONE-HANDED SWORD",
                        "ONE-HANDED AXE",
                        "ONE-HANDED MACE",
                        "DAGGER",
                        "", "", "",
                        "WAND",
                        "STAFF",
                        "",
                        "TWO-HANDED SWORD",
                        "TWO-HANDED AXE",
                        "TWO-HANDED MACE",
                        "SPEAR",
                        "POLEARM",
                        "BOW",
                        "CROSSBOW",
                        "CLAW",
                        "KNUCKLE",
                        "GUN"
                };

            int weaponindex = index - WEAPON_OFFSET;
            type = types[weaponindex];
            eqSlot = EquipSlot.Id.WEAPON;
        }
        else
        {
            type = "CASH";
            eqSlot = EquipSlot.Id.NONE;
        }
    }

    public EquipSlot.Id getEqSlot() {
        return eqSlot;
    }

    public ItemData getItemData() {
        return itemData;
    }
}

package com.bapplications.maplemobile.gameplay.player.inventory;

import com.bapplications.maplemobile.constatns.Loaded;
import com.bapplications.maplemobile.gameplay.player.PlayerViewModel;
import com.bapplications.maplemobile.pkgnx.NXNode;

import java.util.EnumMap;

public class EquipData extends ItemData {

    private byte slots;
    private String type;
    private EquipSlot.Id eqSlot;
    private EnumMap<PlayerViewModel.Id, Short> reqStats;
    private EnumMap<EquipStat, Short> defStats;

    public static EquipData get(int id) {
        if(!ItemData.isEquip(id)){
            return null;
        }
        EquipData res = (EquipData) cache.get(id);
        if (res == null){
            res = new EquipData(id);
            cache.put(id, res);
        }
        return res;
    }

    public EquipData(int id)
    {
        super(id);
        String strid = "0" + id;
        String category = super.getCategory();
        NXNode src = Loaded.getFile(Loaded.WzFileName.CHARACTER).getRoot()
                .getChild(category).getChild(strid + ".img").getChild("info");

        slots = src.getChild("tuc").get(0L).byteValue();

        reqStats = new EnumMap<>(PlayerViewModel.Id.class);
        reqStats.put(PlayerViewModel.Id.LEVEL, src.getChild("reqLevel").get(0L).shortValue());
        reqStats.put(PlayerViewModel.Id.JOB, src.getChild("reqJob").get(0L).shortValue());
        reqStats.put(PlayerViewModel.Id.STR, src.getChild("reqSTR").get(0L).shortValue());
        reqStats.put(PlayerViewModel.Id.DEX, src.getChild("reqDEX").get(0L).shortValue());
        reqStats.put(PlayerViewModel.Id.INT, src.getChild("reqINT").get(0L).shortValue());
        reqStats.put(PlayerViewModel.Id.LUK, src.getChild("reqLUK").get(0L).shortValue());

        defStats = new EnumMap<>(EquipStat.class);
        defStats.put(EquipStat.STR, src.getChild("incSTR").get(0L).shortValue());
        defStats.put(EquipStat.DEX, src.getChild("incDEX").get(0L).shortValue());
        defStats.put(EquipStat.INT, src.getChild("incINT").get(0L).shortValue());
        defStats.put(EquipStat.LUK, src.getChild("incLUK").get(0L).shortValue());
        defStats.put(EquipStat.WATK, src.getChild("incPAD").get(0L).shortValue());
        defStats.put(EquipStat.WDEF, src.getChild("incPDD").get(0L).shortValue());
        defStats.put(EquipStat.MAGIC, src.getChild("incMAD").get(0L).shortValue());
        defStats.put(EquipStat.MDEF, src.getChild("incMDD").get(0L).shortValue());
        defStats.put(EquipStat.HP, src.getChild("incMHP").get(0L).shortValue());
        defStats.put(EquipStat.MP, src.getChild("incMMP").get(0L).shortValue());
        defStats.put(EquipStat.ACC, src.getChild("incACC").get(0L).shortValue());
        defStats.put(EquipStat.AVOID, src.getChild("incEVA").get(0L).shortValue());
        defStats.put(EquipStat.HANDS, src.getChild("incHANDS").get(0L).shortValue());
        defStats.put(EquipStat.SPEED, src.getChild("incSPEED").get(0L).shortValue());
        defStats.put(EquipStat.JUMP, src.getChild("incJUMP").get(0L).shortValue());

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
                EquipSlot.Id.EARRINGS,
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

    public short getDefaultStat(EquipStat stat) {
        return defStats.get(stat);
    }

    public int getRequirment(PlayerViewModel.Id requirement_stat) {
        try {
            return reqStats.get(requirement_stat);
        } catch (NullPointerException e) {
            return 0;
        }
    }
}
package com.bapplications.maplemobile.gameplay.player.look;

import com.bapplications.maplemobile.gameplay.player.inventory.EquipData;
import com.bapplications.maplemobile.gameplay.player.inventory.EquipSlot;
import com.bapplications.maplemobile.gameplay.player.Stance;
import com.bapplications.maplemobile.utils.DrawArgument;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class CharEquips {

    private EnumMap<EquipSlot.Id, Clothing> clothes = new EnumMap<>(EquipSlot.Id.class);
    private static Map<Integer, Clothing> clothCache = new HashMap<>();

    enum CapType
    {
        NONE,
        HEADBAND,
        HAIRPIN,
        HALFCOVER,
        FULLCOVER
    };


    public CharEquips()
    {
        for (EquipSlot.Id id : clothes.keySet())
            clothes.put(id, null);
    }

    public void draw(EquipSlot.Id slot, Stance.Id stance, Clothing.Layer layer, byte frame, DrawArgument args)
    {
        Clothing cloth = clothes.get(slot);
        if (cloth != null)
            cloth.draw(stance, layer, frame, args);
    }

    public void addEquip(Integer itemid, BodyDrawInfo drawInfo) {
        if (itemid <= 0)
            return;

        Clothing cloth = clothCache.get(itemid);

        if (cloth == null)
        {
            clothCache.put(itemid, new Clothing(itemid, drawInfo));
            cloth = clothCache.get(itemid);
        }

        EquipSlot.Id slot = cloth.getEqSlot();
        clothes.put(slot, cloth);
    }

    public void removeEquip(Integer itemid, BodyDrawInfo drawInfo) {
        if (itemid <= 0)
            return;

        EquipSlot.Id slot = EquipData.get(itemid).getEqSlot();
        clothes.remove(slot);

    }


    public boolean hasOverall() {
        return getEquip(EquipSlot.Id.TOP) / 10000 == 105;
    }

    private int getEquip(EquipSlot.Id slot) {
        Clothing cloth = clothes.get(slot);
        if (cloth != null)
            return cloth.getId();
		else
            return 0;
    }

    public boolean isTwoHanded() {
        Clothing weapon = clothes.get(EquipSlot.Id.WEAPON);
        if (weapon != null)
            return weapon.isTwoHanded();
		else
            return false;
    }

    public CapType getCapType() {
        Clothing cap = clothes.get(EquipSlot.Id.HAT);
        if (cap != null)
        {
			String vslot = cap.getVSlot();
            if (vslot.equals("CpH1H5"))
                return CapType.HALFCOVER;
			else if (vslot.equals("CpH1H5AyAs") || vslot.equals("CpH1H2H3H4H5HfHsHbAe"))
                return CapType.FULLCOVER;
			else if (vslot.equals("CpH5"))
                return CapType.HEADBAND;
        }
        return CapType.NONE;
    }
}

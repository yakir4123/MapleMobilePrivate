package com.bapplications.maplemobile.gameplay.player;

import com.bapplications.maplemobile.constatns.Loaded;
import com.bapplications.maplemobile.gameplay.player.inventory.EquipData;
import com.bapplications.maplemobile.gameplay.player.inventory.ItemData;
import com.bapplications.maplemobile.gameplay.textures.Texture;
import com.bapplications.maplemobile.opengl.utils.DrawArgument;
import com.bapplications.maplemobile.opengl.utils.Point;
import com.bapplications.maplemobile.pkgnx.NXNode;
import com.bapplications.maplemobile.pkgnx.nodes.NXBitmapNode;
import com.bapplications.maplemobile.pkgnx.nodes.NXPointNode;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Clothing {

    private int itemid;
    private String vslot;
    private Stance.Id walk;
    private Stance.Id stand;
    private boolean twohanded;
    private boolean transparent;
    private EquipSlot.Id eqSlot;

    private EnumMap<Stance.Id, EnumMap<Layer, HashMap<Byte, Set<Texture>>>> stances;

    public Clothing(int itemid, BodyDrawInfo drawInfo) {
        this.itemid = itemid;
        stances = new EnumMap<>(Stance.Id.class);

        EquipData equipData = (EquipData) ItemData.get(itemid);
        eqSlot = equipData.getEqSlot();
        if (eqSlot == EquipSlot.Id.WEAPON) {
//            twohanded = WeaponData.get(itemid).isTwoHanded();
        } else {
            twohanded = false;
        }

        int NON_WEAPON_TYPES = 15;
        int WEAPON_OFFSET = NON_WEAPON_TYPES + 15;
        int WEAPON_TYPES = 20;

        Clothing.Layer[] layers = {
            Clothing.Layer.CAP,
            Clothing.Layer.FACEACC,
            Clothing.Layer.EYEACC,
            Clothing.Layer.EARRINGS,
            Clothing.Layer.TOP,
            Clothing.Layer.MAIL,
            Clothing.Layer.PANTS,
            Clothing.Layer.SHOES,
            Clothing.Layer.GLOVE,
            Clothing.Layer.SHIELD,
            Clothing.Layer.CAPE,
            Clothing.Layer.RING,
            Clothing.Layer.PENDANT,
            Clothing.Layer.BELT,
            Clothing.Layer.MEDAL
        };

        Clothing.Layer chlayer;
        int index = (itemid / 10000) - 100;

        if (index < NON_WEAPON_TYPES)
            chlayer = layers[index];
        else if (index >= WEAPON_OFFSET && index < WEAPON_OFFSET + WEAPON_TYPES)
            chlayer = Clothing.Layer.WEAPON;
		else
            chlayer = Clothing.Layer.CAPE;

        String strid = "0" + itemid;
        String category = equipData.getCategory();
        NXNode src = Loaded.getFile(Loaded.WzFileName.CHARACTER).getRoot().getChild(category).getChild(strid + ".img");
        NXNode info = src.getChild("info");

        vslot = info.getChild("vslot").get("");
        int standno = info.getChild("stand").get(0L).intValue();
        switch (standno)
        {
            case 1:
                stand = Stance.Id.STAND1;
                break;
            case 2:
                stand = Stance.Id.STAND2;
                break;
            default:
                stand = twohanded ? Stance.Id.STAND2 : Stance.Id.STAND1;
                break;
        }
        int walkno = info.getChild("walk").get(0L).intValue();
        switch (walkno)
        {
            case 1:
                walk = Stance.Id.WALK1;
                break;
            case 2:
                walk = Stance.Id.WALK2;
                break;
            default:
                walk = twohanded ? Stance.Id.WALK2 : Stance.Id.WALK1;
                break;
        }

        for (Stance.Id stance : Stance.mapStances.keySet())
        {
			String stancename = Stance.mapStances.get(stance);

            NXNode stancenode = src.getChild(stancename);

            if (stancenode.isNotExist())
                continue;

            NXNode framenode = stancenode.getChild(0);
            for (byte frame = 0; framenode.isExist(); ++frame)
            {
                framenode = stancenode.getChild(frame);
                for (NXNode partnode : framenode)
                {
                    String part = partnode.getName();

                    if (partnode.isNotExist() || !(partnode instanceof NXBitmapNode))
                        continue;

                    Clothing.Layer z;
                    String zs = partnode.getChild("z").get("");

                    if (part.equals("mailArm"))
                    {
                        z = Clothing.Layer.MAILARM;
                    }
                    else
                    {
                        z = sublayerNames.get(zs);
                        if(z == null) {
                            z = chlayer;
                        }
                    }

                    String parent = "";
                    Point parentpos = new Point();

                    for (NXNode mapnode : partnode.getChild("map"))
                    {
                        if (mapnode instanceof NXPointNode)
                        {
                            parent = mapnode.getName();
                            parentpos = new Point(mapnode);
                        }
                    }

                    NXNode mapnode = partnode.getChild("map");
                    Point shift = new Point();

                    switch (eqSlot)
                    {
                        case FACE:
                            shift = parentpos.negateSign();
                            break;
                        case SHOES:
                        case GLOVES:
                        case TOP:
                        case BOTTOM:
                        case CAPE:
                            shift = drawInfo.getBodyPosition(stance, (byte) frame).minus(parentpos);
                            break;
                        case HAT:
                        case EARACC:
                        case EYEACC:
//                            shift = drawInfo.getFacePos(stance, (byte) frame).minus(parentpos);
                            shift = drawInfo.getHairPos(stance, (byte) frame).minus(parentpos);
                            break;
                        case SHIELD:
                        case WEAPON:
                            switch (parent) {
                                case "handMove":
                                    shift.offset(drawInfo.getHandPosition(stance, (byte) frame));
                                    break;
                                case "hand":
                                    shift.offset(drawInfo.getArmPosition(stance, (byte) frame));
                                    break;
                                case "navel":
                                    shift.offset(drawInfo.getBodyPosition(stance, (byte) frame));
                                    break;
                            }
                            shift.offset(parentpos.negateSign());
                            break;
                    }

                    Texture tex = new Texture(partnode);
                    tex.shift(shift);
                    if(stances.get(stance) == null){
                        stances.put(stance, new EnumMap<>(Layer.class));
                    }
                    if(stances.get(stance).get(z) == null){
                        stances.get(stance).put(z, new HashMap<>());
                    }
                    if(stances.get(stance).get(z).get(frame) == null){
                        stances.get(stance).get(z).put((byte) frame, new HashSet<>());
                    }
                    stances.get(stance).get(z).get((byte) frame).add(tex);
                }
            }
        }

        transparent = transparents_items.contains(itemid);
    }



    public void draw(Stance.Id stance, Layer layer, byte frame, DrawArgument args)
    {
        HashMap<Byte, Set<Texture>> layers = stances.get(stance).get(layer);
        if(layers == null)
            return;

        for (Texture tex : layers.get(frame))
            tex.draw(args);
    }

    public EquipSlot.Id getEqSlot() {
        return eqSlot;
    }

    public int getId() {
        return itemid;
    }

    public boolean isTwoHanded() {
        return twohanded;
    }

    public String getVSlot() {
        return vslot;
    }

    public enum Layer
    {
        CAPE,
        SHOES,
        PANTS,
        TOP,
        MAIL,
        MAILARM,
        EARRINGS,
        FACEACC,
        EYEACC,
        PENDANT,
        BELT,
        MEDAL,
        RING,
        CAP,
        CAP_BELOW_BODY,
        CAP_OVER_HAIR,
        GLOVE,
        WRIST,
        GLOVE_OVER_HAIR,
        WRIST_OVER_HAIR,
        GLOVE_OVER_BODY,
        WRIST_OVER_BODY,
        SHIELD,
        BACKSHIELD,
        SHIELD_BELOW_BODY,
        SHIELD_OVER_HAIR,
        WEAPON,
        BACKWEAPON,
        WEAPON_BELOW_ARM,
        WEAPON_BELOW_BODY,
        WEAPON_OVER_HAND,
        WEAPON_OVER_BODY,
        WEAPON_OVER_GLOVE,
        NUM_LAYERS
    };



    private static Set<Integer> transparents_items = new HashSet<>();
	private static Map<String, Layer> sublayerNames = new HashMap<>();
	static
    {
        transparents_items.add(1002186);

        // WEAPON
        sublayerNames.put( "weaponOverHand",			Clothing.Layer.WEAPON_OVER_HAND);
        sublayerNames.put( "weaponOverGlove",		Clothing.Layer.WEAPON_OVER_GLOVE);
        sublayerNames.put( "weaponOverBody",			Clothing.Layer.WEAPON_OVER_BODY	);
        sublayerNames.put( "weaponBelowArm",			Clothing.Layer.WEAPON_BELOW_ARM	);
        sublayerNames.put( "weaponBelowBody",		Clothing.Layer.WEAPON_BELOW_BODY);
        sublayerNames.put( "backWeaponOverShield",	Clothing.Layer.BACKWEAPON);
        // SHIELD
        sublayerNames.put( "shieldOverHair",			Clothing.Layer.SHIELD_OVER_HAIR	);
        sublayerNames.put( "shieldBelowBody",		Clothing.Layer.SHIELD_BELOW_BODY);
        sublayerNames.put( "backShield",				Clothing.Layer.BACKSHIELD		);
        // GLOVE
        sublayerNames.put("gloveWrist",				Clothing.Layer.WRIST);
        sublayerNames.put( "gloveOverHair",			Clothing.Layer.GLOVE_OVER_HAIR	);
        sublayerNames.put( "gloveOverBody",			Clothing.Layer.GLOVE_OVER_BODY	);
        sublayerNames.put( "gloveWristOverHair",		Clothing.Layer.WRIST_OVER_HAIR	);
        sublayerNames.put( "gloveWristOverBody",		Clothing.Layer.WRIST_OVER_BODY	);
        // CAP
        sublayerNames.put( "capOverHair",			Clothing.Layer.CAP_OVER_HAIR	);
        sublayerNames.put( "capBelowBody",			Clothing.Layer.CAP_BELOW_BODY	);
    };
}

package com.bapplications.maplemobile.gameplay.player.look;

import com.bapplications.maplemobile.gameplay.player.Stance;
import com.bapplications.maplemobile.utils.StaticUtils;
import com.bapplications.maplemobile.constatns.Loaded;
import com.bapplications.maplemobile.gameplay.textures.Texture;
import com.bapplications.maplemobile.utils.DrawArgument;
import com.bapplications.maplemobile.utils.Point;
import com.bapplications.maplemobile.pkgnx.NXNode;

import java.util.HashMap;

public class Body {

    public enum Layer
    {
        BODY,
        ARM,
        ARM_BELOW_HEAD,
        ARM_BELOW_HEAD_OVER_MAIL,
        ARM_OVER_HAIR,
        ARM_OVER_HAIR_BELOW_WEAPON,
        HAND_BELOW_WEAPON,
        HAND_OVER_HAIR,
        HAND_OVER_WEAPON,
        HEAD,
    };

    public static HashMap<String, Body.Layer> layerByName;
    static {
        layerByName = new HashMap<>();
        layerByName.put("arm", Body.Layer.ARM);
        layerByName.put("head", Body.Layer.HEAD);
        layerByName.put("body", Body.Layer.BODY);
        layerByName.put("backBody", Body.Layer.BODY);
        layerByName.put("armOverHair", Body.Layer.ARM_OVER_HAIR);
        layerByName.put("armBelowHead", Body.Layer.ARM_BELOW_HEAD);
        layerByName.put("handOverHair", Body.Layer.HAND_OVER_HAIR);
        layerByName.put("handOverWeapon", Body.Layer.HAND_OVER_WEAPON);
        layerByName.put("handBelowWeapon", Body.Layer.HAND_BELOW_WEAPON);
        layerByName.put("armOverHairBelowWeapon", Body.Layer.ARM_OVER_HAIR_BELOW_WEAPON);
        layerByName.put("armBelowHeadOverMailChest", Body.Layer.ARM_BELOW_HEAD_OVER_MAIL);
    }


    private String skinName;
    private HashMap<Byte, Texture>[][] stances = new HashMap[Stance.Id.values().length][Layer.values().length];

    public Body(int skin, BodyDrawInfo drawInfo) {
        String strid = StaticUtils.extendId(skin, 2);
        NXNode bodynode = Loaded.getFile(Loaded.WzFileName.CHARACTER).getRoot().getChild("000020" + strid + ".img");
        NXNode headnode = Loaded.getFile(Loaded.WzFileName.CHARACTER).getRoot().getChild("000120" + strid + ".img");

        for (Stance.Id stance : Stance.Id.values())
        {
			String stancename = Stance.valueOf(stance);

            NXNode stancenode = bodynode.getChild(stancename);

            if (stancenode.isNotExist())
                continue;

            for (byte frame = 0; stancenode.isChildExist(frame); ++frame) {
                NXNode framenode = stancenode.getChild(frame);
                for (NXNode partnode : framenode) {
                    String part = partnode.getName();

                    if (part.equals("delay") || part.equals("face")) {
                        continue;
                    }
                    String z = (String) partnode.getChild("z").get("");
                    Body.Layer layer = layerByName.get(z);

                    if (layer == null)
                        continue;

                    Point shift;

                    switch (layer) {
                        case HAND_BELOW_WEAPON:
                            shift = drawInfo.getHandPosition(stance, frame)
                                    .minus((Point) partnode.getChild("map")
                                            .getChild("handMove").get(new Point()));
                            break;
                        default:
                            Point navel = (Point) partnode.getChild("map").getChild("navel").get(new Point());
                            shift = drawInfo.getBodyPosition(stance, frame)
                                    .minus(navel);
                            break;
                    }

                    if (stances[stance.ordinal()][layer.ordinal()] == null) {
                        stances[stance.ordinal()][layer.ordinal()] = new HashMap<>();
                    }
                    Texture tex = new Texture(partnode);
                    tex.shift(shift);
                    stances[stance.ordinal()][layer.ordinal()]
                            .put(frame, tex);
                }
                boolean hasHeadImg = !stancename.equals("dead") && !headnode.getChild(stancename).getChild(frame).getChild("head").isNotExist();
                if (hasHeadImg) {
                    NXNode headsfnode = headnode.getChild(stancename).getChild(frame).getChild("head");
                    Point shift = drawInfo.getHeadPosition(stance, frame);

                    if (stances[stance.ordinal()][Layer.HEAD.ordinal()] == null) {
                        stances[stance.ordinal()][Layer.HEAD.ordinal()] = new HashMap<>();
                    }
                    Texture tex = new Texture(headsfnode);
                    tex.shift(shift);
                    stances[stance.ordinal()][Layer.HEAD.ordinal()]
                            .put(frame, tex);
                }
            }
        }

        String[] skintypes = new String[]{
                "Light",
                "Tan",
                "Dark",
                "Pale",
                "Blue",
                "Green",
                "", "", "",
                "Grey",
                "Pink",
                "Red"
        };

        skinName = (skin < skintypes.length) ? skintypes[skin] : "";
    }


    public void draw(Stance.Id stance, Layer layer, byte frame, DrawArgument args) {
        if(stances[stance.ordinal()][layer.ordinal()] == null){
            return;
        }
        Texture frameit = stances[stance.ordinal()][layer.ordinal()].get(frame);

        if (frameit == null)
            return;

        frameit.draw(args);

    }


    public String getSkinName()
    {
        return skinName;
    }


}

package com.bapplications.maplemobile.gameplay.player.look;

import android.util.Log;

import com.bapplications.maplemobile.constatns.Loaded;
import com.bapplications.maplemobile.gameplay.player.Stance;
import com.bapplications.maplemobile.gameplay.player.look.BodyDrawInfo;
import com.bapplications.maplemobile.gameplay.textures.Texture;
import com.bapplications.maplemobile.utils.DrawArgument;
import com.bapplications.maplemobile.utils.Point;
import com.bapplications.maplemobile.pkgnx.NXNode;

import java.util.HashMap;
import java.util.Map;


public class Hair {

    private static final Map<String, Layer> layersByName = new HashMap<>();
    static {

        layersByName.put("hair", Layer.DEFAULT);
        layersByName.put("hairBelowBody", Layer.BELOWBODY);
        layersByName.put("hairOverHead", Layer.OVERHEAD);
        layersByName.put("hairShade", Layer.SHADE);
        layersByName.put("backHair", Layer.BACK);
        layersByName.put("backHairBelowCap", Layer.BELOWCAP);
        layersByName.put("backHairBelowCapNarrow", Layer.BELOWCAPNARROW);
        layersByName.put("backHairBelowCapWide", Layer.BELOWCAPWIDE);
    }

    private final HashMap<Byte, Texture>[][] stances = new HashMap[Stance.Id.values().length][Layer.values().length];

    enum Layer
    {
        NONE,
        DEFAULT,
        BELOWBODY,
        OVERHEAD,
        SHADE,
        BACK,
        BELOWCAP,
        BELOWCAPNARROW,
        BELOWCAPWIDE,
    };

    public Hair(int hair_id, BodyDrawInfo drawInfo) {
        NXNode hairnode = Loaded.getFile(Loaded.WzFileName.CHARACTER).getRoot().getChild("Hair").getChild("000" + hair_id + ".img");

        for (String stanceName : Stance.mapNames.keySet())
        {
            Stance.Id stance = Stance.valueOf(stanceName);

            NXNode stanceNode = hairnode.getChild(stanceName);

            if (stanceNode.isNotExist())
                continue;

            for (byte frame = 0; stanceNode.isChildExist(frame); ++frame)
            {
                NXNode framenode = stanceNode.getChild(frame);
                for (NXNode layernode : framenode)
                {
                    String layername = layernode.getName();
                    Layer layer = layersByName.get(layername);

                    if (layer == null)
                    {
                        Log.e("Wierd error", "Unknown Hair.Layer name: [" + layername + "]\tLocation: [" + hairnode.getName() + "][" + stanceName + "][" + frame + "]");
                        continue;
                    }

                    if(layer == Layer.SHADE)
                        layernode = layernode.getChild(0);

                    Point brow = new Point(layernode.getChild("map").getChild("brow"));
                    Point shift = drawInfo.getHairPos(stance, frame).minus(brow);


                    Texture tex = new Texture(layernode);
                    tex.shift(shift);
                    if(stances[stance.ordinal()][layer.ordinal()] == null)
                        stances[stance.ordinal()][layer.ordinal()] = new HashMap<>();
                    stances[stance.ordinal()][layer.ordinal()]
                            .put(frame, tex);
                }
            }
        }
    }


    public void draw(Stance.Id stance, Layer layer, byte frame, DrawArgument args)
    {
        if(stances[stance.ordinal()][layer.ordinal()] == null){
            return;
        }

        Texture frameit = stances[stance.ordinal()][layer.ordinal()].get(frame);
        if (frameit == null)
            return;

        frameit.draw(args);
    }
}

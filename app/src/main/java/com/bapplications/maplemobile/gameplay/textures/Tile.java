package com.bapplications.maplemobile.gameplay.textures;

import com.bapplications.maplemobile.constatns.Loaded;
import com.bapplications.maplemobile.opengl.utils.DrawArgument;
import com.bapplications.maplemobile.opengl.utils.Point;
import com.bapplications.maplemobile.pkgnx.NXNode;

public class Tile extends Texture {

    public Tile(NXNode src, String tileset) {
        super(Loaded.getFile("Map").getRoot().getChild("Tile").getChild(tileset)
                .getChild((String) src.getChild("u").get())
                .getChild("" + (Long) src.getChild("no").get()), true);

        setPos(new Point(src));

        setZ(((Long) bitmapNode.getChild("z").get(0L)).byteValue());
        if ((Byte)getZ() == 0) {
            setZ(((Long) bitmapNode.getChild("zM").get(0L)).byteValue());
        }

    }

    public void draw(Point viewpos) {
        draw(new DrawArgument(viewpos));
    }


}

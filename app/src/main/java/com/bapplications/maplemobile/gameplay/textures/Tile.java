package com.bapplications.maplemobile.gameplay.textures;

import com.bapplications.maplemobile.constatns.Loaded;
import com.bapplications.maplemobile.opengl.utils.DrawArgument;
import com.bapplications.maplemobile.opengl.utils.Point;
import com.bapplications.maplemobile.pkgnx.NXNode;
import com.bapplications.maplemobile.pkgnx.nodes.NXLongNode;

public class Tile extends Texture {

    public Tile(NXNode src, String tileset) {
        super(Loaded.getFile("Map").getRoot().getChild("Tile").getChild(tileset)
                .getChild((String) src.getChild("u").get())
                .getChild("" + (Long) src.getChild("no").get()));

        setPos(new Point(src));
//        setPos(new Point((int)((NXLongNode)src.getChild("x")).getLong(),
//                -(int)((NXLongNode)src.getChild("y")).getLong()));

        setZ(((Long) bitmapNode.getChild("z").get(0L)).byteValue());
        if (getZ() == 0) {
            setZ(((Long) bitmapNode.getChild("zM").get(0L)).byteValue());
        }
    }

    public void draw(Point viewpos) {
        draw(new DrawArgument(viewpos));
    }


}

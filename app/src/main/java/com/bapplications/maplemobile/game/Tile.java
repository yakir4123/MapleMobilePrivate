package com.bapplications.maplemobile.game;

import com.bapplications.maplemobile.constatns.Loaded;
import com.bapplications.maplemobile.opengl.utils.DrawArgument;
import com.bapplications.maplemobile.opengl.utils.Point;
import com.bapplications.maplemobile.pkgnx.NXNode;
import com.bapplications.maplemobile.pkgnx.nodes.NXLongNode;

public class Tile {
    private Texture texture;
    private Point pos;
    private byte z;

    public Tile(NXNode src, String tileset) {
        NXNode dsrc = Loaded.getFile("Map").getRoot().getChild("Tile").getChild(tileset)
                .getChild((String) src.getChild("u").get())
                .getChild("" + (Long) src.getChild("no").get());
        texture = new Texture(dsrc);
        pos = new Point((int)((NXLongNode)src.getChild("x")).getLong(),
                (int)((NXLongNode)src.getChild("y")).getLong());
        z = (byte) ((NXLongNode)dsrc.getChild("z")).getLong();

        if (z == 0) {
            try {
                z = (byte) ((NXLongNode) dsrc.getChild("zM")).getLong();
            } catch (NullPointerException e){}
        }
    }

    void draw(Point viewpos)
    {
        texture.draw(new DrawArgument(pos.plus(viewpos)));
    }

    byte getz()
    {
        return z;
    }
}

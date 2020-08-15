package com.bapplications.maplemobile.game.textures;

import android.util.Pair;

import com.bapplications.maplemobile.opengl.utils.Point;
import com.bapplications.maplemobile.opengl.utils.Rectangle;
import com.bapplications.maplemobile.pkgnx.NXNode;
import com.bapplications.maplemobile.pkgnx.nodes.NXLongNode;

public class Frame {
    private Pair<Integer, Integer> scales;
    private Pair<Integer, Integer> opacities;
    private short delay;
    private Point head;
    private Texture texture;
    private Rectangle bounds;

    public Frame(NXNode src) {
        texture = new Texture(src);
        bounds = new Rectangle(src);
        try {
            head = (Point) src.getChild("head").get();
        } catch (NullPointerException e) {
            head = new Point();
        }
        try {
            delay = ((Long)(src.getChild("delay").get())).byteValue();
        } catch (NullPointerException e){
            delay = 0;
        }

        if (delay == 0)
            delay = 100;

        NXNode a0 = src.getChild("a0");
        NXNode a1 = src.getChild("a1");
        boolean hasa0 = a0 != null && a0.get() instanceof NXLongNode;
        boolean hasa1 = a1 != null && a1.get() instanceof NXLongNode;

        if (hasa0 && hasa1)
        {
            opacities = new Pair(a0.get(), a1.get());
        }
        else if (hasa0)
        {
            byte a0v = (byte)a0.get();
            opacities = new Pair(a0v, 255 - a0v);
        }
        else if (hasa1)
        {
            byte a1v = (byte)a1.get();
            opacities = new Pair(255 - a1v, a1v);
        }
        else
        {
            opacities = new Pair(255, 255);
        }


        NXNode z0 = src.getChild("z0");
        NXNode z1 = src.getChild("z1");
        boolean hasz0 = z0 != null && z0.get() instanceof NXLongNode;
        boolean hasz1 = z1 != null && z1.get() instanceof NXLongNode;

        if (hasz0 && hasz1)
        {
            scales = new Pair(z0.get(), z1.get());
        }
        else if (hasz0)
        {
            byte z0v = (byte)z0.get();
            scales = new Pair(z0v, 100 - z0v);
        }
        else if (hasz1)
        {
            byte z1v = (byte)z1.get();
            scales = new Pair(100 - z1v, z1v);
        }
        else
        {
            scales = new Pair(100, 100);
        }
    }

    public Frame() {
        delay = 0;
        opacities = new Pair(0, 0);
        scales = new Pair(0, 0);
    }


    public int startOpacity()
    {
        return opacities.first;
    }


    public int startScale()
    {
        return scales.first;
    }

    public short getDelay() {
        return delay;
    }

    public void draw(Point viewpos)
    {
        texture.draw(viewpos);
    }

    public void setZ(float z) {
        texture.setZ(z);
    }

    public float getZ() {
        return texture.getz();
    }
}

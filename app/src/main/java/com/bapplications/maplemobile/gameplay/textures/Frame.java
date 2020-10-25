package com.bapplications.maplemobile.gameplay.textures;

import android.util.Pair;

import com.bapplications.maplemobile.utils.Point;
import com.bapplications.maplemobile.utils.Rectangle;
import com.bapplications.maplemobile.pkgnx.NXNode;

public class Frame extends Texture {
    private Point head;
    private short delay;
    private Rectangle bounds;
    private Pair<Byte, Byte> scales;
    private Pair<Byte, Byte> opacities;

    public Frame(NXNode src) {
        super(src);
        bounds = new Rectangle(src);
        head = new Point(src.getChild("head"));
        head.flipY();
        try {
            delay = src.getChild("delay").get(0L).shortValue();
        } catch (ClassCastException e) {
            // There are cases where delay is string node and not a numeric node
            delay = Short.parseShort(src.getChild("delay").get("0"));
        }


        if (delay == 0)
            delay = 100;

        NXNode a0 = src.getChild("a0");
        NXNode a1 = src.getChild("a1");
        boolean hasa0 = !a0.isNotExist();
        boolean hasa1 = !a1.isNotExist();

        if (hasa0 && hasa1)
        {
            opacities = new Pair<>(a0.get(0L).byteValue(), a1.get(0L).byteValue());
        }
        else if (hasa0)
        {
            byte a0v = a0.get(0L).byteValue();
            opacities = new Pair<>(a0v, (byte) (255 - a0v));
        }
        else if (hasa1)
        {
            byte a1v = a1.get(0L).byteValue();
            opacities = new Pair<>((byte)(255 - a1v), a1v);
        }
        else
        {
            opacities = new Pair<>((byte)255, (byte)255);
        }


        NXNode z0 = src.getChild("z0");
        NXNode z1 = src.getChild("z1");
        boolean hasz0 = !z0.isNotExist();
        boolean hasz1 = !z1.isNotExist();

        if (hasz0 && hasz1)
        {
            scales = new Pair(z0.get(0L).byteValue(), z1.get(0L).byteValue());
        }
        else if (hasz0)
        {
            byte z0v = z0.get(0L).byteValue();
            scales = new Pair(z0v, 100 - z0v);
        }
        else if (hasz1)
        {
            byte z1v = z1.get(0L).byteValue();
            scales = new Pair((byte)(100 - z1v), z1v);
        }
        else
        {
            scales = new Pair((byte)(100), (byte)(100));
        }
    }

    public Frame() {
        super();
        delay = 0;
        opacities = new Pair<>((byte)0, (byte)0);
        scales = new Pair<>((byte)0, (byte)0);
    }


    public byte startOpacity()
    {
        return opacities.first;
    }

    public byte startScale()
    {
        return scales.first;
    }

    public short getDelay() {
        return delay;
    }

    public float opcstep(int timestep) {
        return timestep * (float)(opacities.second - opacities.first) / delay;
    }

    public float scalestep(int timestep) {
        return timestep * (float)(scales.second - scales.first) / delay;
    }

    public void setDelay(short delay) {
        this.delay = delay;
    }

    public Point getHead() {
        return head;
    }

    public Rectangle getBounds() {
        return bounds;
    }
}

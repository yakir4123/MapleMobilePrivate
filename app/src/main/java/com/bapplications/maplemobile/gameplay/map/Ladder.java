package com.bapplications.maplemobile.gameplay.map;

import com.bapplications.maplemobile.opengl.utils.Range;

import com.bapplications.maplemobile.opengl.utils.Point;
import com.bapplications.maplemobile.pkgnx.NXNode;

public class Ladder {

    short x;
    short y1;
    short y2;
    boolean ladder;

    public Ladder(NXNode src)
    {
        x = src.getChild("x").get(0L).shortValue();
        y1 = (short) -src.getChild("y1").get(0L).shortValue();
        y2 = (short) -src.getChild("y2").get(0L).shortValue();

        if ( y1 > y2) {
            short t = y2;
            y2 = y1;
            y1 = t;
        }
        ladder = src.getChild("l").get(0L) > 0;
    }

    public boolean isLadder()
    {
        return ladder;
    }

    public boolean inRange(Point position, boolean upwards)
    {
        Range<Short> hor = new Range((short)(position.x - 15), (short)(position.x + 15));
        Range<Short> ver = new Range<>(y1, y2);

        short y = (short) (upwards ? position.y + 5 : position.y - 5);

        return hor.contains(x) && ver.contains(y);
    }

    public boolean fellOff(short y, boolean downwards)
    {
        short dy = (short) (downwards ? y - 5 : y + 5);

        return dy < y1 || y - 5> y2;
    }

    public short getX()
    {
        return x;
    }
}

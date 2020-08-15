package com.bapplications.maplemobile.game;

import android.util.Range;

import com.bapplications.maplemobile.constatns.Loaded;
import com.bapplications.maplemobile.opengl.utils.Linear;
import com.bapplications.maplemobile.opengl.utils.Point;


public class Camera {

    private Point pos;

    // View limits.
    Range<Short> hbounds;
    Range<Short> vbounds;
    private int VWIDTH;
    private int VHEIGHT;

    public Camera()
    {
        pos = new Point();
        VWIDTH = Loaded.SCREEN_WIDTH;
        VHEIGHT = Loaded.SCREEN_HEIGHT;
    }

    void set_view(Range<Short> mapwalls, Range<Short> mapborders)
    {
        hbounds = new Range<Short>((short)(-mapwalls.getUpper()),
                (short)(-mapwalls.getLower()));
        vbounds = new Range<Short>((short)(-mapborders.getUpper()),
                (short)(-mapborders.getLower()));
    }

    public Point position(float alpha)
    {
        return pos;
    }


    public void offsetPosition(float dx, float dy) {
        pos.x += dx;
        pos.y += dy;
    }
}

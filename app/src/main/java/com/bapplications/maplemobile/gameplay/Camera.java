package com.bapplications.maplemobile.gameplay;

import com.bapplications.maplemobile.opengl.GLState;
import com.bapplications.maplemobile.utils.Range;

import com.bapplications.maplemobile.constatns.Loaded;
import com.bapplications.maplemobile.utils.Point;

import static com.bapplications.maplemobile.constatns.Configuration.OFFSETX;


public class Camera {

    private Point pos;
    // View limits.
    Range<Short> hbounds;
    Range<Short> vbounds;

    public Camera()
    {
        pos = new Point();
    }

    void setView(Range<Short> mapwalls, Range<Short> mapborders)
    {
        hbounds = new Range<>((short)(mapwalls.getLower()),
                (short)(mapwalls.getUpper()));
        vbounds = new Range<>((short)(-mapborders.getUpper()),
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

    public void setPosition(Point p) {
        pos.x = p.x;
        pos.y = p.y;
    }

    public Point position() {
        return position(1f);
    }


    public void update(Point position) {
        int HWidth = (int) (Loaded.SCREEN_WIDTH / 2 / GLState.SCALE);
        int HHeight = Loaded.SCREEN_HEIGHT/8;


        pos.x = -position.x;
        pos.y = -(position.y + HHeight);
        if (pos.x > -(hbounds.getLower() + HWidth - OFFSETX)){
            pos.x = -(hbounds.getLower() + HWidth - OFFSETX);
        }

        if (pos.x < -(hbounds.getUpper() - HWidth + OFFSETX)){
            pos.x = -(hbounds.getUpper() - HWidth + OFFSETX);
        }

    }
}

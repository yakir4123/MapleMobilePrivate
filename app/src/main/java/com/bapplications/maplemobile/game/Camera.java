package com.bapplications.maplemobile.game;

import android.util.Range;

import com.bapplications.maplemobile.constatns.Loaded;
import com.bapplications.maplemobile.opengl.utils.Linear;
import com.bapplications.maplemobile.opengl.utils.Point;


public class Camera {

    private Linear x;
    private Linear y;

    // View limits.
    Range<Short> hbounds;
    Range<Short> vbounds;
    private int VWIDTH;
    private int VHEIGHT;

    public Camera()
    {
        x = new Linear();
        y = new Linear();

        VWIDTH = Loaded.SCREEN_WIDTH;
        VHEIGHT = Loaded.SCREEN_HEIGHT;
    }

    void update(Point position)
    {
        double next_x = x.get();
        double hdelta = VWIDTH / 2. - position.x - next_x;

        if (Math.abs(hdelta) >= 5.0)
            next_x += hdelta * (12.0 / VWIDTH);

        double next_y = y.get();
        double vdelta = VHEIGHT / 2. - position.y - next_y;

        if (Math.abs(vdelta) >= 5.0)
            next_y += vdelta * (12.0 / VHEIGHT);

        if (next_x > hbounds.getLower() || hbounds.getUpper() - hbounds.getLower() < VWIDTH)
            next_x = hbounds.getLower();
        else if (next_x < hbounds.getUpper() + VWIDTH)
            next_x = hbounds.getUpper() + VWIDTH;

        if (next_y > vbounds.getLower() || vbounds.getUpper() - vbounds.getLower() < VHEIGHT)
            next_y = vbounds.getLower();
        else if (next_y < vbounds.getUpper() + VHEIGHT)
            next_y = vbounds.getUpper() + VHEIGHT;

        x.set((float) next_x);
        y.set((float) next_y);
    }

    void set_view(Range<Short> mapwalls, Range<Short> mapborders)
    {
        hbounds = new Range<Short>((short)(-mapwalls.getLower()),
                (short)(-mapwalls.getUpper()));
        vbounds = new Range<Short>((short)(-mapborders.getLower()),
                (short)(-mapborders.getUpper()));
    }

    Point position()
    {
        int shortx = (int) Math.round(x.get());
        int shorty = (int) Math.round(y.get());

        return new Point(shortx, shorty);
    }

    Point position(float alpha)
    {
        int shortx = (int) Math.round(x.get(alpha));
        int shorty = (int) Math.round(y.get(alpha));

        return new Point(shortx, shorty);
    }

    Point realposition(float alpha)
    {
        return new Point((int)x.get(alpha), (int)y.get(alpha));
    }
}

package com.bapplications.maplemobile.gameplay.map.look;

import android.util.Log;

import com.bapplications.maplemobile.gameplay.model_pools.BackgroundModel;
import com.bapplications.maplemobile.gameplay.textures.Animation;
import com.bapplications.maplemobile.utils.DrawArgument;
import com.bapplications.maplemobile.utils.Point;
import com.bapplications.maplemobile.pkgnx.NXNode;
import com.bapplications.maplemobile.constatns.Loaded;
import com.bapplications.maplemobile.gameplay.map.MovingObject;

public class Background extends Animation {

    private static int VHEIGHT;
    private static int VWIDTH;
    private int WOFFSET;
    private int HOFFSET;
    private int cx, cy, rx, ry;
    private float opacity;
    private int htile;
    private int vtile;

    enum Type
    {
        NORMAL,
        HTILED,
        VTILED,
        TILED,
        HMOVEA,
        VMOVEA,
        HMOVEB,
        VMOVEB
    };

    private MovingObject moveobj;
    public Background(NXNode src, BackgroundModel model) {
        super(model);

        VWIDTH = Loaded.SCREEN_WIDTH;
        VHEIGHT = Loaded.SCREEN_HEIGHT;
        WOFFSET = VWIDTH / 2;
        HOFFSET = VHEIGHT / 2;

//        setAnimated(src.getChild("ani").get(0L) > 0);
        this.opacity = src.getChild("a").get(0L);
        setLookLeft(src.getChild("f").get(0L) == 0);
        cx = src.getChild("cx").get(0L).intValue();
        cy = src.getChild("cy").get(0L).intValue();
        rx = src.getChild("rx").get(0L).intValue();
        ry = src.getChild("ry").get(0L).intValue();

        moveobj = new MovingObject();
        moveobj.set_x(src.getChild("x").get(0L).intValue());
        moveobj.set_y(-src.getChild("y").get(0L).intValue());

        Type type = typebyid(src.getChild("type").get(0L).intValue());

        settype(type);
    }

    private void settype(Type type) {
        float dim_x = getDimensions().x;
        float dim_y = getDimensions().y;

        // TODO: Double check for zero. Is this a WZ reading issue?
        if (cx == 0)
            cx = (dim_x > 0) ? (int) dim_x : 1;

        if (cy == 0)
            cy = (dim_y > 0) ? (int) dim_y : 1;

        htile = 1;
        vtile = 1;

        switch (type)
        {
            case HTILED:
            case HMOVEA:
                htile = VWIDTH / cx + 3;
                break;
            case VTILED:
            case VMOVEA:
                vtile = VHEIGHT / cy + 3;
                break;
            case TILED:
            case HMOVEB:
            case VMOVEB:
                htile = VWIDTH / cx + 3;
                vtile = VHEIGHT / cy + 3;
                break;
        }

        switch (type)
        {
            case HMOVEA:
            case HMOVEB:
                moveobj.hspeed = rx / 16;
                break;
            case VMOVEA:
            case VMOVEB:
                moveobj.vspeed = ry / 16;
                break;
        }
    }

    private static Type typebyid(int id) {
        if (id >= Type.NORMAL.ordinal() && id <= Type.VMOVEB.ordinal()){
            for (Type type : Type.values()) {
                if (type.ordinal() == id) {
                    return type;
                }
            }
        }
        Log.e("Background", "Unknown Background::Type id: [" + id + "]");

        return Type.NORMAL;
    }

    public void draw(Point viewpos, float alpha) {
        double x;

        if (moveobj.hmobile()) {
            x = moveobj.getAbsoluteX(viewpos.x, alpha);
        } else {
            float shift_x = rx * (WOFFSET - viewpos.x) / 100 + WOFFSET;
            x = moveobj.getAbsoluteX(shift_x, alpha);
        }

        double y;

        if (moveobj.vmobile()) {
            y = moveobj.getAbsoluteY(viewpos.y, alpha);
        } else {
            float shift_y = ry * (HOFFSET - viewpos.y / 2) / 100 + HOFFSET;
            y = moveobj.getAbsoluteY(shift_y, alpha);
        }

        if (htile > 1) {
            if (x > 0)
                x = (x % cx) - cx;

            if (x < -cx)
                x = x % -cx;
        }

        if (vtile > 1) {
            if (y > 0)
                y = (y % cy) - cy;

            if (y < -cy)
                y = y % -cy;
        }

        short ix = (short) (Math.round(x));
        short iy = (short) (Math.round(y));

        ix -= WOFFSET;
        iy -= HOFFSET;

        short tw = (short) (cx * htile);
        short th = (short) (cy * vtile);
        DrawArgument dargs = new DrawArgument(new Point(ix, iy));
        for (int tx = 0; tx < tw; tx += cx){
            for (int ty = 0; ty < th; ty += cy) {
                super.draw(dargs.offsetPosition(tx, ty), alpha);
                dargs.offsetPosition(-tx, -ty);
            }
        }

    }


    @Override
    public boolean update(int deltatime)
    {
        moveobj.move();
        return super.update(deltatime);
    }
}

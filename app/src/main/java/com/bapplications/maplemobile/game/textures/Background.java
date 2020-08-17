package com.bapplications.maplemobile.game.textures;

import android.util.Log;

import com.bapplications.maplemobile.opengl.utils.Point;
import com.bapplications.maplemobile.pkgnx.NXNode;
import com.bapplications.maplemobile.constatns.Loaded;
import com.bapplications.maplemobile.game.MovingObject;

import com.bapplications.maplemobile.pkgnx.nodes.NXLongNode;

public class Background extends Animation{

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
    public Background(NXNode src) {
        super(Loaded.getFile("Map").getRoot().getChild("Back")
                .getChild(src.getChild("bS").get() + ".img")
                .getChild(((NXLongNode)src.getChild("ani")).getBool() ? "ani" : "back")
                .getChild("" + src.getChild("no").get()), (byte) 0);

        VWIDTH = Loaded.SCREEN_WIDTH;
        VHEIGHT = Loaded.SCREEN_HEIGHT;
        WOFFSET = VWIDTH / 2;
        HOFFSET = VHEIGHT / 2;

        animated = ((NXLongNode)src.getChild("ani")).getBool();
        this.opacity = (Long)src.getChild("a").get();
        setFlip(((NXLongNode)src.getChild("f")).getBool());
        cx = ((Long) src.getChild("cx").get()).intValue();
        cy = ((Long) src.getChild("cy").get()).intValue();
        rx = ((Long) src.getChild("rx").get()).intValue();
        ry = ((Long) src.getChild("ry").get()).intValue();

        moveobj = new MovingObject();
        moveobj.set_x(((Long) src.getChild("x").get()).intValue());
        moveobj.set_y(-((Long) src.getChild("y").get()).intValue());

        Type type = typebyid(((Long) src.getChild("type").get()).intValue());

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

    public static Type typebyid(int id) {
        if (id >= Type.NORMAL.ordinal() && id <= Type.VMOVEB.ordinal()){
            for (Type type : Type.values()) {
                if (type.ordinal() == id) {
                    return type;
                }
            }
        }
        Log.d("Background", "Unknown Background::Type id: [" + id + "]");

        return Type.NORMAL;
    }

    @Override
    public void draw(Point viewpos, float alpha)
    {
        double x;

        if (moveobj.hmobile())
        {
            x = moveobj.getAbsoluteX(viewpos.x, alpha);
        }
        else
        {
            float shift_x = rx * (WOFFSET - viewpos.x) / 100 + WOFFSET;
            x = moveobj.getAbsoluteX(shift_x, alpha);
        }

        double y;

        if (moveobj.vmobile())
        {
            y = moveobj.getAbsoluteY(viewpos.y, alpha);
        }
        else
        {
            float shift_y = ry * (HOFFSET - viewpos.y/2) / 100 + HOFFSET;
            y = moveobj.getAbsoluteY(shift_y, alpha);
        }

        if (htile > 1)
        {
            if(x > 0)
                x = (x % cx) - cx;

            if (x < -cx)
                x = x % -cx;
        }

        if (vtile > 1)
        {
            if(y > 0)
                y = (y % cy) - cy;

            if (y < -cy)
                y = y % -cy;
        }

        short ix = (short)(Math.round(x));
        short iy = (short)(Math.round(y));

        ix -= WOFFSET ;
        iy -= HOFFSET ;

        short tw = (short) (cx * htile);
        short th = (short) (cy * vtile);

        for (int tx = 0; tx < tw; tx += cx)
            for (int ty = 0; ty < th; ty += cy)
                super.draw(new Point(ix + tx, iy + ty),  alpha);
    }


    @Override
    public boolean update(int deltatime)
    {
        moveobj.move();
        return super.update(deltatime);
    }
}

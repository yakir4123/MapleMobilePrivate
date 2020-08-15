package com.bapplications.maplemobile.game.textures;

import com.bapplications.maplemobile.StaticUtils;
import com.bapplications.maplemobile.opengl.utils.Linear;
import com.bapplications.maplemobile.opengl.utils.Nominal;
import com.bapplications.maplemobile.opengl.utils.Point;
import com.bapplications.maplemobile.pkgnx.NXNode;
import com.bapplications.maplemobile.pkgnx.nodes.NXBitmapNode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Animation {

    protected Point pos;
    protected short delay;
    protected boolean flip;
    protected int framestep;
    protected Nominal frame;
    protected Linear opacity;
    protected Linear xyscale;
    protected boolean animated;
    protected List<Frame> frames;
    protected boolean zigzag;

    public Animation(NXNode src, byte z) {
        boolean istexture = src instanceof NXBitmapNode;
        opacity = new Linear();
        xyscale = new Linear();
        frame = new Nominal();


        frames = new ArrayList<>();
        if (istexture)
        {
            frames.add(new Frame(src));
        }
        else
        {
            Set<Short> frameids = new HashSet<>();

            for (NXNode sub : src)
            {
                if (sub instanceof NXBitmapNode)
                {
                    short fid = (short) StaticUtils.orDefault(sub.getName(), -1);

                    if (fid >= 0)
                        frameids.add(fid);
                }
            }

            for ( Short fid : frameids)
            {
                NXNode sub = src.getChild("" + fid);
                Frame frame = new Frame(sub);
                frame.setZ(z);
                frames.add(frame);
            }

            if (frames.isEmpty())
                frames.add(new Frame());
        }

        animated = frames.size() > 1;
        try {
            zigzag = ((Long) src.getChild("zigzag").get()) > 0;
        } catch (NullPointerException e) {
            zigzag = false;
        }

        reset();
    }


    void reset()
    {
        frame.set(0);
        opacity.set(frames.get(0).startOpacity());
        xyscale.set(frames.get(0).startScale());
        delay = frames.get(0).getDelay();
        framestep = 1;
    }


    public void draw(Point viewpos, float alpha)
    {
        short interframe = (short) frame.get(alpha);
        float interopc = opacity.get(alpha) / 255;
        float interscale = xyscale.get(alpha) / 100;

        boolean modifyopc = interopc != 1.0f;
        boolean modifyscale = interscale != 1.0f;


        frames.get(interframe).draw(viewpos);
//        if (modifyopc || modifyscale)
//            frames[interframe].draw(args + DrawArgument(interscale, interscale, interopc));
//        else
//            frames[interframe].draw(args);
    }

    public float getZ() {
        return frames.get(0).getZ();
    }
}

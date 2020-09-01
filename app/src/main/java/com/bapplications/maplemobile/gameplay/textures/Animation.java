package com.bapplications.maplemobile.gameplay.textures;

import com.bapplications.maplemobile.StaticUtils;
import com.bapplications.maplemobile.opengl.utils.DrawArgument;
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
    protected int framestep;
    protected Linear opacity;
    protected boolean zigzag;
    protected Linear xyscale;
    protected boolean animated;
    protected List<Frame> frames;
    protected boolean lookLeft = true;
    protected Nominal<Short> frameNumber;

    public Animation(NXNode src) {
        this(src, 0);
    }
    public Animation(NXNode src, Object z) {
        this();

        if (src instanceof NXBitmapNode)
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
        zigzag = src.getChild("zigzag").get(0L) > 0;

        reset();
    }

    public Animation() {
        opacity = new Linear();
        xyscale = new Linear();
        frameNumber = new Nominal();
        pos = new Point();
        frames = new ArrayList<>();
    }

    public void newFrame(Frame frame){
        frames.add(frame);
        animated = frames.size() > 1;
    }

    public void reset()
    {
        frameNumber.set((short) 0);
        opacity.set(frames.get(0).startOpacity());
        xyscale.set(frames.get(0).startScale());
        delay = frames.get(0).getDelay();
        framestep = 1;
    }

    public void draw(DrawArgument args, float alpha)
    {
        short interframe = frameNumber.get(alpha);
        float interopc = opacity.get(alpha) / 255;
        float interscale = xyscale.get(alpha) / 100;

        boolean modifyopc = interopc != 1.0f;
        boolean modifyscale = interscale != 1.0f;

        args.setDirection(lookLeft);
        frames.get(interframe).draw(args.plus(pos));
//        if (modifyopc || modifyscale)
//            frames[interframe].draw(args + DrawArgument(interscale, interscale, interopc));
//        else
//            frames[interframe].draw(args);
    }

    public Object getZ() {
        return frames.get(0).getZ();
    }


    public boolean update(int deltatime)
    {
		Frame framedata = getFrameNumber();

        opacity.plus(framedata.opcstep(deltatime));

        if (opacity.last() < 0.0f)
            opacity.set(0.0f);
        else if (opacity.last() > 255.0f)
            opacity.set(255.0f);

        xyscale.plus(framedata.scalestep(deltatime));

        if (xyscale.last() < 0.0f)
            opacity.set(0.0f);

        if (deltatime >= delay)
        {
            short lastframe = (short)(frames.size() - 1);
            short nextframe;
            boolean ended;

            if (zigzag && lastframe > 0)
            {
                if (framestep == 1 && frameNumber.equals(lastframe))
                {
                    framestep = -framestep;
                    ended = false;
                }
                else if (framestep == -1 && frameNumber.equals((short)0))
                {
                    framestep = -framestep;
                    ended = true;
                }
                else
                {
                    ended = false;
                }

                nextframe = (short) frameNumber.plus((short) framestep);
            }
            else
            {
                if (frameNumber.equals(lastframe))
                {
                    nextframe = 0;
                    ended = true;
                }
                else
                {
                    nextframe = (short) frameNumber.plus((short)1);
                    ended = false;
                }
            }

            int delta = deltatime - delay;
            float threshold = (float)(delta) / deltatime;
            frameNumber.next(nextframe, threshold);

            try {
                delay = frames.get(nextframe).getDelay();
            } catch (ArrayIndexOutOfBoundsException e){
                delay = 1;
            }

            if (delay >= delta)
                delay -= delta;

            opacity.set(frames.get(nextframe).startOpacity());
            xyscale.set(frames.get(nextframe).startScale());

            return ended;
        }
        else
        {
            frameNumber.normalize();

            delay -= deltatime;

            return false;
        }
    }

    public Point getDimensions() {
        return getFrameNumber().getDimenstion();
    }

	Frame getFrameNumber()
    {
        return frames.get(frameNumber.get());
    }

    protected void setPos(Point point) {
        point.y *= -1;
        pos = point;
    }

	public Frame getFrame()
    {
        return frames.get(frameNumber.get());
    }
    public Point getHead() {
        return getFrame().getHead();
    }

    public void shiftY(float y) {
        for(Frame frame : frames) {
            frame.shiftY(y);
        }
    }

    public void shiftHead() {
        for(Frame frame : frames) {
            frame.shiftY(-frame.getHead().y);
        }
    }
}

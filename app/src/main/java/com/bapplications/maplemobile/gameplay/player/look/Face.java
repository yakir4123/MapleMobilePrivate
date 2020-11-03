package com.bapplications.maplemobile.gameplay.player.look;

import com.bapplications.maplemobile.constatns.Loaded;
import com.bapplications.maplemobile.gameplay.textures.Frame;
import com.bapplications.maplemobile.utils.DrawArgument;
import com.bapplications.maplemobile.utils.Point;
import com.bapplications.maplemobile.pkgnx.NXNode;

import java.util.HashMap;
import java.util.Map;

public class Face {
    private Map<Byte, Frame>[] expressions;
    private Point frameShift = new Point();
    private int lookLeft = 1;

    public Face(int faceid) {
        Frame frame;
        NXNode facenode;
        Point negateShift = new Point(-1, -1);
        expressions = new HashMap[Expression.values().length];
        NXNode faces = Loaded.getFile(Loaded.WzFileName.CHARACTER).getRoot().getChild("Face").getChild("000" + faceid + ".img");
        for (Expression exp : Expression.values())
        {
            expressions[exp.ordinal()] = new HashMap<>();
            if (exp == Expression.DEFAULT)
            {
                frame = new Frame();
                facenode = faces.getChild("default");
                frame.setDelay((short) 2500);
                frame.initTexture(facenode.getChild("face"));
                Point shift = facenode.getChild("face").getChild("map")
                        .getChild("brow").get(new Point());
                frame.shift(shift.mul(negateShift));
                frame.setZ("face");
                expressions[exp.ordinal()].put((byte) 0, frame);
            } else {
                String facename = exp.name().toLowerCase();
                facenode = faces.getChild(facename);
                for (byte frameNumber = 0;  facenode.isChildExist(frameNumber); ++frameNumber) {
                    NXNode frameNode = facenode.getChild(frameNumber);
                    frame = new Frame();
                    frame.setDelay((frameNode.getChild("delay").get(2500L)).shortValue());
                    frame.initTexture(frameNode.getChild("face"));
                    frame.setZ("face");
                    Point shift = (Point) frameNode.getChild("face").getChild("map")
                            .getChild("brow").get(new Point());
                    frame.shift(shift.mul(negateShift));
                    expressions[exp.ordinal()].put(frameNumber, frame);
                }
            }
        }
    }

    public void draw(Expression expression, byte frame, DrawArgument args) {
        Frame frameit = expressions[expression.ordinal()].get(frame);
        if (frameit != null) {
            frameit.draw(args.plus(getDirectionShift(args.getXScale())));
        }
    }

    private Point getDirectionShift(float xscale) {
        return frameShift.mul(new Point(xscale, 1));
    }

    public void shift(Point faceshift) {
        this.frameShift = faceshift;
    }

    public void setDirection(boolean lookLeft){
        if(lookLeft)
            this.lookLeft = 1;
        else
            this.lookLeft = -1;
    }

    public short getDelay(Expression expression, Byte frame) {
        Frame delayit = expressions[expression.ordinal()].get(frame);
        return delayit != null ? delayit.getDelay() : 100;
    }

    public byte nextFrame(Expression expression, Byte frame) {
        return (byte) (expressions[expression.ordinal()]
                .containsKey((byte) (frame + 1)) ? frame + 1 : 0);
    }
}

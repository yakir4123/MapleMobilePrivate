package com.bapplications.maplemobile.gameplay.player;

import com.bapplications.maplemobile.constatns.Loaded;
import com.bapplications.maplemobile.gameplay.textures.Animation;
import com.bapplications.maplemobile.gameplay.textures.Frame;
import com.bapplications.maplemobile.opengl.utils.DrawArgument;
import com.bapplications.maplemobile.opengl.utils.Point;
import com.bapplications.maplemobile.pkgnx.NXNode;

import java.util.HashMap;
import java.util.Map;

public class Face {
    private Map<Byte, Frame>[] expressions;

    public Face(int faceid) {
        Frame frame;
        NXNode facenode;
        expressions = new HashMap[Expression.values().length];
        NXNode faces = Loaded.getFile("Character").getRoot().getChild("Face").getChild("000" + faceid + ".img");

        for (Expression exp : Expression.values())
        {
            expressions[exp.ordinal()] = new HashMap<>();
            if (exp == Expression.DEFAULT)
            {
                frame = new Frame();
                facenode = faces.getChild("default");
                frame.setDelay((short) 2500);
                frame.initTexture(facenode.getChild("face"));
                frame.setZ("face");
//                frame.shift(frame.getDimenstion().mul(new Point(0, 0.5f)));
//                frame.shift(drawInfo.);
                expressions[exp.ordinal()].put((byte) 0, frame);
            } else {
                String facename = exp.name().toLowerCase();
                facenode = faces.getChild(facename);
                for (byte frameNumber = 0;  facenode.isChildExist(frameNumber); ++frameNumber) {
                    NXNode frameNode = facenode.getChild(frameNumber);
                    frame = new Frame();
                    if(frameNode.isChildExist("delay"))
                        frame.setDelay(((Long) frameNode.getChild("delay").get()).shortValue());
                    else
                        frame.setDelay((short) 2500);
                    frame.initTexture(frameNode.getChild("face"));
                    frame.setZ("face");
                    expressions[exp.ordinal()].put(frameNumber, frame);
                }
            }
        }
//        name = nl::nx::string["Eqp.img"]["Eqp"]["Face"][std::to_string(faceid)]["name"];
    }

    public void draw(Expression expression, byte frame, DrawArgument args) {
        Frame frameit = expressions[expression.ordinal()].get(frame);
        if (frameit != null)
            frameit.draw(args);
    }
}

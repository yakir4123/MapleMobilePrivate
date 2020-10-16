package com.bapplications.maplemobile.gameplay.textures;

import com.bapplications.maplemobile.opengl.utils.DrawArgument;
import com.bapplications.maplemobile.opengl.utils.Point;
import com.bapplications.maplemobile.pkgnx.NXNode;

public class Obj extends Animation {

    public Obj(NXNode src, ObjModel model) {
        super(model);
        setPos(new Point(src));
        setLookLeft(src.getChild("f").get(0L) == 0);
    }

    public void draw(Point viewpos, float alpha){
        super.draw(new DrawArgument(viewpos), alpha);
    }
}

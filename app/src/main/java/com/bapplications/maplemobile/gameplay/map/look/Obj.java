package com.bapplications.maplemobile.gameplay.map.look;

import com.bapplications.maplemobile.gameplay.model_pools.ObjModel;
import com.bapplications.maplemobile.gameplay.textures.Animation;
import com.bapplications.maplemobile.utils.DrawArgument;
import com.bapplications.maplemobile.utils.Point;
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

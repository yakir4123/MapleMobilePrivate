package com.bapplications.maplemobile.gameplay.textures;

import com.bapplications.maplemobile.constatns.Loaded;
import com.bapplications.maplemobile.opengl.utils.DrawArgument;
import com.bapplications.maplemobile.opengl.utils.Point;
import com.bapplications.maplemobile.pkgnx.NXNode;
import com.bapplications.maplemobile.pkgnx.nodes.NXLongNode;

public class Obj extends Animation {

    public Obj(NXNode src){
        super(Loaded.getFile(Loaded.WzFileName.MAP).getRoot().getChild("Obj").getChild(src.getChild("oS").get("") + ".img")
                        .getChild(src.getChild("l0").get(""))
                        .getChild(src.getChild("l1").get(""))
                        .getChild(src.getChild("l2").get("")),
                src.getChild("z").get(0L).byteValue());
        setPos(new Point(src));
        lookLeft = src.getChild("f").get(0L) == 0;
    }

    public void draw(Point viewpos, float alpha){
        super.draw(new DrawArgument(viewpos), alpha);
    }
}

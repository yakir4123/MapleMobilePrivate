package com.bapplications.maplemobile.gameplay.textures;

import com.bapplications.maplemobile.constatns.Loaded;
import com.bapplications.maplemobile.opengl.utils.DrawArgument;
import com.bapplications.maplemobile.opengl.utils.Point;
import com.bapplications.maplemobile.pkgnx.NXNode;
import com.bapplications.maplemobile.pkgnx.nodes.NXLongNode;

public class Obj extends Animation {

    public Obj(NXNode src){
        super(Loaded.getFile("Map").getRoot().getChild("Obj").getChild(src.getChild("oS").get() + ".img")
                        .getChild((String) src.getChild("l0").get())
                        .getChild((String) src.getChild("l1").get())
                        .getChild((String) src.getChild("l2").get()),
                (byte) ((NXLongNode)src.getChild("z")).getLong());
        setPos(new Point(src));
        try {
            if(((NXLongNode) src.getChild("f")).getBool())
                flip();
        } catch (NullPointerException e) {}
    }

    public void draw(Point viewpos, float alpha){
        super.draw(new DrawArgument(viewpos), alpha);
    }
}

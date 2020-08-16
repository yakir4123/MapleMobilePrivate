package com.bapplications.maplemobile.game;

import com.bapplications.maplemobile.opengl.utils.Point;
import com.bapplications.maplemobile.pkgnx.NXNode;

import java.util.EnumMap;

public class MapTilesObjs {

    EnumMap<Layer.Id, TilesObjs> layers;

    public MapTilesObjs(NXNode src) {
        layers = new EnumMap<>(Layer.Id.class);
        for (Layer.Id id : Layer.Id.values())
            layers.put(id, new TilesObjs(src.getChild("" + id.ordinal())));
    }

    public void draw(Layer.Id layer, Point viewpos, float alpha) {
        layers.get(layer).draw(viewpos, alpha);
    }

    public void update(int deltatime) {
        for (Layer.Id id : Layer.Id.values())
            layers.get(id).update(deltatime);
    }
}

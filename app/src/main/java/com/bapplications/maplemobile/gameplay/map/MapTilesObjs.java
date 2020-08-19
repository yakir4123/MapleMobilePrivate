package com.bapplications.maplemobile.gameplay.map;

import com.bapplications.maplemobile.opengl.utils.Point;
import com.bapplications.maplemobile.pkgnx.NXNode;

import java.util.EnumMap;

public class MapTilesObjs {

    EnumMap<Layer, TilesObjs> layers;

    public MapTilesObjs(NXNode src) {
        layers = new EnumMap<>(Layer.class);
        for (Layer id : Layer.values())
            layers.put(id, new TilesObjs(src.getChild("" + id.ordinal())));
    }

    public void draw(Layer layer, Point viewpos, float alpha) {
        layers.get(layer).draw(viewpos, alpha);
    }

    public void update(int deltatime) {
        for (Layer id : Layer.values())
            layers.get(id).update(deltatime);
    }
}

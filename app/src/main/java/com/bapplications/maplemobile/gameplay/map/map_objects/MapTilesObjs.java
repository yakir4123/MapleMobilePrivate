package com.bapplications.maplemobile.gameplay.map.map_objects;

import com.bapplications.maplemobile.gameplay.map.Layer;
import com.bapplications.maplemobile.utils.Point;
import com.bapplications.maplemobile.pkgnx.NXNode;
import com.bapplications.maplemobile.utils.Rectangle;

import java.util.EnumMap;

public class MapTilesObjs {

    EnumMap<Layer, TilesObjs> layers;

    public MapTilesObjs(NXNode src, Rectangle mapSize) {
        layers = new EnumMap<>(Layer.class);
        for (Layer id : Layer.values())
            layers.put(id, new TilesObjs(src.getChild(id.ordinal()), mapSize));
    }

    public void draw(Layer layer, Rectangle cameraRect, Point viewpos, float alpha) {
        layers.get(layer).draw(cameraRect, viewpos, alpha);
    }

    public void update(int deltaTime) {
        for (Layer id : Layer.values())
            layers.get(id).update(deltaTime);
    }
}

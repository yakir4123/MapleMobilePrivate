package com.bapplications.maplemobile.gameplay.map;

import com.bapplications.maplemobile.gameplay.physics.Physics;
import com.bapplications.maplemobile.opengl.utils.Point;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MapObjects<T extends MapObject> {

    private Map<Integer, T> objects = new HashMap<>();
    private Set<Integer>[] layers = new Set[Layer.values().length];

    public void draw(Layer layer, Point view, float alpha) {

        if(layers[layer.ordinal()] == null)
            return;
        for (Integer oid : layers[layer.ordinal()])
        {
            MapObject mmo = get(oid);

            if (mmo != null && mmo.isActive())
                mmo.draw(view, alpha);
        }
    }

    public MapObject get(Integer oid) {
        return objects.get(oid);
    }

    public void add(T mo)
    {
        int oid = mo.getOid();
        byte layer = mo.getLayer();
        objects.put(oid, mo);
        addOid(layer, oid);
    }

    public void update(Physics physics, int deltatime) {
        for(int oid: objects.keySet()){
            boolean remove_mob = false;

            if (objects.get(oid) != null)
            {
                MapObject mmo = objects.get(oid);
                byte oldlayer = mmo.getLayer();
                byte newlayer = mmo.update(physics, deltatime);

                if (newlayer == -1)
                {
                    remove_mob = true;
                }
                else if (newlayer != oldlayer)
                {
                    layers[oldlayer].remove(oid);
                    addOid(newlayer, oid);
                }
            }
            else
            {
                remove_mob = true;
            }

            if (remove_mob)
                objects.remove(oid);
        }
    }

    private void addOid(byte layer, int oid) {
        if (layers[layer] == null) {
            layers[layer] = new HashSet<>();
        }
        layers[layer].add(oid);
    }

    public Map<Integer, T> getObjects() {
        return objects;
    }

    public void clear() {
        objects.clear();

        for (Set<Integer> layer : layers)
            layer.clear();
    }
}

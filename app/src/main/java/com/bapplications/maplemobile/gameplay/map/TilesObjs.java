package com.bapplications.maplemobile.gameplay.map;



import com.bapplications.maplemobile.gameplay.textures.Obj;
import com.bapplications.maplemobile.gameplay.textures.Tile;
import com.bapplications.maplemobile.opengl.utils.Point;
import com.bapplications.maplemobile.pkgnx.NXNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class TilesObjs {
    private Map<Float, List<Tile>> tiles;
    private Map<Float, List<Obj>> objs;

    public TilesObjs(NXNode src) {

        String tileset;

        tiles = new TreeMap<>();
        objs = new TreeMap<>();

        // order matter
        for(int i = 0; i < src.getChild("obj").getChildCount() ;i++)
        {
            Obj obj = new Obj(src.getChild("obj").getChild("" + i));
            float z = obj.getZ();
            putObj(z, obj);
        }

        try {
            tileset = src.getChild("info").getChild("tS").get() + ".img";
        } catch (NullPointerException e){
          return;
        }

        for (NXNode tilenode : src.getChild("tile"))
        {
            Tile tile = new Tile(tilenode, tileset);
            float z = tile.getZ();
            putTile(z, tile);
        }

    }

    private <K, V> void putOnSortedMap(Map<K, List<V>> orderedMap, K z, V value){
        if (!orderedMap.containsKey(z)){
            orderedMap.put(z, new ArrayList<>());
        }
        orderedMap.get(z).add(value);
    }

    private void putObj(float z, Obj obj) {
        putOnSortedMap(objs, z, obj);
    }

    private void putTile(float z, Tile tile) {
        putOnSortedMap(tiles, z, tile);
    }

    public void draw(Point viewpos, float alpha) {
        if(objs == null){
            return;
        }
        for (List<Obj> lobjs : objs.values()){
            for(Obj obj : lobjs){
                obj.draw(viewpos, alpha);
            }
        }

        if(tiles == null){
            return;
        }
        for (List<Tile> ltiles : tiles.values()){
            for(Tile tile : ltiles) {
                tile.draw(viewpos);
            }
        }
    }

    public void update(int deltatime) {
        for (List<Obj> lobjs : objs.values()){
            for(Obj obj : lobjs){
                obj.update(deltatime);
            }
        }
    }
}

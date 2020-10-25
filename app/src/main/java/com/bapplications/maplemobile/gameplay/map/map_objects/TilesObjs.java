package com.bapplications.maplemobile.gameplay.map.map_objects;



import com.bapplications.maplemobile.gameplay.map.look.Obj;
import com.bapplications.maplemobile.gameplay.map.look.Tile;
import com.bapplications.maplemobile.gameplay.model_pools.ObjModel;
import com.bapplications.maplemobile.gameplay.model_pools.TileModel;
import com.bapplications.maplemobile.utils.Point;
import com.bapplications.maplemobile.pkgnx.NXNode;

import java.util.Map;
import java.util.List;
import java.util.TreeMap;
import java.util.HashMap;
import java.util.ArrayList;

public class TilesObjs {
    private static final String TAG = "TilesObjs";

    private Map<String, ObjModel> objModelTree = new HashMap<>();
    private Map<String, TileModel> tileModelTree = new HashMap<>();

    private Map<Byte, List<Tile>> tiles;
    private Map<Byte, List<Obj>> objs;

    public TilesObjs(NXNode src) {

        String tileset;

        tiles = new TreeMap<>();
        objs = new TreeMap<>();

        // order matter
        for(int i = 0; i < src.getChild("obj").getChildCount() ;i++)
        {
            ObjModel model = getObjModel(src.getChild("obj").getChild(i));
            Obj obj = new Obj(src.getChild("obj").getChild(i), model);
            byte z = (Byte)obj.getZ();
            putObj(z, obj);
        }

        try {
            tileset = src.getChild("info").getChild("tS").get("") + ".img";
        } catch (NullPointerException e){
          return;
        }

        for (NXNode tilenode : src.getChild("tile"))
        {
            TileModel model = getTileModel(tilenode, tileset);
            Tile tile = new Tile(tilenode, model);
            byte z = ((Long) model.getZ()).byteValue();
            putTile(z, tile);
        }

    }

    private ObjModel getObjModel(NXNode src) {
        String oS = src.getChild("oS").get("") + ".img";
        String l0 = src.getChild("l0").get("");
        String l1 = src.getChild("l1").get("");
        String l2 = src.getChild("l2").get("");

        String key = oS + l0 + l1 + l2;
        if(!objModelTree.containsKey(key)){
            objModelTree.put(key, new ObjModel(oS, l0, l1, l2, src.getChild("z").get(0L).byteValue()));
        }
        return objModelTree.get(key);
    }

    private TileModel getTileModel(NXNode src, String tileset) {
        String u = src.getChild("u").get("");
        int no = src.getChild("no").get(0L).intValue();

        String key = tileset + u + no;
        if(!tileModelTree.containsKey(key)){
            tileModelTree.put(key, new TileModel(tileset, u, no));
        }
        return tileModelTree.get(key);
    }

    private <K, V> void putOnSortedMap(Map<K, List<V>> orderedMap, K z, V value){
        if (!orderedMap.containsKey(z)){
            orderedMap.put(z, new ArrayList<>());
        }
        orderedMap.get(z).add(value);
    }

    private void putObj(byte z, Obj obj) {
        putOnSortedMap(objs, z, obj);
    }

    private void putTile(byte z, Tile tile) {
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

package com.bapplications.maplemobile.gameplay.map;



import android.util.Log;

import com.bapplications.maplemobile.gameplay.textures.Obj;
import com.bapplications.maplemobile.gameplay.textures.ObjModel;
import com.bapplications.maplemobile.gameplay.textures.Tile;
import com.bapplications.maplemobile.gameplay.textures.TileModel;
import com.bapplications.maplemobile.opengl.utils.Point;
import com.bapplications.maplemobile.pkgnx.NXNode;

import java.util.Map;
import java.util.List;
import java.util.TreeMap;
import java.util.HashMap;
import java.util.ArrayList;

public class TilesObjs {
    private static final String TAG = "TilesObjs";

    private Map<String,
                Map<String,
                    Map<String,
                        Map<String, ObjModel>
                    >
                >
            > objModelTree = new HashMap<>();

    private Map<String,
                Map<String,
                    Map<Integer, TileModel>
                >
            > tileModelTree = new HashMap<>();

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
        if(!objModelTree.containsKey(oS)){
            objModelTree.put(oS, new HashMap<>());
        }
        Map<String, Map<String, Map<String, ObjModel>>> l0s = objModelTree.get(oS);

        String l0 = src.getChild("l0").get("");
        if(!l0s.containsKey(l0)){
            l0s.put(l0, new HashMap<>());
        }
        Map<String, Map<String, ObjModel>> l1s = l0s.get(l0);

        String l1 = src.getChild("l1").get("");
        if(!l1s.containsKey(l1)){
            l1s.put(l1, new HashMap<>());
        }
        Map<String, ObjModel> l2s = l1s.get(l1);

        String l2 = src.getChild("l2").get("");
        if(!l2s.containsKey(l2)){
            l2s.put(l2, new ObjModel(oS, l0, l1, l2, src.getChild("z").get(0L).byteValue()));
        }
        return l2s.get(l2);
    }

    private TileModel getTileModel(NXNode tilenode, String tileset) {
        if(!tileModelTree.containsKey(tileset)){
            tileModelTree.put(tileset, new HashMap<>());
        }
        Map<String, Map<Integer,TileModel>> types = tileModelTree.get(tileset);
        String typeName = tilenode.getChild("u").get("");
        if(!types.containsKey(typeName)){
            types.put(typeName, new HashMap<>());
        }
        Map<Integer, TileModel> numbers = types.get(typeName);
        Integer numberName = tilenode.getChild("no").get(0L).intValue();
        if(!numbers.containsKey(numberName)){
            numbers.put(numberName, new TileModel(tileset, typeName, numberName));
        }
        return numbers.get(numberName);
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

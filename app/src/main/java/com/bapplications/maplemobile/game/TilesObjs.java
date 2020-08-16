package com.bapplications.maplemobile.game;


import android.util.Log;

import com.bapplications.maplemobile.game.textures.Obj;
import com.bapplications.maplemobile.game.textures.Tile;
import com.bapplications.maplemobile.opengl.utils.Point;
import com.bapplications.maplemobile.pkgnx.NXNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TilesObjs {
    private Map<Float, List<Tile>> tiles;
    private Map<Float, List<Obj>> objs;

    public TilesObjs(NXNode src) {

        String tileset;

        tiles = new HashMap<>();
        objs = new HashMap<>();

        try {
            tileset = src.getChild("info").getChild("tS").get() + ".img";
        } catch (NullPointerException e){
          return;
        }

        for (NXNode objnode : src.getChild("obj"))
        {
            Obj obj = new Obj(objnode);
            float z = obj.getZ();
            putObj(z, obj);
        }

        for (NXNode tilenode : src.getChild("tile"))
        {
            Tile tile = new Tile(tilenode, tileset);
            float z = tile.getz();
            putTile(z, tile);
        }

//        String[] arr = new String[]{"1" , "0"};
//        for(String a : arr) {
//            NXNode tilenode = src.getChild("tile").getChild(a);
//            Tile tile = new Tile(tilenode, tileset);
//            float z = tile.getz();
//            putTile(z, tile);
//        }


//        NXNode tilenode = src.getChild("tile").getChild("1");
//        Tile tile = new Tile(tilenode, tileset);
////        tile.setPos(new Point(-30, 0));
//        float z = tile.getz();
//        putTile(z, tile);
//
//
//        tilenode = src.getChild("tile").getChild("8");
//        tile = new Tile(tilenode, tileset);
////        tile.setPos(new Point(15, 0));
//        z = tile.getz();
//        putTile(z, tile);
    }


    private void putObj(float z, Obj obj) {
        if (!objs.containsKey(z)){
            objs.put(z, new ArrayList<>());
        }
        objs.get(z).add(obj);
    }

    private void putTile(float z, Tile tile) {
        if (!tiles.containsKey(z)){
            tiles.put(z, new ArrayList<>());
        }
        tiles.get(z).add(tile);
    }

    public void draw(Point viewpos, float alpha) {
        if(objs == null){
            return;
        }
        for (List<Obj> lobjs : objs.values()){
            for(Obj obj : lobjs){
                obj.draw(viewpos, 1f);
            }
        }


        if(tiles == null){
            return;
        }
        for (List<Tile> ltiles : tiles.values()){
            for(Tile tile : ltiles){
                tile.draw(viewpos);
            }
        }
        Log.d("draw", "==========================");
    }
}

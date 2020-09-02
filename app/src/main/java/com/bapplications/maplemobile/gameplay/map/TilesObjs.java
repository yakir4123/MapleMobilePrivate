package com.bapplications.maplemobile.gameplay.map;



import android.util.Log;
import com.bapplications.maplemobile.gameplay.textures.Obj;
import com.bapplications.maplemobile.gameplay.textures.Texture;
import com.bapplications.maplemobile.gameplay.textures.Tile;
import com.bapplications.maplemobile.opengl.utils.Point;
import com.bapplications.maplemobile.pkgnx.NXNode;

import org.reactivestreams.Subscriber;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class TilesObjs {
    private static final String TAG = "TilesObjs";
    private Map<Byte, List<Tile>> tiles;
    private Map<Byte, List<Obj>> objs;

    public TilesObjs(NXNode src) {

        String tileset;

        tiles = new TreeMap<>();
        objs = new TreeMap<>();

        // order matter
        for(int i = 0; i < src.getChild("obj").getChildCount() ;i++)
        {
            Obj obj = new Obj(src.getChild("obj").getChild("" + i));
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
            Tile tile = new Tile(tilenode, tileset);
            byte z = (Byte)tile.getZ();
            putTile(z, tile);
        }

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

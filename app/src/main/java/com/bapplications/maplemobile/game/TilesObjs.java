package com.bapplications.maplemobile.game;


import com.bapplications.maplemobile.opengl.utils.Point;
import com.bapplications.maplemobile.pkgnx.NXNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TilesObjs {
    private Map<Byte, List<Tile>> tiles;
    public TilesObjs(NXNode src) {

//        for (auto objnode : src["obj"])
//        {
//            Obj obj{ objnode };
//            int8_t z = obj.getz();
//            objs.emplace(z, std::move(obj));
//        }

        String tileset;
        try {
            tileset = src.getChild("info").getChild("tS").get() + ".img";
        } catch (NullPointerException e){
          return;
        }
        tiles = new HashMap<>();
        for (NXNode tilenode : src.getChild("tile"))
        {
            Tile tile = new Tile(tilenode, tileset);
            byte z = tile.getz();
            putTile(z, tile);
        }


    }

    private void putTile(byte z, Tile tile) {
        if (!tiles.containsKey(z)){
            tiles.put(z, new ArrayList<>());
        }
        tiles.get(z).add(tile);
    }

    public void draw(Point viewpos, float alpha) {
//        for (auto& iter : objs)
//            iter.second.draw(viewpos, alpha);

        if(tiles == null){
            return;
        }
        for (List<Tile> ztiles : tiles.values()){
            for(Tile tile : ztiles){
                tile.draw(viewpos);
            }
        }
    }
}

package com.bapplications.maplemobile.gameplay.map.map_objects;

import com.bapplications.maplemobile.gameplay.map.look.Background;
import com.bapplications.maplemobile.gameplay.model_pools.BackgroundModel;
import com.bapplications.maplemobile.opengl.GLState;
import com.bapplications.maplemobile.utils.Point;
import com.bapplications.maplemobile.pkgnx.NXNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapBackgrounds {


    private Map<String, BackgroundModel> backModelTree = new HashMap<>();
    private final List<Background> foregrounds;
    private final List<Background> backgrounds;

    private boolean black;

    public MapBackgrounds(NXNode src)
    {
        this();
        black = src.getChild("0").getChild("bS").get("").equals("");
        if(black)
            return;
        for(int i = 0 ; i < src.getChildCount() ; i++) {
            NXNode back = src.getChild(i);
            BackgroundModel model = getBackgroundModel(back);
            if ((back.getChild("front").get(0L)) > 0) // if fronted background
                foregrounds.add(new Background(back, model));
            else
                backgrounds.add(new Background(back, model));
        }

    }

    public MapBackgrounds() {
        foregrounds = new ArrayList<>();
        backgrounds = new ArrayList<>();
    }

    private BackgroundModel getBackgroundModel(NXNode src) {
        String bs = src.getChild("bS").get("") + ".img";
        String ani = src.getChild("ani").get(0L) > 0 ? "ani" : "back";
        int no = src.getChild("no").get(0L).intValue();

        String key = bs + ani + no;
        if(!backModelTree.containsKey(key)){
            backModelTree.put(key, new BackgroundModel(bs, ani, no));
        }
        return backModelTree.get(key);
    }

    public void drawBackgrounds(Point viewpos, float alpha) {
        if (black)
            GLState.drawScreenFill();
//
        for (Background background : backgrounds)
            background.draw(viewpos, alpha);
    }

    public void drawForegrounds(Point viewpos, float alpha) {
        for (Background background : foregrounds)
            background.draw(viewpos, alpha);
    }
    public void update(int deltatime) {

        for (Background background : backgrounds)
            background.update(deltatime);
        for (Background background : foregrounds)
            background.update(deltatime);
    }

}

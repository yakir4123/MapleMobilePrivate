package com.bapplications.maplemobile.game;

import com.bapplications.maplemobile.game.textures.Background;
import com.bapplications.maplemobile.opengl.GLState;
import com.bapplications.maplemobile.opengl.utils.Point;
import com.bapplications.maplemobile.pkgnx.NXNode;

import java.util.ArrayList;
import java.util.List;

public class MapBackgrounds {

    private final List<Background> foregrounds;
    private final List<Background> backgrounds;
    private boolean black;

    public MapBackgrounds(NXNode src)
    {
        this();
        for(int i = 0 ; i < src.getChildCount() ; i++) {
            NXNode back = src.getChild("" + i);
            if (((Long)back.getChild("front").get()) > 0) // if fronted background
                foregrounds.add(new Background(back));
            else
                backgrounds.add(new Background(back));
        }

        black = src.getChild("0").getChild("bS").get().equals("");
    }

    public MapBackgrounds() {
        foregrounds = new ArrayList<>();
        backgrounds = new ArrayList<>();
    }


    public void drawBackgrounds(Point viewpos, float alpha) {
//        if (black)
//            GLState.drawScreenFill();
//
        for (Background background : backgrounds)
            background.draw(viewpos, alpha);
//
//            backgrounds.get(2).draw(viewpos, alpha);
//            backgrounds.get(3).draw(viewpos, alpha);
//            backgrounds.get(4).draw(viewpos, alpha);
//            backgrounds.get(5).draw(viewpos, alpha);
//            backgrounds.get(6).draw(viewpos, alpha);
//            backgrounds.get(7).draw(viewpos, alpha);
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

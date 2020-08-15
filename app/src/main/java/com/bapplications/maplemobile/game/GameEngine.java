package com.bapplications.maplemobile.game;

import android.graphics.Rect;

import java.util.Map;

public class GameEngine {

    private Stage stage;
    private float ratio;
    private Map<Integer, Rect> viewLocations;

    public GameEngine() {
        stage = new Stage();
        startGame();
    }


    public void startGame()
    {
        stage.init();
    }


    public void drawFrame ()
    {
        stage.draw(1f);
//        UI::get().draw(alpha);

    }

    /** ratio is height/width of screen which affects where in Y coordinate to place sprites */
    public void setViewLocations ()
    {
        setViewLocations(viewLocations);
    }


    public void setViewLocations (Map<Integer, Rect> viewLocations)
    {
        this.viewLocations = viewLocations;
        if (ratio != 0)
        {
//            initHUDIcon(_asteroidIcon, _ratio, R.id.asteroid_icon);
//            initHUDText(_asteroidCountText, Integer.toString(_asteroidCount),
//                    TextSprite.TEXT_NO_ALIGN, _ratio, R.id.asteroid_count_text);
        }
    }

    public void destroy() {

    }

    public Stage getStage() {
        return stage;
    }

    public void changeMap(int mapId) {
        stage.clear();
        stage.load(mapId,  0);
    }
}

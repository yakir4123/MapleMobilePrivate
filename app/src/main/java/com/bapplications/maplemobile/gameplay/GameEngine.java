package com.bapplications.maplemobile.gameplay;

import com.bapplications.maplemobile.gameplay.player.CharEntry;

public class GameEngine {

    private GameMap map;

    public GameEngine() {
        map = new GameMap();
        startGame();
    }


    public void startGame()
    {
        map.init();
    }



    public void update(int deltatime) {
        map.update(deltatime);
    }
    public void drawFrame ()
    {
        map.draw(1f);
    }

    public void destroy() {

    }

    public GameMap getMap() {
        return map;
    }

    public void changeMap(int mapId) {
        map.changeMap(mapId);
    }

    public void loadPlayer(int charId) {
        // for development this will be a new char instead from reading from a db
        CharEntry ce = new CharEntry(charId);
        ce.look.faceid = 20000;
        ce.look.hairid = 30020;
        map.loadPlayer(ce);
    }
}

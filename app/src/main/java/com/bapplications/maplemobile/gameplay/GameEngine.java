package com.bapplications.maplemobile.gameplay;

import com.bapplications.maplemobile.constatns.Configuration;
import com.bapplications.maplemobile.gameplay.player.CharEntry;
import com.bapplications.maplemobile.gameplay.player.Player;
import com.bapplications.maplemobile.views.KeyAction;
import com.bapplications.maplemobile.views.UIControllers;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GameEngine implements UIControllers.UIKeyListener {

    private Player player;
    private GameMap currMap;
    private UIControllers controllers;
    private static GameEngine instance;
    private Map<Integer, GameMap> nextMaps;

    public static GameEngine getInstance() {
        if (instance == null)
            instance = new GameEngine();
        return instance;
    }

    private GameEngine() {
        currMap = new GameMap(new Camera());
        nextMaps = new HashMap<>();
    }


    public void startGame()
    {
        currMap.init(Configuration.START_MAP);
    }


    public void update(int deltatime) {
        currMap.update(deltatime);
    }
    public void drawFrame ()
    {
        currMap.draw(1f);
    }

    public void destroy() {

    }


    public void setControllers(UIControllers controllers) {
        this.controllers = controllers;
        this.controllers.registerListener(this);
    }

    @Override
    public void onAction(KeyAction key) {
        player.sendAction(key);
    }

    public GameMap getCurrMap() {
        return currMap;
    }


    public void changeMap() {
        changeMap(currMap.getMapId(), "sp");
    }

    public void changeMap(int mapId, String portalName) {
        if(mapId != currMap.getMapId()) {
            controllers.loading();
        }
        if (nextMaps.containsKey(mapId)) {
            currMap = nextMaps.get(mapId);
            return;
        } else {
            currMap = new GameMap(currMap.getCamera());
            currMap.init(mapId);
        }
        currMap.enterMap(player, currMap.getPortalByName(portalName));
    }

    public void loadPlayer(int charId) {
        // for development this will be a new char instead from reading from a db
        CharEntry ce = new CharEntry(charId);
        ce.look.faceid = 20000;
        ce.look.hairid = 30020;
        loadPlayer(ce);
    }

    public void notifyChangedMap() {
        notifyChangedMap(null);
    }

    public void notifyChangedMap(Set<Integer> mapids) {
//        // put currMap in NextMaps
//        if(!nextMaps.containsKey(currMap.getMapId()))
//            nextMaps.put(currMap.getMapId(), currMap);
//
//        // Remove all those maps that now not 1 way connected to currMap
//        nextMaps.entrySet().removeIf(entry -> mapids.contains(entry.getKey()));
//
//        // Load new maps
//        for (Integer mapid : mapids) {
//            if(nextMaps.containsKey(mapid))
//                continue;
//            GameMap map = new GameMap(currMap.getCamera());
//            map.loadMap(mapid);
//            nextMaps.put(mapid, map);
//        }
        controllers.finishLoading();
    }

    public void loadPlayer(CharEntry entry)
    {
        player = new Player(entry, controllers);
        controllers.setExpressions(player.getExpressions());
    }
}

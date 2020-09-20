package com.bapplications.maplemobile.gameplay;

import com.bapplications.maplemobile.constatns.Configuration;
import com.bapplications.maplemobile.gameplay.player.CharEntry;
import com.bapplications.maplemobile.gameplay.player.EquipSlot;
import com.bapplications.maplemobile.gameplay.player.Player;
import com.bapplications.maplemobile.views.KeyAction;
import com.bapplications.maplemobile.views.GameActivityUIManager;

import java.util.HashMap;
import java.util.Map;

public class GameEngine implements GameActivityUIManager.UIKeyListener {

    private Player player;
    private GameMap currMap;
    private static GameEngine instance;
    private Map<Integer, GameMap> nextMaps;
    private GameActivityUIManager controllers;

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


    public void setControllers(GameActivityUIManager controllers) {
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
            controllers.startLoadingMap();
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

        ce.look.equips = new HashMap<>();

        // magician look
//        ce.look.equips.put((byte) EquipSlot.Id.TOP.ordinal(), 1050045);
//        ce.look.equips.put((byte) EquipSlot.Id.GLOVES.ordinal(), 1082028);
//        ce.look.equips.put((byte) EquipSlot.Id.HAT.ordinal(), 1002017);
//        ce.look.equips.put((byte) EquipSlot.Id.SHIELD.ordinal(), 1092045);
//        ce.look.equips.put((byte) EquipSlot.Id.SHOES.ordinal(), 1072024);
//        ce.look.equips.put((byte) EquipSlot.Id.WEAPON.ordinal(), 1382009);

        // thief look
        ce.look.equips.put((byte) EquipSlot.Id.TOP.ordinal(), 1050018);
        ce.look.equips.put((byte) EquipSlot.Id.GLOVES.ordinal(), 1082223);
        ce.look.equips.put((byte) EquipSlot.Id.SHOES.ordinal(), 1072171);
        ce.look.equips.put((byte) EquipSlot.Id.WEAPON.ordinal(), 1472054);
        ce.look.equips.put((byte) EquipSlot.Id.HAT.ordinal(), 1002357);

        loadPlayer(ce);
    }

    public void notifyChangedMap(GameMap to) {
        controllers.finishLoadingMap();
    }

    public void loadPlayer(CharEntry entry)
    {
        player = new Player(entry, controllers);
        controllers.setExpressions(player.getExpressions());
    }
}

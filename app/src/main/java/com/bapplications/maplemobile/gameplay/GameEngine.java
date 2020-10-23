package com.bapplications.maplemobile.gameplay;

import com.bapplications.maplemobile.gameplay.player.Player;
import com.bapplications.maplemobile.constatns.Configuration;
import com.bapplications.maplemobile.gameplay.player.CharEntry;
import com.bapplications.maplemobile.input.EventsQueue;
import com.bapplications.maplemobile.input.network.NetworkHandler;
import com.bapplications.maplemobile.ui.GameActivityUIManager;
import com.bapplications.maplemobile.ui.GameViewController;

import java.util.Map;
import java.util.HashMap;

import kotlin.jvm.functions.Function1;

public class GameEngine {

    private Player player;
    private GameMap currMap;
    private static GameEngine instance;
    private NetworkHandler networkHandler;
    private Map<Integer, GameMap> nextMaps;
    private GameActivityUIManager controllers;

    public static GameEngine getInstance() {
        if (instance == null)
            instance = new GameEngine();
        return instance;
    }

    private GameEngine() {
        currMap = new GameMap(new Camera());
        networkHandler = new NetworkHandler(Configuration.HOST, Configuration.PORT);
        nextMaps = new HashMap<>();
    }


    public void startGame()
    {
        currMap.init(Configuration.START_MAP);
    }


    public void update(int deltatime) {

        // handle clicked buttons
        Map<GameViewController, Function1<Player, Boolean>> clickedButtons =
                controllers.getInputHandler().handleClick();
        Map<GameViewController, Function1<Player, Boolean>> releasedButtons =
                controllers.getInputHandler().handleReleased();
        clickedButtons.values().forEach(f -> f.invoke(getPlayer()));
        releasedButtons.values().forEach(f -> f.invoke(getPlayer()));

        EventsQueue.Companion.getInstance().dequeueAll();
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
//        ce.look.equips.put((byte) EquipSlot.Id.TOP.ordinal(), 1050018);
//        ce.look.equips.put((byte) EquipSlot.Id.HAT.ordinal(), 1002357);
//        ce.look.equips.put((byte) EquipSlot.Id.SHOES.ordinal(), 1072171);
//        ce.look.equips.put((byte) EquipSlot.Id.GLOVES.ordinal(), 1082223);
//        ce.look.equips.put((byte) EquipSlot.Id.WEAPON.ordinal(), 1472054);

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

    public Player getPlayer(){
        return player;
    }

}

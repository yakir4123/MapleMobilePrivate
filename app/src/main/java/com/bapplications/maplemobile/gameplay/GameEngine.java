package com.bapplications.maplemobile.gameplay;

import com.bapplications.maplemobile.input.EventsQueue;
import com.bapplications.maplemobile.input.events.Event;
import com.bapplications.maplemobile.gameplay.player.Player;
import com.bapplications.maplemobile.input.events.EventType;
import com.bapplications.maplemobile.constatns.Configuration;
import com.bapplications.maplemobile.ui.GameActivityUIManager;
import com.bapplications.maplemobile.gameplay.player.CharEntry;
import com.bapplications.maplemobile.input.events.EventListener;
import com.bapplications.maplemobile.input.network.NetworkHandler;
import com.bapplications.maplemobile.input.events.PlayerConnectEvent;
import com.bapplications.maplemobile.input.network.NetworkHandlerDemo;
import com.bapplications.maplemobile.input.events.PlayerConnectedEvent;
import com.bapplications.maplemobile.ui.interfaces.GameEngineListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class GameEngine implements EventListener {

    private Camera camera;
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
        camera = new Camera();
        currMap = new GameMap(camera);
        networkHandler = new NetworkHandler(Configuration.HOST, Configuration.PORT);
//        new NetworkHandlerDemo();
        EventsQueue.Companion.getInstance().registerListener(EventType.PlayerConnected, this);
        nextMaps = new HashMap<>();
    }


    public void startGame()
    {
        currMap.init(Configuration.START_MAP);
        listeners.forEach(listener -> listener.onGameStarted());
    }


    public void update(int deltatime) {

        EventsQueue.Companion.getInstance().dequeueAll();
        currMap.update(deltatime);
    }
    public void drawFrame ()
    {
        currMap.draw(1f);
    }

    public void destroy() {

    }

    public GameMap getCurrMap() {
        return currMap;
    }

    public void initMap() {
        initMap(currMap.getMapId());
    }

    public void initMap(int mapId) {
        if(mapId != currMap.getMapId()) {
            listeners.forEach(listener -> listener.onChangedMap(mapId));
        }
        if (nextMaps.containsKey(mapId)) {
            currMap = nextMaps.get(mapId);
        } else {
            currMap = new GameMap(camera);
            currMap.init(mapId);
        }
    }

    public void changeMap(int mapId, String portalName) {
        initMap(mapId);
        currMap.enterMap(player, currMap.getPortalByName(portalName));
    }

    public void loadPlayer() {

        EventsQueue.Companion.getInstance().enqueue(new PlayerConnectEvent(0));

        if(false) {
            // for development this will be a new char instead from reading from a db
            CharEntry ce = new CharEntry(0);
            ce.look.faceid = 20000;
            ce.look.hairid = 30020;

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
    }

    public void loadPlayer(CharEntry entry)
    {
        player = new Player(entry);
    }

    public Player getPlayer(){
        return player;
    }

    public Camera getCamera() {
        return camera;
    }

    @Override
    public void onEventReceive(@NotNull Event event) {
        switch (event.getType()){
            case PlayerConnected:
                PlayerConnectedEvent _event = (PlayerConnectedEvent) event;
                CharEntry ce = new CharEntry(_event.getCharid());
                ce.look.hairid = _event.getHair();
                ce.look.faceid = _event.getFace();
                ce.look.skin = (byte) _event.getSkin();
                loadPlayer(ce);
                listeners.forEach(listener -> listener.onPlayerLoaded(player));
                currMap.enterMap(player, currMap.getPortalByName("sp"));
                break;
        }
    }

    public List<GameEngineListener> listeners = new ArrayList<>();

    public void registerListener(GameEngineListener listener) {
        listeners.add(listener);
    }

    public void removeListener(GameEngineListener listener) {
        listeners.remove(listener);
    }

}

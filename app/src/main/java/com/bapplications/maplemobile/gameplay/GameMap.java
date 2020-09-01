package com.bapplications.maplemobile.gameplay;

import android.util.Log;

import com.bapplications.maplemobile.StaticUtils;
import com.bapplications.maplemobile.constatns.Loaded;
import com.bapplications.maplemobile.gameplay.audio.Sound;
import com.bapplications.maplemobile.gameplay.map.Layer;
import com.bapplications.maplemobile.gameplay.map.MapBackgrounds;
import com.bapplications.maplemobile.gameplay.map.MapInfo;
import com.bapplications.maplemobile.gameplay.map.MapMobs;
import com.bapplications.maplemobile.gameplay.map.MapPortals;
import com.bapplications.maplemobile.gameplay.map.MapTilesObjs;
import com.bapplications.maplemobile.gameplay.audio.Music;
import com.bapplications.maplemobile.gameplay.map.Portal;
import com.bapplications.maplemobile.gameplay.mobs.MobSpawn;
import com.bapplications.maplemobile.gameplay.physics.Physics;
import com.bapplications.maplemobile.gameplay.player.Player;
import com.bapplications.maplemobile.opengl.utils.Point;
import com.bapplications.maplemobile.pkgnx.NXNode;
import com.bapplications.maplemobile.views.KeyAction;

public class GameMap{


    private int mapid;
    private State state;
    private MapMobs mobs;
    private Player player;
    private Camera camera;
    private MapInfo mapInfo;
    private Physics physics;
    private MapPortals portals;
    private MapTilesObjs tilesobjs;
    private MapBackgrounds backgrounds;

    public enum State
    {
        INACTIVE,
        TRANSITION,
        ACTIVE
    };
    
    public GameMap(Camera camera)
    {
        state = State.INACTIVE;
        this.camera = camera;
    }

    public void init(int mapid){
        // drops.init();
        state = State.ACTIVE;
        loadMap(mapid);
    }

    public void spawnMobs(NXNode src) {
        int oid = 100; // todo: needs a way to calculate that
        for(NXNode spawnNode : src) {
            if(!spawnNode.getChild("type").get("").equals("m")) {
                continue;
            }
            String id = spawnNode.getChild("id").get("");
            short flip = spawnNode.getChild("f").get(0L).shortValue();
            Point p = new Point(spawnNode);
            MobSpawn spawn = new MobSpawn(oid++, Integer.parseInt(id), (byte)0, (byte)0, flip, true, (byte)0, p.flipY());
            mobs.spawn(spawn);
        }
    }


    void loadMap(int mapid)
    {
        this.mapid = mapid;
        String strid = StaticUtils.extendId(mapid, 9);
        char prefix = strid.charAt(0);

        NXNode src = mapid == -1 ? null : Loaded.getFile("Map").getRoot().getChild("Map").getChild("Map" + prefix).getChild(strid + ".img");

        // in case of no map exist with this mapid
        if (src != null && !src.isNull()) {
            try {
                mobs = new MapMobs();
                tilesobjs = new MapTilesObjs(src);
                physics = new Physics(src.getChild("foothold"));
                backgrounds = new MapBackgrounds(src.getChild("back"));
                portals = new MapPortals(src.getChild("portal"), mapid);

                if (state == State.ACTIVE)
                    GameEngine.getInstance().notifyNewMaps(portals.getNextMaps());
                mapInfo = new MapInfo(src, physics.getFHT().getWalls(), physics.getFHT().getBorders());

                spawnMobs(src.getChild("life"));
            } catch (Exception e) {
                Log.e("GameMap", "Error loading map " + mapid);
                throw e;
            }
        }
    }


    public void respawn(int portalid)
    {
        Music.play(mapInfo.getBgm());

//        Point<int16_t> spawnpoint = portals.get_portal_by_id(portalid);
        Point startpos = physics.getYBelow(new Point());
        player.respawn(new Point(0, 200), mapInfo.isUnderwater());
        camera.setView(mapInfo.getWalls(), mapInfo.getBorders());
        camera.update(player.getPosition().negateSign());
    }

    public void update(int deltatime) {
        if (state != State.ACTIVE)
            return;

////        combat.update();
        backgrounds.update(deltatime);
////        effect.update();
        tilesobjs.update(deltatime);
//
////        reactors.update(physics);
////        npcs.update(physics);
        mobs.update(physics, deltatime);
//        chars.update(physics);
////        drops.update(physics);
        player.update(physics, deltatime);
        portals.update(player.getPosition(), deltatime);
        camera.update(player.getPosition());

        if (!player.isClimbing()/* && !player.is_sitting()*/ && !player.isAttacking())
        {
            if (player.isPressed(KeyAction.UP_ARROW_KEY) && !player.isPressed(KeyAction.DOWN_ARROW_KEY))
                checkLadders(true);

            if (player.isPressed(KeyAction.DOWN_ARROW_KEY))
                checkLadders(false);

            if (player.isPressed(KeyAction.UP_ARROW_KEY))
                checkPortals();


//            if (player.isPressed(KeyAction.SIT))
//            check_seats();

//            if (player.isPressed(KeyAction.ATTACK))
//            combat.use_move(0);
//
//            if (player.isPressed(KeyAction.PICKUP))
//            check_drops();
        }

    }

    private void checkPortals() {
        if (player.isAttacking())
            return;

        Point playerpos = player.getPosition();
        Portal.WarpInfo warpinfo = portals.findWarpAt(playerpos);

        if (warpinfo.intramap)
        {
            Point spawnpoint = portals.getPortalByName(warpinfo.toname);
            Point startpos = physics.getYBelow(spawnpoint);

            player.respawn(startpos, mapInfo.isUnderwater());
        }
        else if (warpinfo.valid)
        {
            (new Sound(Sound.Name.PORTAL)).play();
            GameEngine.getInstance().changeMap(warpinfo.mapid);
        }
    }

    public void draw(float alpha)
    {
        if (state != State.ACTIVE)
            return;

        Point viewpos = camera.position(alpha);

        backgrounds.drawBackgrounds(viewpos, alpha);

        for (Layer id : Layer.values())
        {
            tilesobjs.draw(id, viewpos, alpha);
            physics.draw(viewpos);
//            reactors.draw(id, viewx, viewy, alpha);
//            npcs.draw(id, viewx, viewy, alpha);
            mobs.draw(id, viewpos, alpha);
//            chars.draw(id, viewx, viewy, alpha);
            player.draw(id, viewpos, alpha);
//            drops.draw(id, viewx, viewy, alpha);
        }
//
//        combat.draw(viewx, viewy, alpha);
        portals.draw(viewpos, alpha);
        backgrounds.drawForegrounds(viewpos, alpha);
//        effect.draw();
    }


    public void clear()
    {
        state = State.INACTIVE;
//        Texture.clear();
//        chars.clear();
//        npcs.clear();
//        mobs.clear();
//        drops.clear();
//        reactors.clear();
    }

    public int getMapId() {
        return mapid;
    }

    public Camera getCamera() {
        return camera;
    }

    public Player getPlayer() {
        return player;
    }

    public State getState() {
        return state;
    }

    public void checkLadders(boolean up)
    {
        if (!player.canClimb() || player.isClimbing() || player.isAttacking())
            return;

        player.setLadder(mapInfo.findLadder(player.getPosition(), up));
    }


    public void enterMap(Player player) {
        this.player = player;
        state = State.ACTIVE;
        respawn(0);
        GameEngine.getInstance().notifyNewMaps(portals.getNextMaps());
    }

}

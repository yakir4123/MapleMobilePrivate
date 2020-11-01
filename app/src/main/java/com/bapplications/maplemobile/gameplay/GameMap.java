package com.bapplications.maplemobile.gameplay;

import android.util.Log;

import com.bapplications.maplemobile.gameplay.map.map_objects.MapCharacters;
import com.bapplications.maplemobile.input.EventsQueue;
import com.bapplications.maplemobile.input.events.Event;
import com.bapplications.maplemobile.input.events.EventListener;
import com.bapplications.maplemobile.input.events.EventType;
import com.bapplications.maplemobile.input.events.ItemDroppedEvent;
import com.bapplications.maplemobile.utils.Rectangle;
import com.bapplications.maplemobile.utils.StaticUtils;
import com.bapplications.maplemobile.constatns.Loaded;
import com.bapplications.maplemobile.gameplay.audio.Sound;
import com.bapplications.maplemobile.gameplay.map.map_objects.Drop;
import com.bapplications.maplemobile.gameplay.map.map_objects.DropSpawn;
import com.bapplications.maplemobile.gameplay.map.Layer;
import com.bapplications.maplemobile.gameplay.map.map_objects.MapBackgrounds;
import com.bapplications.maplemobile.gameplay.map.map_objects.MapDrops;
import com.bapplications.maplemobile.gameplay.map.MapInfo;
import com.bapplications.maplemobile.gameplay.map.map_objects.MapMobs;
import com.bapplications.maplemobile.gameplay.map.map_objects.MapPortals;
import com.bapplications.maplemobile.gameplay.map.map_objects.MapTilesObjs;
import com.bapplications.maplemobile.gameplay.audio.Music;
import com.bapplications.maplemobile.gameplay.map.Portal;
import com.bapplications.maplemobile.gameplay.map.map_objects.mobs.Attack;
import com.bapplications.maplemobile.gameplay.map.map_objects.mobs.MobSpawn;
import com.bapplications.maplemobile.gameplay.physics.Physics;
import com.bapplications.maplemobile.gameplay.player.Player;
import com.bapplications.maplemobile.utils.Point;
import com.bapplications.maplemobile.pkgnx.NXNode;
import com.bapplications.maplemobile.input.InputAction;

import org.jetbrains.annotations.NotNull;

public class GameMap implements EventListener {


    private int mapid;
    private State state;
    private MapMobs mobs;
    private Player player;
    private Camera camera;
    private MapDrops drops;
    private MapInfo mapInfo;
    private Physics physics;
    private MapPortals portals;
    private MapTilesObjs tilesobjs;
    private MapCharacters characters;
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
        EventsQueue.Companion.getInstance().registerListener(EventType.ItemDropped, this);
    }

    public void init(int mapid){
        // drops.init();
        loadMap(mapid);
    }

    @Override
    public void onEventReceive(@NotNull Event event) {
        switch (event.getType()) {
            case ItemDropped:
                ItemDroppedEvent _event = (ItemDroppedEvent) event;
                if(mapid == _event.getMapId()) {
                    spawnItemDrop(_event.getOid(), _event.getId(), _event.getStart(), _event.getOwner());
                }
                break;
        }
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

    public void spawnItemDrop(int oid, int id, Point start, int owner) {
        DropSpawn spawn = new DropSpawn(oid, id, id == 0, owner, start, Drop.State.DROPPED, true );
        drops.spawn(spawn);
    }

    void loadMap(int mapid)
    {
        this.mapid = mapid;
        String strid = StaticUtils.extendId(mapid, 9);
        char prefix = strid.charAt(0);

        NXNode src = mapid == -1 ? null : Loaded.getFile(Loaded.WzFileName.MAP).getRoot().getChild("Map").getChild("Map" + prefix).getChild(strid + ".img");

        // in case of no map exist with this mapid
        if (src != null && !src.isNotExist()) {
            try {
                mobs = new MapMobs();
                drops = new MapDrops();
                characters = new MapCharacters();
                physics = new Physics(src.getChild("foothold"));
                backgrounds = new MapBackgrounds(src.getChild("back"));
                portals = new MapPortals(src.getChild("portal"), mapid);

                mapInfo = new MapInfo(src, physics.getFHT().getWalls(), physics.getFHT().getBorders());
                tilesobjs = new MapTilesObjs(src, new Rectangle(mapInfo.getWalls(), mapInfo.getBorders()));
                spawnMobs(src.getChild("life"));
            } catch (Exception e) {
                Log.e("GameMap", "Error loading map " + mapid);
                throw e;
            }
        }
    }


    public void respawn(Point position)
    {
        Music.play(mapInfo.getBgm());

//        Point<int16_t> spawnpoint = portals.get_portal_by_id(portalid);
        Point startpos = physics.getYBelow(position);
        player.respawn(startpos, mapInfo.isUnderwater());
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
        characters.update(physics, deltatime);
        drops.update(physics, deltatime);
        player.update(physics, deltatime);
        portals.update(player.getPosition(), deltatime);
        camera.update(player.getPosition());

        if (!player.isClimbing()/* && !player.is_sitting()*/ && !player.isAttacking())
        {
            if (player.isPressed(InputAction.UP_ARROW_KEY) && !player.isPressed(InputAction.DOWN_ARROW_KEY))
                checkLadders(true);

            if (player.isPressed(InputAction.DOWN_ARROW_KEY))
                checkLadders(false);

            if (player.isPressed(InputAction.UP_ARROW_KEY))
                checkPortals();


//            if (player.isPressed(InputAction.SIT))
//            check_seats();

//            if (player.isPressed(InputAction.ATTACK))
//            combat.use_move(0);
//
//            if (player.isPressed(InputAction.PICKUP))
//            check_drops();
        }


        if (player.isInvincible())
            return;

        int oid = mobs.findColliding(player);
        if (oid != 0)
        {
            Attack.MobAttack attack = mobs.createAttack(oid);
            if (attack.isValid())
            {
                Attack.MobAttackResult result = player.damage(attack);
            }
        }

    }

    public Portal getPortalByName(String portalName) {
        return portals.getPortalByName(portalName);
    }

    private void checkPortals() {
        if (player.isAttacking())
            return;

        Point playerpos = player.getPosition();
        Portal.WarpInfo warpinfo = portals.findWarpAt(playerpos);

        if (warpinfo.intramap)
        {
            Portal spawnpoint = portals.getPortalByName(warpinfo.toname);
            Point startpos = physics.getYBelow(spawnpoint.getSpawnPosition());

            player.respawn(startpos, mapInfo.isUnderwater());
        }
        else if (warpinfo.valid)
        {
            (new Sound(Sound.Name.PORTAL)).play();
            GameEngine.getInstance().changeMap(warpinfo.mapid, warpinfo.toname);
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
            tilesobjs.draw(id, camera.getPositionRect(), viewpos, alpha);
            physics.draw(viewpos);
//            reactors.draw(id, viewx, viewy, alpha);
//            npcs.draw(id, viewx, viewy, alpha);
            mobs.draw(id, viewpos, alpha);
            characters.draw(id, viewpos, alpha);
            player.draw(id, viewpos, alpha);
            drops.draw(id, viewpos, alpha);
        }
//
//        combat.draw(viewx, viewy, alpha);
        portals.draw(viewpos, alpha);
        backgrounds.drawForegrounds(viewpos, alpha);
//        effect.draw();
        // for debugging purposes
//        camera.draw();

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


    public void enterMap(Player player, Portal portal) {
        this.player = player;
        player.setMap(this);
        state = State.ACTIVE;
        respawn(portal.getSpawnPosition());
    }

}

package com.bapplications.maplemobile.gameplay;


import com.bapplications.maplemobile.StaticUtils;
import com.bapplications.maplemobile.constatns.Loaded;
import com.bapplications.maplemobile.gameplay.audio.Sound;
import com.bapplications.maplemobile.gameplay.map.Layer;
import com.bapplications.maplemobile.gameplay.map.MapBackgrounds;
import com.bapplications.maplemobile.gameplay.map.MapInfo;
import com.bapplications.maplemobile.gameplay.map.MapPortals;
import com.bapplications.maplemobile.gameplay.map.MapTilesObjs;
import com.bapplications.maplemobile.gameplay.audio.Music;
import com.bapplications.maplemobile.gameplay.map.Portal;
import com.bapplications.maplemobile.gameplay.physics.Physics;
import com.bapplications.maplemobile.gameplay.player.CharEntry;
import com.bapplications.maplemobile.gameplay.player.Player;
import com.bapplications.maplemobile.opengl.utils.Point;
import com.bapplications.maplemobile.pkgnx.NXNode;
import com.bapplications.maplemobile.views.KeyAction;
import com.bapplications.maplemobile.views.UIControllers;

public class Stage implements UIControllers.UIKeyListener{


    private int mapid;
    private State state;
    private Player player;
    private Camera camera;
    private MapInfo mapInfo;
    private Physics physics;
    private MapPortals portals;
    private MapTilesObjs tilesobjs;
    private UIControllers controllers;
    private MapBackgrounds backgrounds;

    enum State
    {
        INACTIVE,
        TRANSITION,
        ACTIVE
    };
    
    public Stage()
    {
        state = State.INACTIVE;
        camera = new Camera();
    }

    public void init(){
        // drops.init();
    }


    public void loadPlayer(CharEntry entry)
    {
        player = new Player(entry, controllers);
        controllers.setExpressions(player.getExpressions());
//        CharStats stats = player.getStats();
//        levelBefore = stats.get_stat(MapleStat.LEVEL);
//        expBefore = stats.get_exp();
    }

    public void load(int mapid, int portalid)
    {
        switch (state)
        {
            case INACTIVE:
                load_map(mapid);
                respawn(portalid);
                break;
            case TRANSITION:
                respawn(portalid);
                break;
        }

        state = State.ACTIVE;
    }


    void load_map(int mapid)
    {
        this.mapid = mapid;

        String strid = StaticUtils.extendId(mapid, 9);
        char prefix = strid.charAt(0);

        NXNode src = mapid == -1 ? null : Loaded.getFile("Map").getRoot().getChild("Map").getChild("Map" + prefix).getChild(strid + ".img");

        // in case of no map exist with this mapid
        // todo:: fix what happend if src == null
        if (src != null) {
            tilesobjs = new MapTilesObjs(src);
            backgrounds = new MapBackgrounds(src.getChild("back"));
            physics = new Physics(src.getChild("foothold"));
            mapInfo = new MapInfo(src, physics.getFHT().getWalls(), physics.getFHT().getBorders());
            portals = new MapPortals(src.getChild("portal"), mapid);
        }
    }


    public void respawn(int portalid)
    {
        Music.play(mapInfo.getBgm());

//        Point<int16_t> spawnpoint = portals.get_portal_by_id(portalid);
        Point startpos = physics.getYBelow(new Point());
        player.respawn(new Point(0, 200), mapInfo.isUnderwater());
        camera.setView(mapInfo.getWalls(), mapInfo.getBorders());
        camera.setPosition(player.getPosition());
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
////        mobs.update(physics);
////        chars.update(physics);
////        drops.update(physics);
        player.update(physics, deltatime);

        portals.update(player.getPosition(), deltatime);
        camera.update(player.getPosition());
//        camera.setPosition(player.getPosition().negateSign());

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
            changeMap(warpinfo.mapid);
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
//            mobs.draw(id, viewx, viewy, alpha);
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


    public Camera getCamera() {
        return camera;
    }

    public Player getPlayer() {
        return player;
    }

    public void setControllers(UIControllers controllers) {
        this.controllers = controllers;
        this.controllers.registerListener(this);
    }

    @Override
    public void onAction(KeyAction key) {
        player.sendAction(key);
        switch (key){
            case JUMP_KEY:
        }
    }

    public void checkLadders(boolean up)
    {
        if (!player.canClimb() || player.isClimbing() || player.isAttacking())
            return;

        player.setLadder(mapInfo.findLadder(player.getPosition(), up));
    }


    public void changeMap(int mapId) {
        clear();
        load(mapId,  0);
    }

}

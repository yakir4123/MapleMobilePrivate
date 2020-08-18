package com.bapplications.maplemobile.gameplay;


import com.bapplications.maplemobile.StaticUtils;
import com.bapplications.maplemobile.constatns.Loaded;
import com.bapplications.maplemobile.gameplay.Map.Layer;
import com.bapplications.maplemobile.gameplay.Map.MapBackgrounds;
import com.bapplications.maplemobile.gameplay.Map.MapInfo;
import com.bapplications.maplemobile.gameplay.Map.MapTilesObjs;
import com.bapplications.maplemobile.gameplay.audio.Music;
import com.bapplications.maplemobile.gameplay.physics.Physics;
import com.bapplications.maplemobile.gameplay.textures.Texture;
import com.bapplications.maplemobile.opengl.utils.Point;
import com.bapplications.maplemobile.pkgnx.NXNode;

public class Stage {


    private int mapid;
    private State state;
    private Camera camera;
    private MapInfo mapInfo;
    private MapTilesObjs tilesobjs;
    private MapBackgrounds backgrounds;
    private Physics physics;


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
        }
    }


    public void respawn(int portalid)
    {
        Music.play(mapInfo.getBgm());

//        Point<int16_t> spawnpoint = portals.get_portal_by_id(portalid);
//        Point<int16_t> startpos = physics.get_y_below(spawnpoint);
//        player.respawn(startpos, mapinfo.is_underwater());
//        camera.set_view(mapInfo.getWalls(), mapInfo.getBorders());
        camera.setPosition(0,0);
//        camera.setPosition(0,0);
    }

    public void update(int deltatime) {
        if (state != State.ACTIVE)
            return;

//        combat.update();
        backgrounds.update(deltatime);
//        effect.update();
        tilesobjs.update(deltatime);

//        reactors.update(physics);
//        npcs.update(physics);
//        mobs.update(physics);
//        chars.update(physics);
//        drops.update(physics);
//        player.update(physics);

//        portals.update(player.get_position());
//        camera.update(player.get_position());

    }

    public void draw(float alpha)
    {
        if (state != State.ACTIVE)
            return;

        Point viewpos = camera.position(alpha);

        backgrounds.drawBackgrounds(viewpos, alpha);
//
        for (Layer.Id id : Layer.Id.values())
        {
            tilesobjs.draw(id, viewpos, alpha);
//            reactors.draw(id, viewx, viewy, alpha);
//            npcs.draw(id, viewx, viewy, alpha);
//            mobs.draw(id, viewx, viewy, alpha);
//            chars.draw(id, viewx, viewy, alpha);
//            player.draw(id, viewx, viewy, alpha);
//            drops.draw(id, viewx, viewy, alpha);
        }
//
//        combat.draw(viewx, viewy, alpha);
//        portals.draw(viewpos, alpha);
        backgrounds.drawForegrounds(viewpos, alpha);
//        effect.draw();
    }


    public void clear()
    {
        state = State.INACTIVE;
        Texture.clear();
//        chars.clear();
//        npcs.clear();
//        mobs.clear();
//        drops.clear();
//        reactors.clear();
    }


    public Camera getCamera() {
        return camera;
    }
}

package com.bapplications.maplemobile.game;


import com.bapplications.maplemobile.StaticUtils;
import com.bapplications.maplemobile.constatns.Loaded;
import com.bapplications.maplemobile.game.textures.Texture;
import com.bapplications.maplemobile.opengl.utils.Point;
import com.bapplications.maplemobile.pkgnx.NXNode;

public class Stage {


    private State state;
    private Camera camera;
    private MapTilesObjs tilesobjs;
    private MapInfo mapInfo;
    private int mapid;

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
            mapInfo = new MapInfo(src);
        }
    }


    public void respawn(int portalid)
    {
//        Music(mapinfo.get_bgm()).play();

//        Point<int16_t> spawnpoint = portals.get_portal_by_id(portalid);
//        Point<int16_t> startpos = physics.get_y_below(spawnpoint);
//        player.respawn(startpos, mapinfo.is_underwater());
        camera.set_view(mapInfo.getWalls(), mapInfo.getBorders());
    }

    public void draw(float alpha)
    {
        if (state != State.ACTIVE)
            return;

        Point viewpos = camera.position(alpha);

//        backgrounds.drawbackgrounds(viewx, viewy, alpha);
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
//        backgrounds.drawforegrounds(viewx, viewy, alpha);
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

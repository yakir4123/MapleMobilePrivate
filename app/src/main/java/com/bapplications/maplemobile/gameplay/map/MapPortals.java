package com.bapplications.maplemobile.gameplay.map;

import com.bapplications.maplemobile.constatns.Loaded;
import com.bapplications.maplemobile.gameplay.textures.Animation;
import com.bapplications.maplemobile.opengl.utils.Point;
import com.bapplications.maplemobile.pkgnx.NXNode;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MapPortals {
    private static final short WARPCD = 48;
    private short cooldown;
    private final HashMap<Byte, Portal> portalsById = new HashMap<>();
    private final HashMap<String, Byte> portalIdsByName = new HashMap<>();
    private final static HashMap<Portal.Type, Animation> animations = new HashMap<>();

    public MapPortals(NXNode src, int mapid) {
        for (NXNode sub : src)
        {
            byte portal_id = (byte)Integer.parseInt(sub.getName());

            if (portal_id < 0)
                continue;

            Portal.Type type = Portal.typeById(sub.getChild("pt").get(0L).intValue());
            String name = sub.getChild("pn").get("");
            String target_name = sub.getChild("tn").get("");
            int targeMapid = sub.getChild("tm").get(999999999L).intValue();
            Point position = new Point(sub);
            position.y *= -1;

			Animation animation = animations.get(type);
            boolean intramap = targeMapid == mapid;

            portalsById.put(portal_id,
                    new Portal(animation, type, name, intramap, position, targeMapid, target_name));

            portalIdsByName.put(name, portal_id);
        }

        cooldown = WARPCD;
    }


    public void update(Point playerpos, int deltatime)
    {
        animations.get(Portal.Type.REGULAR).update(deltatime);
        animations.get(Portal.Type.HIDDEN).update(deltatime);

        for (Portal portal : portalsById.values())
        {
            switch (portal.getType())
            {
                case HIDDEN:
                case TOUCH:
                    portal.update(playerpos);
                    break;
            }
        }

        if (cooldown > 0)
            cooldown--;
    }

    public void draw(Point viewpos, float inter)
    {
        for (Portal portal : portalsById.values())
            portal.draw(viewpos, inter);
    }


    public static void init()
    {
        NXNode src = Loaded.getFile(Loaded.WzFileName.MAP).getRoot().getChild("MapHelper.img").getChild("portal").getChild("game");

        animations.put(Portal.Type.HIDDEN, new Animation(src.getChild("ph").getChild("default").getChild("portalContinue"), "0"));
        animations.put(Portal.Type.REGULAR, new Animation(src.getChild("pv"), "0"));
    }

    public Portal.WarpInfo findWarpAt(Point playerpos) {

        if (cooldown == 0)
        {
            cooldown = WARPCD;

            for (Portal portal : portalsById.values())
            {
                if (portal.bounds().contains(playerpos))
                    return portal.getWarpInfo();
            }
        }

        return new Portal.WarpInfo();
    }

    public Portal getPortalByName(String toname) {
        Byte pid = portalIdsByName.get(toname);
        if (pid != null)
            return getPortalById(pid);
        return null;
    }

    private Portal getPortalById(Byte pid) {
        return portalsById.get(pid);
    }

    public byte getPortalIdByName(String toname) {
        return portalIdsByName.get(toname);
    }

    public Set<Integer> getNextMaps() {
        Set<Integer> nextMaps = new HashSet<>();
        for (Portal portal: portalsById.values()) {
            nextMaps.add(portal.getWarpInfo().mapid);
        }
        return nextMaps;
    }
}

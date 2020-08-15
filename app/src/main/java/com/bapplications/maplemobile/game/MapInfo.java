package com.bapplications.maplemobile.game;

import android.util.Range;

import com.bapplications.maplemobile.pkgnx.NXNode;
import com.bapplications.maplemobile.pkgnx.nodes.NXLongNode;

public class MapInfo {
    private Range<Short> mapWalls;
    private Range<Short> mapBorders;

    public MapInfo(NXNode src) {
        NXNode info = src.getChild("info");

        try {
            if (info.getChild("VRLeft").getClass() == NXLongNode.class) {
                long lower = (Long) info.getChild("VRLeft").get();
                long upper = (Long) info.getChild("VRRight").get();
                mapWalls = new Range<Short>((short) lower, (short) upper);

                lower = (Long) info.getChild("VRTop").get();
                upper = (Long) info.getChild("VRBottom").get();
                mapBorders = new Range<Short>((short) lower, (short) upper);
            }
        } catch (NullPointerException e) {
            long lower = -400;
            long upper = 400;
            mapWalls = new Range<Short>((short)lower, (short)upper);
            mapBorders = new Range<Short>((short)lower, (short)upper);

        }
//		else
//        {
//            mapwalls = walls;
//            mapborders = borders;
//        }
//
//        String bgmpath = (String) info.getChild("bgm").get();
//        int split = bgmpath.charAt('/');
//        bgm = bgmpath.substring(0, split) + ".img/" + bgmpath.substring(split + 1);
//
//        cloud = info["cloud"].get_bool();
//        fieldlimit = info["fieldLimit"];
//        hideminimap = info["hideMinimap"].get_bool();
//        mapmark = info["mapMark"];
//        swim = info["swim"].get_bool();
//        town = info["town"].get_bool();
//
//        for (auto seat : src["seat"])
//            seats.push_back(seat);
//
//        for (auto ladder : src["ladderRope"])
//            ladders.push_back(ladder);

    }

    public Range<Short> getWalls() {
        return mapWalls;
    }
    public Range<Short> getBorders() {
        return mapBorders;
    }
}

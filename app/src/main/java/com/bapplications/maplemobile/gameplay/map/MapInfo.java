package com.bapplications.maplemobile.gameplay.map;

import com.bapplications.maplemobile.utils.Range;

import com.bapplications.maplemobile.utils.Point;
import com.bapplications.maplemobile.pkgnx.NXNode;
import com.bapplications.maplemobile.pkgnx.nodes.NXLongNode;

import java.util.ArrayList;
import java.util.List;

public class MapInfo {
    private boolean swim;
    private Range mapWalls;
    private Range mapBorders;
    private final String bgm;
    private final List<Ladder> ladders = new ArrayList<>();

    public MapInfo(NXNode src, Range walls, Range borders) {
        NXNode info = src.getChild("info");
        long lower = info.getChild("VRLeft").get(0L);
        long upper = info.getChild("VRRight").get(0L);
        mapWalls = new Range(lower, upper);
        if(mapWalls.isDot() && mapWalls.getLower() == 0) {
            mapWalls = walls;
        }
        lower = info.getChild("VRTop").get(0L);
        upper = info.getChild("VRBottom").get(0L);
        mapBorders = new Range(lower, upper);
        if(mapBorders.isDot() && mapBorders.getLower() == 0){
            mapBorders = borders;
        }

        String bgmpath = info.getChild("bgm").get("");
        int split = bgmpath.indexOf('/');
        bgm = bgmpath.substring(0, split) + ".img/" + bgmpath.substring(split + 1);
//
//        cloud = info["cloud"].get_bool();
//        fieldlimit = info["fieldLimit"];
//        hideminimap = info["hideMinimap"].get_bool();
//        mapmark = info["mapMark"];
        try {
            swim = ((NXLongNode) info.getChild("swim")).getBool();
        } catch (NullPointerException | ClassCastException e){
            swim = false;
        }
//        town = info["town"].get_bool();
//
//        for (auto seat : src["seat"])
//            seats.push_back(seat);
//
        for (NXNode ladder : src.getChild("ladderRope"))
            ladders.add(new Ladder(ladder));

    }

    public Range getWalls() {
        return mapWalls;
    }
    public Range getBorders() {
        return mapBorders;
    }

    public String getBgm() {
        return bgm;
    }

    public boolean isUnderwater() {
        return swim;
    }

    public Ladder findLadder(Point position, boolean upwards) {
        for (Ladder ladder: ladders)
            if (ladder.inRange(position, upwards))
                return ladder;
        return null;
    }
}

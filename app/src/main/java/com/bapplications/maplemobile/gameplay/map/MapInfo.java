package com.bapplications.maplemobile.gameplay.map;

import android.util.Range;

import com.bapplications.maplemobile.opengl.utils.Point;
import com.bapplications.maplemobile.pkgnx.NXNode;
import com.bapplications.maplemobile.pkgnx.nodes.NXLongNode;

import java.util.ArrayList;
import java.util.List;

public class MapInfo {
    private boolean swim;
    private final String bgm;
    private Range<Short> mapWalls;
    private Range<Short> mapBorders;
    private final List<Ladder> ladders = new ArrayList<>();

    public MapInfo(NXNode src, Range walls, Range borders) {
        NXNode info = src.getChild("info");

        try {
            long lower = (Long) info.getChild("VRLeft").get();
            long upper = (Long) info.getChild("VRRight").get();
            mapWalls = new Range<Short>((short) lower, (short) upper);

            lower = (Long) info.getChild("VRTop").get();
            upper = (Long) info.getChild("VRBottom").get();
            mapBorders = new Range<Short>((short) lower, (short) upper);
        } catch (NullPointerException e) {
            mapWalls = walls;
            mapBorders = borders;

        }
        String bgmpath = (String) info.getChild("bgm").get();
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

    public Range<Short> getWalls() {
        return mapWalls;
    }
    public Range<Short> getBorders() {
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

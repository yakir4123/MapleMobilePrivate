package com.bapplications.maplemobile.gameplay.player;

import com.bapplications.maplemobile.opengl.utils.Point;

import java.util.HashMap;
import java.util.List;

public class BodyDrawInfo {
    private HashMap<Byte, Point>[] arm_positions = new HashMap[Stance.Id.values().length];
    private HashMap<Byte, Short>[] stance_delays = new HashMap[Stance.Id.values().length];
    private HashMap<Byte, Point>[] body_positions = new HashMap[Stance.Id.values().length];
    private HashMap<Byte, Point>[] hand_positions = new HashMap[Stance.Id.values().length];
    private HashMap<Byte, Point>[] head_positions = new HashMap[Stance.Id.values().length];
    private HashMap<Byte, Point>[] hair_positions = new HashMap[Stance.Id.values().length];
    private HashMap<Byte, Point>[] face_positions = new HashMap[Stance.Id.values().length];

    private HashMap<String, List<Byte>> attack_delays = new HashMap<>();
    private HashMap<String, HashMap<Byte, BodyAction>> body_actions = new HashMap<>();


    public Point getHandPosition(Stance.Id stance, byte frame) {
        return hand_positions[stance.ordinal()].get(frame);
    }

    public Point getBodyPosition(Stance.Id stance, byte frame) {
        return body_positions[stance.ordinal()].get(frame);
    }

    public Point getHeadPosition(Stance.Id stance, byte frame) {
        return head_positions[stance.ordinal()].get(frame);
    }
}

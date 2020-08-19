package com.bapplications.maplemobile.gameplay.player;

import java.util.EnumMap;
import java.util.HashMap;

public class Stance {
    public enum Id {
        NONE,
        ALERT,
        DEAD,
        FLY,
        HEAL,
        JUMP,
        LADDER,
        PRONE,
        PRONESTAB,
        ROPE,
        SHOT,
        SHOOT1,
        SHOOT2,
        SHOOTF,
        SIT,
        STABO1,
        STABO2,
        STABOF,
        STABT1,
        STABT2,
        STABTF,
        STAND1,
        STAND2,
        SWINGO1,
        SWINGO2,
        SWINGO3,
        SWINGOF,
        SWINGP1,
        SWINGP2,
        SWINGPF,
        SWINGT1,
        SWINGT2,
        SWINGT3,
        SWINGTF,
        WALK1,
        WALK2;

        public String stanceName(){
            return this.name().toLowerCase();
        }
    }
    private static Stance.Id[] map = new Stance.Id[Stance.Id.values().length];
    private static HashMap<String, Stance.Id> byName = new HashMap<>();
    static {
        for (Stance.Id stance : Stance.Id.values()) {
            map[stance.ordinal()] = stance;
            byName.put(stance.stanceName(), stance);
        }
    }

    public static Stance.Id valueOf(int stance) {
        return map[stance];
    }
    public static Stance.Id valueOf(String stance) {
        return byName.get(stance.toLowerCase());
    }

}

package com.bapplications.maplemobile.gameplay.player;

import com.bapplications.maplemobile.gameplay.player.look.Char;

import java.util.HashMap;
import java.util.Map;

public class Stance {
    public static Id byState(Char.State state) {
        return state.getStance();
    }

    public enum Id {
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
            return this.name();
        }
    }

	public static Map<String, Id> mapNames = new HashMap<>();
	public static Map<Id, String> mapStances = new HashMap<>();

    static {
        String[] names = new String[] {
            "alert",
            "dead",
            "fly",
            "heal",
            "jump",
            "ladder",
            "prone",
            "proneStab",
            "rope",
            "shot",
            "shoot1",
            "shoot2",
            "shootF",
            "sit",
            "stabO1",
            "stabO2",
            "stabOF",
            "stabT1",
            "stabT2",
            "stabTF",
            "stand1",
            "stand2",
            "swingO1",
            "swingO2",
            "swingO3",
            "swingOF",
            "swingP1",
            "swingP2",
            "swingPF",
            "swingT1",
            "swingT2",
            "swingT3",
            "swingTF",
            "walk1",
            "walk2"
        };
        int i = 0;
        for (Stance.Id stance : Stance.Id.values()) {
            if(stance.name().toLowerCase().equals(names[i].toLowerCase())) {
                mapNames.put(names[i], stance);
                mapStances.put(stance, names[i++]);
            } else {
                throw new IllegalArgumentException();
            }
        }

    }

    public static Stance.Id valueOf(String stance) {
        return mapNames.get(stance);
    }

    public static String valueOf(Stance.Id stance) {
        return mapStances.get(stance);
    }

}

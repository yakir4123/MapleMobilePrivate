package com.bapplications.maplemobile.gameplay.player;

import com.bapplications.maplemobile.gameplay.player.state.PlayerState;

import java.util.EnumMap;
import java.util.Map;

import androidx.databinding.BindingAdapter;

public class PlayerStats {
    public enum Id {
        MAX_MP,
        MAX_HP,
        HP,
        MP,
        EXP,
        LEVEL;
    }

    private final Map<Id, Short> stats = new EnumMap<>(Id.class);

    public short getStat(Id id) {
        if ( stats.containsKey(id))
            return stats.get(id);
        return 0;
    }

    public PlayerStats setStat(Id id, short val) {
        stats.put(id, val);
        return this;
    }

    public PlayerStats addStat(Id id, short val) {
        int newStat = getStat(id) + val;
        if(newStat > Short.MAX_VALUE)
            newStat = Short.MAX_VALUE;
        if(newStat < Short.MIN_VALUE)
            newStat = Short.MIN_VALUE;
        return setStat(id, (short)newStat);
    }


}

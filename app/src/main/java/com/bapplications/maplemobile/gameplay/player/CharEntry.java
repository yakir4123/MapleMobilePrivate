package com.bapplications.maplemobile.gameplay.player;

import android.util.Pair;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;

public class CharEntry {
    public StatsEntry stats;
    public LookEntry look;
    public int id;

    public CharEntry(int cid) {
        stats = new StatsEntry();
        look = new LookEntry();
        id = cid;
    }

    public class StatsEntry {
        String name;
        boolean female;
        List<Integer> petids;
        EnumMap<Id, Short> stats;
        long exp;
        int mapid;
        byte portal;
        Pair<Integer, Byte> rank;
        Pair<Integer, Byte> jobrank;
    }


    public enum Id
    {
        SKIN,
        FACE,
        HAIR,
        LEVEL,
        JOB,
        STR,
        DEX,
        INT,
        LUK,
        HP,
        MAXHP,
        MP,
        MAXMP,
        AP,
        SP,
        EXP,
        FAME,
        MESO,
        PET,
        GACHAEXP;

    };

    public static class LookEntry {
        public boolean male;
        public byte skin;
        public int faceid;
        public int hairid;
        public List<Integer> petids;
        public HashMap<Byte, Integer> equips = new HashMap();

    }

}

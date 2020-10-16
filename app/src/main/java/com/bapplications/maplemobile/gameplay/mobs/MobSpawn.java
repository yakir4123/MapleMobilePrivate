package com.bapplications.maplemobile.gameplay.mobs;

import com.bapplications.maplemobile.opengl.utils.Point;

import java.util.HashMap;
import java.util.Map;

public class MobSpawn {
    private int id;
    private int oid;
    private short fh;
    private byte mode;
    private byte team;
    private byte stance;
    private Point position;
    private boolean newspawn;

    public MobSpawn(int o, int i, byte m, byte st, short f, boolean ns, byte t, Point p){
        id = i;
        fh = f;
        oid = o;
        mode = m;
        team = t;
        stance = st;
        newspawn = ns;
        position = new Point(p);
    }

    public int getOid() {
        return oid;
    }

    public byte getMode() {
        return mode;
    }

    public Mob instantiate(Map<Integer, MobModel> mobModels) {
        MobModel model = mobModels.get(id);
        if(model == null){
            model = new MobModel(id);
            mobModels.put(id, model);
        }
        return new Mob(oid, model, mode, stance, fh, newspawn, team, position);
    }
}

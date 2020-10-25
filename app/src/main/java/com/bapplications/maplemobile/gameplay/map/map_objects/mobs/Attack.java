package com.bapplications.maplemobile.gameplay.map.map_objects.mobs;

import com.bapplications.maplemobile.utils.Point;

public class Attack {
    public enum Type {
        CLOSE,
        RANGED,
        MAGIC
    }

    public static class MobAttack {
        private int oid = 0;
        public Point origin;
        private int watk = 0;
        private int matk = 0;
        private int mobid = 0;
        private boolean valid;
        private Attack.Type type;

        public MobAttack() {valid = false; }
        public MobAttack(int watk, Point origin, int mobid, int oid) {
            this.oid = oid;
            this.watk = watk;
            this.valid = true;
            this.mobid = mobid;
            this.origin = origin;
            this.type = Attack.Type.CLOSE;
        }

        public boolean isValid() {
            return valid;
        }
    }

    public static class MobAttackResult
    {
        public int oid;
        public int mobid;
        public int damage;
        public short direction;

        public MobAttackResult(MobAttack attack, int damage, short direction){
            this.damage = damage;
            this.direction = direction;
            this.mobid = attack.mobid;
            this.oid = attack.oid;
        }
    };
}

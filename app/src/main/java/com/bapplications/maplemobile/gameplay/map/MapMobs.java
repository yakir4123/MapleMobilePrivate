package com.bapplications.maplemobile.gameplay.map;

import com.bapplications.maplemobile.gameplay.mobs.Mob;
import com.bapplications.maplemobile.gameplay.mobs.MobSpawn;
import com.bapplications.maplemobile.gameplay.physics.Physics;
import com.bapplications.maplemobile.opengl.utils.Point;

import java.util.LinkedList;
import java.util.Queue;

public class MapMobs {
    MapObjects mobs = new MapObjects();
    Queue<MobSpawn> spawns = new LinkedList<>();

    public void draw(Layer layer, Point view, float alpha)
    {
        mobs.draw(layer, view, alpha);
    }

    public void update(Physics physics, int deltatime)
    {
        while(!spawns.isEmpty())
        {
			MobSpawn spawn = spawns.poll();
            Mob mob = (Mob) mobs.get(spawn.getOid());
            if (mob != null)
            {
                byte mode = spawn.getMode();
                if (mode > 0)
                    mob.setControl(mode);
                mob.makeActive();
            } else {
                mobs.add(spawn.instantiate());
            }
        }
        mobs.update(physics, deltatime);
    }

    public void spawn(MobSpawn spawn) {
        spawns.add(spawn);
    }
}

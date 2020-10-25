package com.bapplications.maplemobile.gameplay.map.map_objects;

import com.bapplications.maplemobile.gameplay.components.ColliderComponent;
import com.bapplications.maplemobile.gameplay.map.Layer;
import com.bapplications.maplemobile.gameplay.map.MapObject;
import com.bapplications.maplemobile.gameplay.map.map_objects.mobs.Attack;
import com.bapplications.maplemobile.gameplay.map.map_objects.mobs.Mob;
import com.bapplications.maplemobile.gameplay.model_pools.MobModel;
import com.bapplications.maplemobile.gameplay.map.map_objects.mobs.MobSpawn;
import com.bapplications.maplemobile.gameplay.physics.Physics;
import com.bapplications.maplemobile.utils.Point;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;

public class MapMobs {
    MapObjects<Mob> mobs = new MapObjects<>();
    Queue<MobSpawn> spawns = new LinkedList<>();
    Map<Integer, MobModel> models = new HashMap<>();

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
                mobs.add(spawn.instantiate(models));
            }
        }
        mobs.update(physics, deltatime);
    }

    public void spawn(MobSpawn spawn) {
        spawns.add(spawn);
    }

    public int findColliding(ColliderComponent collider) {

        Optional<Mob> obj = mobs.getObjects().values().stream()
                .filter(mob -> mob.isAlive() && mob.isInRange(collider)).findAny();

        return obj.map(MapObject::getOid).orElse(0);

    }

    public Attack.MobAttack createAttack(int oid) {
        Mob mob = (Mob) mobs.get(oid);
        if (mob != null)
            return mob.createTouchAttack();
		else
            return null;
    }
}

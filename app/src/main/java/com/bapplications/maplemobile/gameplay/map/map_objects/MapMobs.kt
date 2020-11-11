package com.bapplications.maplemobile.gameplay.map.map_objects

import com.bapplications.maplemobile.gameplay.components.ColliderComponent
import com.bapplications.maplemobile.gameplay.map.Layer
import com.bapplications.maplemobile.gameplay.map.MapObject
import com.bapplications.maplemobile.gameplay.map.map_objects.mobs.Attack.MobAttack
import com.bapplications.maplemobile.gameplay.map.map_objects.mobs.Mob
import com.bapplications.maplemobile.gameplay.map.map_objects.spawns.MobSpawn
import com.bapplications.maplemobile.gameplay.model_pools.MobModel
import com.bapplications.maplemobile.gameplay.physics.Physics
import com.bapplications.maplemobile.utils.Point
import java.util.*

class MapMobs {
    var mobs = MapObjects<Mob>()
    var spawns: Queue<MobSpawn> = LinkedList()
    var models: MutableMap<Int?, MobModel?> = HashMap()

    fun draw(layer: Layer?, view: Point?, alpha: Float) {
        mobs.draw(layer!!, view!!, alpha)
    }

    fun update(physics: Physics, deltatime: Int) {
        while (!spawns.isEmpty()) {
            val spawn = spawns.poll()
            val mob = mobs[spawn.oid] as Mob?
            if (mob != null) {
                val mode = spawn.mode
                if (mode > 0) mob.setControl(mode)
                mob.makeActive()
            } else {
                mobs.add(spawn.instantiate(models))
            }
        }
        mobs.update(physics, deltatime)
    }

    fun spawn(spawn: MobSpawn) {
        spawns.add(spawn)
    }

    fun findColliding(collider: ColliderComponent?): Int {
        val obj = mobs.objects.values.stream()
                .filter { mob: Mob -> mob.isAlive && mob.isInRange(collider!!) }.findAny()
        return obj.map(MapObject::oid).orElse(0)
    }

    fun createAttack(oid: Int): MobAttack {
        val mob = mobs[oid] as Mob
        return mob.createTouchAttack()
    }
}
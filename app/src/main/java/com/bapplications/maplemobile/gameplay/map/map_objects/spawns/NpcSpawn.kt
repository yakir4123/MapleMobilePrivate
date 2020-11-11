package com.bapplications.maplemobile.gameplay.map.map_objects.spawns

import com.bapplications.maplemobile.gameplay.map.map_objects.Npc
import com.bapplications.maplemobile.gameplay.map.map_objects.mobs.Mob
import com.bapplications.maplemobile.gameplay.model_pools.MobModel
import com.bapplications.maplemobile.gameplay.physics.Physics
import com.bapplications.maplemobile.utils.Point

class NpcSpawn(val oid: Int, private val id: Int, p: Point, val flip: Boolean, val fh: Short) {
    private val position: Point = Point(p)
    fun instantiate(physics: Physics): Npc {
        val spawnpoint = physics.getYBelow(position)
        return Npc(id, oid, flip ,fh, false, spawnpoint)
    }

}
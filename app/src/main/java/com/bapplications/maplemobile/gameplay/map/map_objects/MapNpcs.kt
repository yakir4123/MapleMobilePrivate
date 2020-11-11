package com.bapplications.maplemobile.gameplay.map.map_objects

import com.bapplications.maplemobile.gameplay.map.Layer
import com.bapplications.maplemobile.gameplay.map.map_objects.spawns.MobSpawn
import com.bapplications.maplemobile.gameplay.map.map_objects.spawns.NpcSpawn
import com.bapplications.maplemobile.gameplay.physics.Physics
import com.bapplications.maplemobile.utils.Point
import java.util.*

class MapNpcs {

    private val npcs: MapObjects<Npc> = MapObjects()
    private val spawns: Queue<NpcSpawn> = LinkedList()

    fun draw(layer: Layer?, view: Point?, alpha: Float) {
        npcs.draw(layer!!, view!!, alpha)
    }

    fun update(physics: Physics, deltaTime: Int)
    {
        while (!spawns.isEmpty()) {
            val spawn: NpcSpawn = spawns.poll()
            val npc = npcs[spawn.oid]
            if (npc != null) {
                npc.makeActive()
            } else {
                npcs.add(spawn.instantiate(physics))
            }
        }
        npcs.update(physics, deltaTime)
    }

    fun spawn(spawn: NpcSpawn) {
        spawns.add(spawn)
    }

}
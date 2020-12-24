package com.bapplications.maplemobile.gameplay.map.map_objects.spawns

import com.bapplications.maplemobile.gameplay.map.map_objects.mobs.Mob
import com.bapplications.maplemobile.gameplay.model_pools.MobModel
import com.bapplications.maplemobile.utils.Point

class MobSpawn(val oid: Int, private val id: Int, val mode: Byte, private val stance: Byte, private val fh: Short, private val newspawn: Boolean, private val team: Byte, p: Point) {
    private val position: Point = Point(p)
    fun instantiate(mobModels: MutableMap<Int?, MobModel?>): Mob {
        var model = mobModels[id]
        if (model == null) {
            model = MobModel(id)
            mobModels[id] = model
        }
        return Mob(oid, model, mode, stance.toInt(), fh, newspawn, team, position)
    }

}
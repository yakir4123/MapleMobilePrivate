package com.bapplications.maplemobile.gameplay.map.map_objects

import com.bapplications.maplemobile.gameplay.map.Layer
import com.bapplications.maplemobile.gameplay.map.MapObject
import com.bapplications.maplemobile.gameplay.physics.Physics
import com.bapplications.maplemobile.utils.Point
import java.util.*

class MapObjects<T : MapObject> {
    val objects: MutableMap<Int, T> = HashMap()
    private val layers: Array<MutableSet<Int>?> = arrayOfNulls<MutableSet<Int>?>(Layer.values().size)

    fun draw(layer: Layer, view: Point, alpha: Float) {
        if (layers[layer.ordinal] == null) return
        for (oid in layers[layer.ordinal]!!) {
            val mmo = get(oid)
            if (mmo != null && mmo.isActive) mmo.draw(view, alpha)
        }
    }

    operator fun get(oid: Int): MapObject? {
        return objects[oid]
    }

    fun add(mo: T) {
        val oid = mo.oid
        val layer = mo.layer.ordinal.toByte()
        objects[oid] = mo
        addOid(layer, oid)
    }

    fun update(physics: Physics, deltatime: Int) {
        val toRemove = mutableListOf<Int>()
        for (oid in objects.keys) {
            var removeObj = false
            if (objects[oid] != null) {
                val mmo: MapObject = objects[oid] as MapObject
                val oldlayer = mmo.layer.ordinal.toByte()
                val newlayer = mmo.update(physics, deltatime)
                if (newlayer.toInt() == -1) {
                    removeObj = true
                } else if (newlayer != oldlayer) {
                    layers[oldlayer.toInt()]!!.remove(oid)
                    addOid(newlayer, oid)
                }
            } else {
                removeObj = true
            }
            if (removeObj) toRemove.add(oid)
        }
        toRemove.forEach{objects.remove(it)}
    }

    private fun addOid(layer: Byte, oid: Int) {
        if (layers[layer.toInt()] == null) {
            layers[layer.toInt()] = HashSet()
        }
        layers[layer.toInt()]!!.add(oid)
    }

    fun clear() {
        objects.clear()
        for (layer in layers) layer!!.clear()
    }
}
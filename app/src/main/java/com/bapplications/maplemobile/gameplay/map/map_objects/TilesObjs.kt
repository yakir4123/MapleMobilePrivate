package com.bapplications.maplemobile.gameplay.map.map_objects

import com.bapplications.maplemobile.gameplay.map.look.Obj
import com.bapplications.maplemobile.gameplay.map.look.Tile
import com.bapplications.maplemobile.gameplay.model_pools.ObjModel
import com.bapplications.maplemobile.gameplay.model_pools.TileModel
import com.bapplications.maplemobile.pkgnx.NXNode
import com.bapplications.maplemobile.utils.Point
import com.bapplications.maplemobile.utils.Rectangle
import com.bapplications.maplemobile.utils.TwoDIntervalTree
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class TilesObjs(src: NXNode, mapSize: Rectangle) {
    private val objModelTree: MutableMap<String, ObjModel> = HashMap()
    private val tileModelTree: MutableMap<String, TileModel> = HashMap()

    private val tiles = mutableMapOf<Byte, TwoDIntervalTree>()
    private val objs = mutableMapOf<Byte, TwoDIntervalTree>()

    private val animatedObjs = mutableListOf<Obj>()

    private fun getObjModel(src: NXNode): ObjModel {
        val oS = src.getChild<NXNode>("oS").get("").toString() + ".img"
        val l0 = src.getChild<NXNode>("l0").get("")
        val l1 = src.getChild<NXNode>("l1").get("")
        val l2 = src.getChild<NXNode>("l2").get("")
        val key = oS + l0 + l1 + l2
        if (!objModelTree.containsKey(key)) {
            objModelTree[key] = ObjModel(oS, l0, l1, l2, src.getChild<NXNode>("z").get(0L).toByte())
        }
        return objModelTree[key]!!
    }

    private fun getTileModel(src: NXNode, tileset: String): TileModel? {
        val u = src.getChild<NXNode>("u").get("")
        val no = src.getChild<NXNode>("no").get(0L).toInt()
        val key = tileset + u + no
        if (!tileModelTree.containsKey(key)) {
            tileModelTree[key] = TileModel(tileset, u, no)
        }
        return tileModelTree[key]
    }

    private fun <K, V> putOnSortedMap(orderedMap: MutableMap<K, MutableList<V>>, z: K, value: V) {
        if (!orderedMap.containsKey(z)) {
            orderedMap[z] = ArrayList()
        }
        orderedMap[z]!!.add(value)
    }

    fun draw(cameraRect: Rectangle, viewpos: Point, alpha: Float) {
        for (lobjs in objs.values) {
            for (obj in lobjs.getRectangles(cameraRect)) {
                (obj as Obj).draw(viewpos, alpha)
            }
        }
        for (ltiles in tiles.values) {
            for (tile in ltiles.getRectangles(cameraRect)) {
                (tile as Tile).draw(viewpos)
            }
        }
    }

    fun update(deltatime: Int) {
        for (obj in animatedObjs) {
            obj.update(deltatime)
        }
    }

    companion object {
        private const val TAG = "TilesObjs"
    }

    init {
        val tileset: String?
        val tilesList: MutableMap<Byte, MutableList<Tile>> = TreeMap()
        val objsList: MutableMap<Byte, MutableList<Obj>> = TreeMap()

        // order matter
        for (i in 0 until src.getChild<NXNode>("obj").childCount) {
            val model = getObjModel(src.getChild<NXNode>("obj").getChild(i))
            val obj = Obj(src.getChild<NXNode>("obj").getChild(i), model)
            val z = obj.getZ() as Byte
            putOnSortedMap(objsList, z, obj)
            if(model.animated)
                animatedObjs.add(obj)
        }

        tileset = src.getChild<NXNode>("info").getChild<NXNode>("tS").get("").toString() + ".img"
        if(tileset != ".img") {
            for (tilenode in src.getChild<NXNode>("tile")) {
                val model = getTileModel(tilenode, tileset)
                val tile = Tile(tilenode, model!!)
                val z = when (model.z) {
                    is Long -> (model.z as Long).toByte()
                    is String -> (model.z as String).toByte()
                    else -> 0.toByte()
                }
                putOnSortedMap(tilesList, z, tile)
            }
        }

        // remove objects that outside the map (there are cases like that) :/
        objsList.forEach{ (k, v) -> objsList[k] = v.filter { it.height.intersect(mapSize.height) } as MutableList<Obj> }
        tilesList.forEach{ (k, v) -> tilesList[k] = v.filter { it.height.intersect(mapSize.height) } as MutableList<Tile> }

        objsList.forEach{ (k, v) -> objsList[k] = v.filter { it.width.intersect(mapSize.width) } as MutableList<Obj> }
        tilesList.forEach{ (k, v) -> tilesList[k] = v.filter { it.width.intersect(mapSize.width) } as MutableList<Tile> }

        objsList.forEach{ (k, v) -> objs[k] = TwoDIntervalTree(v, mapSize)}
        tilesList.forEach{ (k, v) -> tiles[k] = TwoDIntervalTree(v, mapSize)}

    }

}
package com.bapplications.maplemobile.gameplay.map.map_objects

import com.bapplications.maplemobile.utils.Point
import com.bapplications.maplemobile.gameplay.textures.Texture
import com.bapplications.maplemobile.gameplay.map.look.ItemDrop

class DropSpawn(val oid: Int, val itemId: Int,
                val meso: Boolean, val owner: Int,
                val start: Point, val state: Drop.State, val playerdrop: Boolean) {

    fun isMeso() =  meso

    fun instantiate(icon : Texture) = ItemDrop(oid, owner, start, state, itemId, playerdrop, icon)

}

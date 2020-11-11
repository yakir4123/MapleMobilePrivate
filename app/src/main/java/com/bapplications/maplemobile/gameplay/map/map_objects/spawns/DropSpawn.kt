package com.bapplications.maplemobile.gameplay.map.map_objects.spawns

import com.bapplications.maplemobile.gameplay.map.look.ItemDrop
import com.bapplications.maplemobile.gameplay.map.map_objects.Drop
import com.bapplications.maplemobile.gameplay.textures.Texture
import com.bapplications.maplemobile.utils.Point

data class DropSpawn(val oid: Int, val itemId: Int,
                     val meso: Boolean, val owner: Int,
                     val start: Point, val state: Drop.State, val playerdrop: Boolean) {


    fun isMeso() =  meso

    fun instantiate(icon : Texture) = ItemDrop(oid, owner, start, state, itemId, playerdrop, icon)

}

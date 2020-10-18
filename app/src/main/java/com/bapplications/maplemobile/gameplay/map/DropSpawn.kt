package com.bapplications.maplemobile.gameplay.map

import com.bapplications.maplemobile.gameplay.textures.Texture
import com.bapplications.maplemobile.opengl.utils.Point

class DropSpawn(val oid: Int, val itemId: Int,
                val meso: Boolean, val owner: Int,
                val start: Point, val dest: Point, val state: Drop.State, val playerdrop: Boolean) {


    fun isMeso() =  meso

    fun instantiate(icon : Texture) = ItemDrop(oid, owner, start, dest, state, itemId, playerdrop, icon)

}

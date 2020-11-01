package com.bapplications.maplemobile.gameplay.map.map_objects

import com.bapplications.maplemobile.gameplay.player.CharEntry
import com.bapplications.maplemobile.gameplay.player.look.CharLook
import com.bapplications.maplemobile.utils.Point

data class CharSpawn(val cid: Int, val look: CharEntry.LookEntry, val level: Byte, val job: Short,
                     val name: String, val stance: Byte, val position: Point) {

    fun instantiate() = OtherChar(cid, CharLook(look), level, job, name, stance, position)

}

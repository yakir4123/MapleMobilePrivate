package com.bapplications.maplemobile.gameplay.map.map_objects.spawns

import com.bapplications.maplemobile.gameplay.map.map_objects.OtherChar
import com.bapplications.maplemobile.gameplay.player.CharEntry
import com.bapplications.maplemobile.gameplay.player.look.Char
import com.bapplications.maplemobile.gameplay.player.look.CharLook
import com.bapplications.maplemobile.utils.Point

data class CharSpawn(val cid: Int, val look: CharEntry.LookEntry, val level: Byte, val job: Short,
                     val name: String, val state: Char.State, val position: Point) {

    fun instantiate() = OtherChar(cid, CharLook(look), level, job, name, state, position)

}

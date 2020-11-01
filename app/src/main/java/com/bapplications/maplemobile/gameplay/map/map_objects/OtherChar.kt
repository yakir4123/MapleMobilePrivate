package com.bapplications.maplemobile.gameplay.map.map_objects

import com.bapplications.maplemobile.constatns.Configuration
import com.bapplications.maplemobile.gameplay.map.Layer
import com.bapplications.maplemobile.gameplay.physics.Physics
import com.bapplications.maplemobile.gameplay.player.look.Char
import com.bapplications.maplemobile.gameplay.player.look.CharLook
import com.bapplications.maplemobile.utils.*

class OtherChar(cid: Int, look: CharLook, level: Byte, job: Short,
                name: String, state: State, position: Point) : Char(cid, look, name){

    init {
        respawn(position, false)
        setState(state)
    }

    fun draw(layer: Layer, viewpos: Point?, alpha: Float) {
        if (layer == getLayer()) super.draw(viewpos, alpha)
    }

    override fun getStanceSpeed(): Float {
        return 1f
    }
}

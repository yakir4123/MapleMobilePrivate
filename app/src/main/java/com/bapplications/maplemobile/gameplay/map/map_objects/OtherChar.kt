package com.bapplications.maplemobile.gameplay.map.map_objects

import com.bapplications.maplemobile.gameplay.map.Layer
import com.bapplications.maplemobile.gameplay.player.look.Char
import com.bapplications.maplemobile.gameplay.player.look.CharLook
import com.bapplications.maplemobile.utils.Point

class OtherChar(cid: Int, look: CharLook, level: Byte, job: Short,
                name: String, state: State, position: Point) : Char(cid, look, name){

    init {
        respawn(position, false)
        super.state = state
    }

    override val stanceSpeed: Float
        get() = when (state) {
            State.WALK -> Math.abs(phobj.hspeed)
            State.LADDER, State.ROPE -> Math.abs(phobj.vspeed)
            else -> 1.0f
        }

    fun draw(layer: Layer, viewpos: Point, alpha: Float) {
        if (layer == getLayer()) super.draw(viewpos, alpha)
    }

    fun updateState(state: State, pos: Point) {
        position = pos
        super.state = state
    }
}

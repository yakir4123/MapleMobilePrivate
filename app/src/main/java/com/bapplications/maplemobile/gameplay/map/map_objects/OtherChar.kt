package com.bapplications.maplemobile.gameplay.map.map_objects

import com.bapplications.maplemobile.constatns.Configuration
import com.bapplications.maplemobile.gameplay.map.Layer
import com.bapplications.maplemobile.gameplay.physics.Physics
import com.bapplications.maplemobile.gameplay.player.look.Char
import com.bapplications.maplemobile.gameplay.player.look.CharLook
import com.bapplications.maplemobile.utils.*

class OtherChar(cid: Int, look: CharLook, level: Byte, job: Short,
                name: String, stance: Byte, position: Point) : Char(cid, look, name){

    override fun update(physics: Physics, deltatime: Int): Byte
    {
        return getLayer().ordinal.toByte();
    }


    fun draw(layer: Layer, viewpos: Point?, alpha: Float) {
        if (layer == getLayer()) super.draw(viewpos, alpha)
    }

    override fun getStanceSpeed(): Float {
        return 1f
    }
}

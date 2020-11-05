package com.bapplications.maplemobile.gameplay.map

import com.bapplications.maplemobile.utils.Point
import com.bapplications.maplemobile.utils.Range
import com.bapplications.maplemobile.pkgnx.NXNode
import com.bapplications.maplemobile.utils.Rectangle

class Ladder(src: NXNode) {
    var x: Short = src.getChild<NXNode>("x").get(0L).toShort()
    var y1: Short
    var y2: Short
    var isLadder: Boolean
    fun inRange(position: Point, upwards: Boolean): Boolean {
        val hor = Range(position.x - 25, position.x + 25)
        val ver = Range(y1.toFloat(), y2.toFloat())
        val y = (if (upwards) position.y + 5 else position.y - 5).toShort()
        return hor.contains(x.toFloat()) && ver.contains(y.toFloat())
    }

    fun fellOff(y: Short, downwards: Boolean): Boolean {
        val dy = (if (downwards) y - 5 else y + 5).toShort()
        return dy < y1 || y - 5 > y2
    }

    init {
        y1 = (-src.getChild<NXNode>("y1").get(0L).toShort()).toShort()
        y2 = (-src.getChild<NXNode>("y2").get(0L).toShort()).toShort()
        if (y1 > y2) {
            val t = y2
            y2 = y1
            y1 = t
        }
        isLadder = src.getChild<NXNode>("l").get(0L) > 0
    }
}
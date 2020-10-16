package com.bapplications.maplemobile.gameplay.textures

import com.bapplications.maplemobile.opengl.utils.DrawArgument
import com.bapplications.maplemobile.opengl.utils.Point
import com.bapplications.maplemobile.pkgnx.NXNode

class Tile(src: NXNode?, private val model: TileModel) {
    private val pos: Point = model.calculateDrawingPos(Point(src))
    fun draw(viewpos: Point) {
        model.draw(DrawArgument(viewpos.plus(pos)))
    }

}
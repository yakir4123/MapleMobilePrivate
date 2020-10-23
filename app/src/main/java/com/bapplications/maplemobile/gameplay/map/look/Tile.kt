package com.bapplications.maplemobile.gameplay.map.look

import com.bapplications.maplemobile.gameplay.model_pools.TileModel
import com.bapplications.maplemobile.utils.DrawArgument
import com.bapplications.maplemobile.utils.Point
import com.bapplications.maplemobile.pkgnx.NXNode

class Tile(src: NXNode?, private val model: TileModel) {
    private val pos: Point = model.calculateDrawingPos(Point(src))
    fun draw(viewpos: Point) {
        model.draw(DrawArgument(viewpos.plus(pos)))
    }

}
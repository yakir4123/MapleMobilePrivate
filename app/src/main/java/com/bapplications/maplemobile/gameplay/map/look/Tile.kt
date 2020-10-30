package com.bapplications.maplemobile.gameplay.map.look

import com.bapplications.maplemobile.utils.Point
import com.bapplications.maplemobile.pkgnx.NXNode
import com.bapplications.maplemobile.utils.DrawArgument
import com.bapplications.maplemobile.gameplay.model_pools.TileModel
import com.bapplications.maplemobile.utils.Range

class Tile (src: NXNode, private val model: TileModel) : Point.TwoDPolygon {
    private val pos: Point = model.calculateDrawingPos(Point(src))

    fun draw(viewpos: Point) {
        model.draw(DrawArgument(viewpos.plus(pos)))
    }

    override val width: Range
        get() = Range(pos.x, pos.x + model.dimenstion.x)
    override val height: Range
        get() = Range(pos.y, pos.y + model.dimenstion.y)

}
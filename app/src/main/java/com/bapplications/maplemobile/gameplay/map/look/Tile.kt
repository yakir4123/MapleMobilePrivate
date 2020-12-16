package com.bapplications.maplemobile.gameplay.map.look

import com.bapplications.maplemobile.utils.Point
import com.bapplications.maplemobile.pkgnx.NXNode
import com.bapplications.maplemobile.utils.DrawArgument
import com.bapplications.maplemobile.gameplay.model_pools.TileModel
import com.bapplications.maplemobile.utils.Range

class Tile (src: NXNode, private val model: TileModel) : Point.TwoDPolygon {
    private val pos: Point = model.calculateDrawingPos(Point(src))
    private val mHeight = Range(pos.y, pos.y + model.dimension.y)
    private val mWidth = Range(pos.x, pos.x + model.dimension.x)

    fun draw(viewpos: Point) {
        model.draw(DrawArgument(viewpos.plus(pos)))
    }

    override val width: Range
        get() = mWidth
    override val height: Range
        get() = mHeight

}
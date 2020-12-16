package com.bapplications.maplemobile.gameplay.map.look

import android.util.Log

import com.bapplications.maplemobile.gameplay.model_pools.ObjModel
import com.bapplications.maplemobile.gameplay.textures.Animation
import com.bapplications.maplemobile.pkgnx.NXNode
import com.bapplications.maplemobile.utils.DrawArgument
import com.bapplications.maplemobile.utils.Point
import com.bapplications.maplemobile.utils.Point.TwoDPolygon
import com.bapplications.maplemobile.utils.Range

class Obj(src: NXNode, model: ObjModel) : Animation(model), TwoDPolygon {
    private val dargs = DrawArgument(pos)

    val _width: Range
    val _height: Range

    override val width: Range
        get() = _width
    override val height: Range
        get() = _height

    fun draw(viewpos: Point?, alpha: Float) {
        // read on tile.kt why I doing this and not creating new DrawArgument each draw
        super.draw(dargs.plusPosition(viewpos), alpha)
        dargs.minusPosition(viewpos)
    }

    init {
        pos = Point(src)
        lookLeft = src.getChild<NXNode>("f").get(0L).toInt() == 0

        var maxXDimension: Float = Float.MIN_VALUE
        var maxYDimension: Float = Float.MIN_VALUE
        for(n in 0 until model.size()) {
            if (maxXDimension < model.dimensions(n).x) {
                maxXDimension = model.dimensions(n).x
            }
            if (maxYDimension < model.dimensions(n).y) {
                maxYDimension = model.dimensions(n).y
            }
        }
        _width = Range(pos.x, pos.x + maxXDimension)
        _height = Range(pos.y, pos.y + maxYDimension)
    }
}
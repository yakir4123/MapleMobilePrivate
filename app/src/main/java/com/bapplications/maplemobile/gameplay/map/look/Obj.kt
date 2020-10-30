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
    fun draw(viewpos: Point?, alpha: Float) {
        super.draw(DrawArgument(viewpos), alpha)
    }
    val _width: Float
    val _height: Float

    override val width: Range
        get() = Range(pos.x, pos.x + _width)
    override val height: Range
        get() = Range(pos.y, pos.y + _height)

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
        _width = maxXDimension
        _height = maxYDimension
    }
}
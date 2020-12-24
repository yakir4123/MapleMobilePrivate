package com.bapplications.maplemobile.utils

import com.bapplications.maplemobile.pkgnx.NXNode
import com.bapplications.maplemobile.constatns.Loaded
import com.bapplications.maplemobile.pkgnx.nodes.NXPointNode
import messaging.Service
import kotlin.math.pow
import kotlin.math.sqrt

class Point {
    @JvmField
    var x = 0f
    @JvmField
    var y = 0f

    constructor(p: Point) {
        x = p.x
        y = p.y
    }

    constructor(src: NXNode) {
        if (src.isNotExist) {
            x = 0f
            y = 0f
            return
        }
        if (src is NXPointNode) {
            val p = src.get(Point())
            x = p.x
            y = p.y
            return
        }
        if (src.isChildExist("origin")) {
            val o = src.getChild<NXNode>("origin").get(Point())
            x = o.x
            y = o.y
            return
        }
        if (src.isChildExist("x") && src.isChildExist("y")) {
            x = src.getChild<NXNode>("x").get(0L).toFloat()
            y = src.getChild<NXNode>("y").get(0L).toFloat()
        }
    }

    @JvmOverloads
    constructor(x: Float = 0f, y: Float = 0f) {
        this.x = x
        this.y = y
    }

    constructor(p: Service.Point) {
        x = p.x
        y = p.y
    }

    operator fun plus(p: Point): Point {
        return Point(x + p.x, y + p.y)
    }

    operator fun minus(p: Point): Point {
        return Point(x - p.x, y - p.y)
    }

    fun offsetThisY(y: Float): Point {
        this.y += y
        return this
    }

    fun offsetThisX(x: Float): Point {
        this.x += x
        return this
    }

    fun toGLRatio(): Point {
        x *= 2
        y *= 2
        return this;
    }
//    fun toGLRatio(): FloatArray {
//        return floatArrayOf(
//                2 * x ,
//                2 * y )
//    }

    fun negateSign(): Point {
        return scalarMul(-1f)
    }

    fun scalarMul(a: Float): Point {
        return Point(x * a, y * a)
    }

    fun offset(p: Point?) : Point {
        p?.let{ offset(it.x, it.y) }
        return this
    }

    fun offset(x: Float, y: Float) {
        this.x += x
        this.y += y
    }

    fun mul(o: Point): Point {
        return Point(x * o.x, y * o.y)
    }

    override fun equals(other: Any?): Boolean {
        return if(other is Point) x == other.x && y == other.y else super.equals(other)
    }

    override fun toString(): String {
        return "($x, $y)"
    }

    fun flipY(): Point {
        y *= -1f
        return this
    }

    fun flipX(): Point {
        x *= -1f
        return this
    }

    fun dist(position: Point): Float = sqrt((x - position.x).pow(2) + (y - position.y).pow(2))
    fun copy(pos: Point): Point {
        this.x = pos.x
        this.y = pos.y
        return this
    }

    interface TwoDPolygon {
        val width: Range
        val height: Range
    }
}
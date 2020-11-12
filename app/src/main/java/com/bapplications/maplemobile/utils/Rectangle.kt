package com.bapplications.maplemobile.utils

import com.bapplications.maplemobile.pkgnx.NXNode
import com.bapplications.maplemobile.pkgnx.nodes.NXBitmapNode
import com.bapplications.maplemobile.pkgnx.nodes.NXPointNode
import com.bapplications.maplemobile.utils.Point.TwoDPolygon


class Rectangle(var leftTop: Point, var rightBottom: Point) : TwoDPolygon {

    // want new instance of the points to avoid shallow copy
    init {
        leftTop = Point(leftTop)
        rightBottom = Point(rightBottom)
    }
    
    constructor(sourceLeftTop: NXPointNode, sourceRightBottom: NXPointNode) :
            this(Point(sourceLeftTop.point).flipY(), Point(sourceRightBottom.point).flipY())

    constructor(source: NXNode) :
            this(Point(source.getChild("lt")).flipY(),
                    Point(source.getChild("rb")).flipY()) {
        if(source.getChild<NXNode>("lt").isNotExist
                && source is NXBitmapNode) {
            val bmap = source.get()
            this.leftTop = Point(-bmap.width/2, bmap.height)
            this.rightBottom = Point(bmap.width/2, 0)
            bmap.recycle()
        }
    }

    constructor(horizon: Range, vertical: Range): this(Point(horizon.lower, vertical.upper), Point(horizon.upper, vertical.lower))
    
    constructor(left: Float, right: Float, top: Float, bottom: Float): this(Point(left, top), Point(right, bottom)) 

    constructor() : this(Point(), Point())

    constructor(rect: Rectangle): this(Point(rect.leftTop), Point(rect.rightBottom))

    fun width(): Float {
        return Math.abs(left() - right())
    }

    fun height(): Float {
        return Math.abs(top() - bottom())
    }

    fun left(): Float {
        return leftTop.x
    }

    fun top(): Float {
        return leftTop.y
    }

    fun right(): Float {
        return rightBottom.x
    }

    fun bottom(): Float {
        return rightBottom.y
    }

    fun center(): Point {
        return Point((left() + right()) / 2, (top() + bottom()) / 2)
    }

    operator fun contains(p: Point): Boolean {
        return !straight() && p.x >= left() && p.x <= right() && p.y <= top() && p.y >= bottom()
    }

    fun overlaps(ar: Rectangle): Boolean {
        return (get_horizontal().intersect(Range(ar.left(), ar.right()))
                && get_vertical().intersect(Range(ar.top(), ar.bottom())))
    }

    fun straight(): Boolean {
        return leftTop === rightBottom
    }

    fun empty(): Boolean {
        return leftTop.x == leftTop.y && rightBottom.x == rightBottom.y && straight()
    }

    fun get_left_top(): Point? {
        return leftTop
    }

    fun get_right_bottom(): Point? {
        return rightBottom
    }

    fun get_right_top(): Point {
        return Point(rightBottom.x, leftTop.y)
    }

    fun get_left_bottom(): Point {
        return Point(leftTop.x, rightBottom.y)
    }

    fun get_horizontal(): Range {
        return Range(left(), right())
    }

    fun get_vertical(): Range {
        return Range(top(), bottom())
    }

    fun shift(v: Point) {
        leftTop.offset(v.x, v.y)
        rightBottom.offset(v.x, v.y)
    }

    fun setLeft(`val`: Float) {
        leftTop.x = `val`
    }

    fun setRight(`val`: Float) {
        rightBottom.x = `val`
    }

    fun draw(pos: Point) {
        val args = DrawArgument(pos)
        val points = listOf<DrawableCircle>(
                DrawableCircle.createCircle(get_left_top(), Color.GREEN),
                DrawableCircle.createCircle(get_right_top(), Color.GREEN),
                DrawableCircle.createCircle(get_left_bottom(), Color.GREEN),
                DrawableCircle.createCircle(get_right_bottom(), Color.GREEN))
        for (p in points) {
            p.draw(args)
        }
    }

    override fun toString(): String {
        return "$leftTop -> $rightBottom"
    }

    override val width: Range
        get() = get_horizontal()
    override val height: Range
        get() = get_vertical()
}
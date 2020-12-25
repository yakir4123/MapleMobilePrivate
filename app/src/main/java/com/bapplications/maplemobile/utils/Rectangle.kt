package com.bapplications.maplemobile.utils

import com.bapplications.maplemobile.pkgnx.NXNode
import com.bapplications.maplemobile.pkgnx.nodes.NXPointNode
import com.bapplications.maplemobile.utils.Point.TwoDPolygon
import kotlin.math.abs


class Rectangle(var leftTop: Point, var rightBottom: Point) : TwoDPolygon {

    private lateinit var widthRange: Range
    private lateinit var heightRange: Range
    private var widthChanged = true
    private var heightChanged = true

    // want new instance of the points to avoid shallow copy
    init {
        leftTop = Point(leftTop)
        rightBottom = Point(rightBottom)
    }
    
    constructor(sourceLeftTop: NXPointNode, sourceRightBottom: NXPointNode) :
            this(Point(sourceLeftTop.point).flipY(), Point(sourceRightBottom.point).flipY())

    constructor(source: NXNode) :
            this(Point(source.getChild("lt")).flipY(), Point(source.getChild("rb")).flipY())

    constructor(horizon: Range, vertical: Range): this(Point(horizon.lower, vertical.upper), Point(horizon.upper, vertical.lower))
    
    constructor(left: Float, right: Float, top: Float, bottom: Float): this(Point(left, top), Point(right, bottom)) 

    constructor() : this(Point(), Point())

    constructor(rect: Rectangle): this(Point(rect.leftTop), Point(rect.rightBottom))

    fun width(): Float {
        return abs(left() - right())
    }

    fun height(): Float {
        return abs(top() - bottom())
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
        return (getHorizontal().intersect(Range(ar.left(), ar.right()))
                && getVertical().intersect(Range(ar.top(), ar.bottom())))
    }

    fun straight(): Boolean {
        return leftTop === rightBottom
    }

    fun empty(): Boolean {
        return leftTop.x == leftTop.y && rightBottom.x == rightBottom.y && straight()
    }

    fun getRightTop(): Point {
        return Point(rightBottom.x, leftTop.y)
    }

    fun getLeftBottom(): Point {
        return Point(leftTop.x, rightBottom.y)
    }

    fun getHorizontal(): Range {
        if(widthChanged) {
            widthRange = Range(left(), right())
            widthChanged = false;
        }
        return widthRange
    }

    fun getVertical(): Range {
        if(heightChanged) {
            heightRange = Range(top(), bottom())
            heightChanged = false;
        }
        return heightRange
    }

    fun shift(v: Point) {
        leftTop.offset(v.x, v.y)
        rightBottom.offset(v.x, v.y)
        widthChanged = true
        heightChanged = true
    }

    fun setLeft(`val`: Float) {
        leftTop.x = `val`
        widthChanged = true
    }

    fun setRight(`val`: Float) {
        rightBottom.x = `val`
        widthChanged = true
    }

    fun setTop(`val`: Float) {
        leftTop.y = `val`
        heightChanged = true
    }

    fun setBottom(`val`: Float) {
        rightBottom.y = `val`
        heightChanged = true
    }

    fun draw(pos: Point) {
        val args = DrawArgument(pos)
        val points = listOf<DrawableCircle>(
                DrawableCircle.createCircle(leftTop, Color.GREEN),
                DrawableCircle.createCircle(getRightTop(), Color.GREEN),
                DrawableCircle.createCircle(getLeftBottom(), Color.GREEN),
                DrawableCircle.createCircle(rightBottom, Color.GREEN))
        for (p in points) {
            p.draw(args)
        }
    }

    override fun toString(): String {
        return "$leftTop -> $rightBottom"
    }

    override val width: Range
        get() = getHorizontal()
    override val height: Range
        get() = getVertical()
}
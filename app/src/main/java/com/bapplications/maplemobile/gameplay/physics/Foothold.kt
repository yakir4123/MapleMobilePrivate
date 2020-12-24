package com.bapplications.maplemobile.gameplay.physics

import com.bapplications.maplemobile.constatns.Configuration
import com.bapplications.maplemobile.pkgnx.NXNode
import com.bapplications.maplemobile.utils.*

class Foothold {
    private var c1: DrawableCircle? = null
    private var c2: DrawableCircle? = null
    private var m_prev: Short
    private var m_next: Short
    var layer: Byte
        private set
    private val m_id: Short
    //    private var m_vertical: Range
//    private var m_horizontal: Range

    val top: Float
    val left: Float
    val right: Float
    val bottom: Float

    constructor(src: NXNode, id: Int, layer: Int) {
        m_prev = src.getChild<NXNode>("prev").get(0L).toShort()
        m_next = src.getChild<NXNode>("next").get(0L).toShort()
        val m_horizontal = Range(src.getChild<NXNode>("x1").get(0L).toShort().toFloat(),
                src.getChild<NXNode>("x2").get(0L).toShort().toFloat())
        val m_vertical = Range(
                (-src.getChild<NXNode>("y1").get(0L).toShort()).toFloat(),
                (-src.getChild<NXNode>("y2").get(0L).toShort()).toFloat()
        )
        left = m_horizontal.lower
        right = m_horizontal.upper
        top = -src.getChild<NXNode>("y1").get(0L).toFloat()//m_vertical.lower
        bottom = -src.getChild<NXNode>("y2").get(0L).toFloat()//m_vertical.upper

        m_id = id.toShort()
        this.layer = layer.toByte()
        if (Configuration.SHOW_FH) {
            c1 = DrawableCircle.createCircle(Point(m_horizontal.lower, m_vertical.lower), Color.RED)
            c2 = DrawableCircle.createCircle(Point(m_horizontal.upper, m_vertical.upper), Color.RED)
        }
    }

    constructor() {
        m_id = 0
        layer = 0
        m_next = 0
        m_prev = 0
        top = 0f
        left = 0f
        right = 0f
        bottom = 0f
    }

    val isWall: Boolean
        get() = m_id.toInt() != 0 && right == left

    operator fun next(): Short {
        return m_next
    }

    fun prev(): Short {
        return m_prev
    }

    fun groundBelow(x: Float): Float {
        return if (isFloor) top else slope() * (x - left) + top
    }

    fun slope(): Float {
        return if (isWall) 0.0f else (vdelta() / hdelta())
    }

    private fun hdelta(): Float {
        return right - left
    }

    private fun vdelta(): Float {
        return bottom - top
    }

    private val isFloor: Boolean
        get() = m_id.toInt() != 0 && bottom == top

    fun id(): Short {
        return m_id
    }

    fun isBlocking(vertical: Range): Boolean {
        return (vertical.contains(top) || vertical.contains(bottom)) && isWall
    }

    fun draw(viewpos: Point) {
        c1?.draw(DrawArgument(viewpos))
        c2?.draw(DrawArgument(viewpos))
    }
}
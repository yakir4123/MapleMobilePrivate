package com.bapplications.maplemobile.gameplay

import com.bapplications.maplemobile.constatns.Configuration
import com.bapplications.maplemobile.constatns.Loaded
import com.bapplications.maplemobile.utils.*

class Camera {
    private val pos: Point = Point()

    // View limits.
    var hbounds: Range? = null
    var vbounds: Range? = null

    var halfCameraWidth = 0f
    var halfCameraHeight = 0f
    var eighthCameraHeight = 0f
    var sevenEighthCameraHeight = 0f

    fun setCameraSize() {
        halfCameraWidth = Loaded.SCREEN_WIDTH / 4f
        halfCameraHeight = Loaded.SCREEN_HEIGHT / 4f
        eighthCameraHeight = Loaded.SCREEN_HEIGHT / 8f
        sevenEighthCameraHeight = Loaded.SCREEN_HEIGHT / 8f * 7
    }

    fun setView(mapwalls: Range, mapborders: Range) {
        hbounds = Range(mapwalls.lower, mapwalls.upper)
        vbounds = Range(-mapborders.upper, -mapborders.lower)
    }

    fun draw() {
        val drawPos = DrawableCircle.createCircle(Point(), Color.RED)
        drawPos.draw(DrawArgument())

        getPositionRect().draw(pos)
    }

    fun getPositionRect() : Rectangle {
        return Rectangle(Point(-pos.x - Loaded.SCREEN_WIDTH + halfCameraWidth,
                                -pos.y - Loaded.SCREEN_HEIGHT + halfCameraHeight),
                        Point(-pos.x + Loaded.SCREEN_WIDTH - halfCameraWidth,
                                -pos.y + Loaded.SCREEN_HEIGHT - halfCameraHeight))
    }

    @JvmOverloads
    fun position(alpha: Float = 1f): Point {
        return pos
    }

    fun offsetPosition(dx: Float, dy: Float) {
        pos.x += dx
        pos.y += dy
    }

    fun setPosition(p: Point) {
        pos.x = p.x
        pos.y = p.y
    }

    fun update(position: Point) {
        pos.x = -position.x
        pos.y = -(position.y + eighthCameraHeight)
        if (pos.x > -(hbounds!!.lower + halfCameraWidth - Configuration.OFFSETX)) {
            pos.x = -(hbounds!!.lower + halfCameraWidth - Configuration.OFFSETX)
        }
        if (pos.x < -(hbounds!!.upper - halfCameraWidth + Configuration.OFFSETX)) {
            pos.x = -(hbounds!!.upper - halfCameraWidth + Configuration.OFFSETX)
        }
    }

}
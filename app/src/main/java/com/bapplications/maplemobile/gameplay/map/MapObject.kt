package com.bapplications.maplemobile.gameplay.map

import com.bapplications.maplemobile.gameplay.components.ColliderComponent
import com.bapplications.maplemobile.gameplay.physics.Physics
import com.bapplications.maplemobile.gameplay.physics.PhysicsObject
import com.bapplications.maplemobile.utils.Point

abstract class MapObject protected constructor(var oid: Int, pos: Point = Point())
    : ColliderComponent {
    var isActive: Boolean
        protected set
    var phobj: PhysicsObject
        protected set

    open fun update(physics: Physics, deltatime: Int): Byte {
        physics.moveObject(phobj)
        return phobj.fhlayer
    }

    fun setPosition(x: Float, y: Float) {
        phobj.set_x(x.toInt())
        phobj.set_y(y.toInt())
    }

    fun makeActive() {
        isActive = true
    }

    fun deActivate() {
        isActive = false
    }

    open val layer: Layer
        get() = Layer.byValue(phobj.fhlayer.toInt())
    open var position: Point
        get() = phobj.position
        set(value) = setPosition(value.x, value.y)

    abstract fun draw(view: Point, alpha: Float)

    init {
        phobj = PhysicsObject()
        position = pos
        isActive = true
    }
}
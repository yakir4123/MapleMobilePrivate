package com.bapplications.maplemobile.gameplay.map.map_objects

import com.bapplications.maplemobile.constatns.Configuration
import com.bapplications.maplemobile.gameplay.components.ColliderComponent
import com.bapplications.maplemobile.gameplay.map.MapObject
import com.bapplications.maplemobile.gameplay.physics.Physics
import com.bapplications.maplemobile.gameplay.physics.PhysicsObject
import com.bapplications.maplemobile.utils.Linear
import com.bapplications.maplemobile.utils.Point
import com.bapplications.maplemobile.utils.Rectangle
import kotlin.math.cos


open class Drop(id: Int, val owner: Int,
                start: Point, var state: State,
                val playerdrop: Boolean) : MapObject(id), ColliderComponent {

    enum class State {
        DROPPED, FLOATING, PICKEDUP
    }


    private var collider = Rectangle()
    val PICKUP_TIME = 24
    val FLOAT_PIXELS = 8
    val SPIN_STEP: Float = 20F
    val OPC_STEP = 1.0f / PICKUP_TIME

    var opacity: Linear = Linear()
    var angle: Linear = Linear()
    var moved: Double = 180.0
    var baseY: Float = 0.0f
    var looter: MapObject? = null
    var onPickProcess = false

    init {
        this.setPosition(start.x, start.y + FLOAT_PIXELS)
        angle.set(0.0f)
        opacity.set(1.0f)

        when(state)
        {
            State.DROPPED -> {
                phobj.vspeed = -7f
                phobj.hspeed = (phobj.crntX() - start.x) / 48
            }
            State.FLOATING -> {
                baseY = phobj.crntY() + FLOAT_PIXELS
                phobj.type = PhysicsObject.Type.FIXATED
            }
            State.PICKEDUP -> {
                phobj.vspeed = -7.0f;
            }
        }
    }

    override fun update(physics: Physics, deltatime: Int) : Byte
    {
        physics.moveObject(phobj);

        if (state == State.DROPPED)
        {
            if (phobj.onground)
            {

                val lt = position - Point(16f, 0f)
                val rb = lt + Point(32f, 32f);
                collider = Rectangle(lt, rb)
                baseY = phobj.crntY() + FLOAT_PIXELS

                phobj.hspeed = 0.0f;
                phobj.type = PhysicsObject.Type.FIXATED;
                state = State.FLOATING
                angle.set(0.0f);
                setPosition(phobj.crntX(), phobj.crntY() + FLOAT_PIXELS);
            }
            else
            {
                angle.setPlus(SPIN_STEP)
            }
        }

        if (state == State.FLOATING)
        {
            phobj.y.set((baseY + FLOAT_PIXELS + (cos(moved) - 1.0f) * FLOAT_PIXELS / 2f).toFloat());
            moved = if (moved < 360.0f) moved + 0.08f else 0.0;
        }

        if (state == State.PICKEDUP)
        {

            val hdelta = looter?.phobj?.x?.minus(phobj.x.get())
            phobj.hspeed = ((looter?.phobj?.hspeed?.div(2.0) ?: 0.0)
                    + ((hdelta?.minus(16.0))?.div(PICKUP_TIME) ?: 0.0)).toFloat()

            opacity.setMinus(OPC_STEP)

            if (opacity.last() <= OPC_STEP)
            {
                opacity.set(1.0f);
                deActivate()

                return -1;
            }
        }
        return phobj.fhlayer;
    }

    fun expire(newState: State, lt: MapObject)
    {
        when (newState)
        {
            State.DROPPED -> state = State.PICKEDUP
            State.FLOATING -> deActivate()
            State.PICKEDUP -> {
                angle.set(0.0f);
                state = State.PICKEDUP;
                looter = lt;
                phobj.vspeed = -5.5f;
                phobj.type = PhysicsObject.Type.NORMAL;
            }
        }
    }

    override fun getCollider(): Rectangle {
        return collider
    }

    fun isPicked(): Boolean = state == State.PICKEDUP

    override fun draw(view: Point, alpha: Float) {
        if(Configuration.SHOW_DROPS_RECT) {
            collider.draw(view)
        }
    }
}
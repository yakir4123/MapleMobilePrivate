package com.bapplications.maplemobile.gameplay.player.look

import com.bapplications.maplemobile.gameplay.audio.Sound
import com.bapplications.maplemobile.gameplay.map.Ladder
import com.bapplications.maplemobile.gameplay.map.Layer
import com.bapplications.maplemobile.gameplay.map.MapObject
import com.bapplications.maplemobile.gameplay.physics.Physics
import com.bapplications.maplemobile.gameplay.player.Stance
import com.bapplications.maplemobile.gameplay.player.state.*
import com.bapplications.maplemobile.input.InputAction
import com.bapplications.maplemobile.utils.*
import java.util.*
import kotlin.collections.HashMap

abstract class Char protected constructor(o: Int, val look: CharLook, name: String?) : MapObject(o) {
    abstract val stanceSpeed: Float
    open var state: State = State.STAND
        set(value) {
            field = value
            val stance = Stance.byState(value)
            look.setStance(stance)
            val pst = getState(value)
            pst?.initialize(this)

        }
    var ladder: Ladder? = null
        set(value) {
        field = value
        if (value != null) {
            phobj.set_x(value.x.toInt())
            phobj.hspeed = 0.0f
            phobj.vspeed = 0.0f
            phobj.fhlayer = 7
            state = if (value.isLadder) State.LADDER else State.ROPE
        }
    }

    open var isAttacking = false
    @JvmField
    protected var underwater = false
    private val invincible = TimedBool()
    private val pressedButton = ArrayList<InputAction>()
    protected val timedPressedButton = TimedBoolList<InputAction>()
    open var lookLeft = true
        set(value) {
            field = value
            look.setDirection(field)
        }

    override fun draw(view: Point, alpha: Float) {
        val absp = phobj.getAbsolute(view, alpha)
        look.draw(DrawArgument(absp), alpha)
    }

    fun respawn(pos: Point, underwater: Boolean) {
        setPosition(pos.x, pos.y)
        this.underwater = underwater
        isAttacking = false
        ladder = null
    }

    override fun update(physics: Physics, deltaTime: Int): Byte {
        val pst = getState(state)
        timedPressedButton.update(deltaTime)
        if (pst != null) {
            pst.update(this)
            physics.moveObject(phobj)
            var stancespeed: Short = 0
            if (stanceSpeed >= 1.0f / deltaTime) stancespeed = (deltaTime * stanceSpeed).toShort()
            invincible.update(deltaTime)
            val aniend = look.update((stancespeed / 2).toShort())
            if (aniend && isAttacking) {
                isAttacking = false
            } else {
                pst.updateState(this)
            }
        }
        return layer.ordinal.toByte()
    }

    val walkForce: Float
        get() = 0.05f + 0.32f // * (float)(stats.get_total(EquipStat::Id::SPEED)) / 100;

    val jumpForce: Float
        get() = 1.0f + 6f // * (float)(stats.get_total(EquipStat::Id::JUMP)) / 100;

    val climbForce: Float
        get() = 1.5f //static_cast<float>(stats.get_total(EquipStat::Id::SPEED)) / 100;


    fun setExpression(expression: Expression?) {
        look.setExpression(expression)
    }

    // Player states which determine animation and state
    // Values are used in movement packets (Add one if facing left)
    enum class State(val stance: Stance.Id) {
        WALK(Stance.Id.WALK1), STAND(Stance.Id.STAND1), FALL(Stance.Id.JUMP), ALERT(Stance.Id.ALERT), PRONE(Stance.Id.PRONE), SWIM(Stance.Id.FLY), LADDER(Stance.Id.LADDER), ROPE(Stance.Id.ROPE), DIED(Stance.Id.DEAD), SIT(Stance.Id.SIT);
    }

    fun isInvincible(): Boolean {
        return invincible.isTrue
    }

    override val layer: Layer
        get() = Layer.byValue(if (isClimbing) 7 else phobj.fhlayer.toInt())

    val isClimbing: Boolean
        get() = state == State.LADDER || state == State.ROPE

    protected fun showDamage(damage: Int) {
        val start_y = (phobj.getY() - 60).toShort()
        val x = (phobj.getX() - 10).toShort()
        look.setAlerted(5000)
        invincible.setFor(2000)
    }

    protected fun getState(state: State?): PlayerState? {
        return when (state) {
            State.STAND -> standing
            State.WALK -> walking
            State.FALL -> falling
            State.PRONE -> lying
            State.LADDER, State.ROPE -> climbing
            else -> null
        }
    }

    fun playJumpSound() {
        Sound(Sound.Name.JUMP).play()
    }

    fun hasWalkInput(): Boolean {
        return isPressed(InputAction.LEFT_ARROW_KEY) || isPressed(InputAction.RIGHT_ARROW_KEY)
    }

    open fun clickedButton(key: InputAction): Boolean {
        val pst = getState(state) ?: return false
        if (pressedButton.contains(key)) {
            return true
        }
       when(key.type) {
           InputAction.Type.CONTINUES_CLICK -> pressedButton.add(key)
           InputAction.Type.TIMED_CLICK -> timedPressedButton[key] = TimedBool().setFor(key.time)
       }
        return pst.sendAction(this, key)
    }

    fun releasedButtons(key: InputAction): Boolean {
        if (!pressedButton.contains(key)) {
            return false
        }
        pressedButton.remove(key)
        return true
    }

    fun isPressed(key: InputAction): Boolean {
        return pressedButton.contains(key) || timedPressedButton.containsKey(key)
    }


    companion object {
        fun init() {
            CharLook.init()
        }

        private val lying = PlayerProneState()
        private val walking = PlayerWalkState()
        private val falling = PlayerFallState()
        private val standing = PlayerStandState()
        private val climbing = PlayerClimbState()
        //    private static PlayerSitState sitting = new PlayerStandState();
        //    private static PlayerFlyState flying = new PlayerStandState();
    }


    override fun getCollider(): Rectangle {
        var left: Int
        var right: Int
        val bottom: Int
        val top: Int
        if (state === State.PRONE) {
            left = 10
            right = -50
            bottom = 0
            top = 30
        } else {
            left = -15
            right = 12
            bottom = 0
            top = 55
        }
        if (!lookLeft) {
            left *= -1
            right *= -1
        }
        return Rectangle(
                (phobj.lastX + left).toFloat(),
                (phobj.getX() + right).toFloat(),
                (phobj.lastY + bottom).toFloat(),
                (phobj.getY() + top).toFloat())
    }

}
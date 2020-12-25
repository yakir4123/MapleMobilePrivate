package com.bapplications.maplemobile.gameplay.map.map_objects.mobs

import com.bapplications.maplemobile.constatns.Configuration
import com.bapplications.maplemobile.gameplay.components.ColliderComponent
import com.bapplications.maplemobile.gameplay.map.MapObject
import com.bapplications.maplemobile.gameplay.map.map_objects.mobs.Attack.MobAttack
import com.bapplications.maplemobile.gameplay.model_pools.MobModel
import com.bapplications.maplemobile.gameplay.physics.Physics
import com.bapplications.maplemobile.gameplay.physics.PhysicsObject
import com.bapplications.maplemobile.gameplay.textures.Animation
import com.bapplications.maplemobile.utils.*
import java.util.*

class Mob(oid: Int, var mobModel: MobModel, mode: Byte, st: Int, fh: Short, newspawn: Boolean, val team: Byte, pos: Point) : MapObject(oid) {
    enum class Stance {
        MOVE, STAND, JUMP, HIT, DIE
    }

    private var stanceLength = 400 * Randomizer.nextExponential().toInt()

    enum class FlyDirection {
        STRAIGHT, UPWARDS, DOWNWARDS, NUM_DIRECTIONS
    }

    var animations: MutableMap<Stance?, Animation> = EnumMap(Stance::class.java)
    var id: Int
    var effect: Byte = 0
    var dying: Boolean
    var dead: Boolean
    var control = false
    var aggro = false
    var stance: Stance? = null
    set(value) {
        if (field != value) {
            field = value
            animations.forEach { (stance: Stance?, animation: Animation) -> animation.reset() }
        }

    }
    var lookLeft = false
    var stanceCounter: Short
    var flydirection: FlyDirection
    var walkforce = 0f
    var hppercent: Byte
    var fading: Boolean
    var fadein = false
    var opacity: Linear
    override fun draw(view: Point, alpha: Float) {
        val absp = phobj.getAbsolute(view, alpha)
        val headpos = getHeadPosition(absp)
        if (!dead) {
            val interopc = opacity[alpha]
            val dargs = DrawArgument(absp, lookLeft && !mobModel.noflip, interopc)
            animations[stance]!!.draw(dargs, alpha)
            if (Configuration.SHOW_MOBS_RECT) {
                collider.draw(view)
                var origin = DrawableCircle.createCircle(position, Color.GREEN)
                origin.draw(dargs)
                origin = DrawableCircle.createCircle(headpos, Color.BLUE)
                origin.draw(dargs)
                origin = DrawableCircle.createCircle(absp, Color.RED)
                origin.draw(dargs)
            }
        }
    }

    override fun update(physics: Physics, deltatime: Int): Byte {
        if (!isActive) return phobj.fhlayer
        val aniend = animations[stance]!!.update(deltatime)
        if (aniend && stance == Stance.DIE) dead = true
        if (fading) {
            opacity.setMinus(0.025f)
            if (opacity.last() < 0.025f) {
                opacity.set(0.0f)
                fading = false
                dead = true
            }
        } else if (fadein) {
            opacity.setPlus(0.025f)
            if (opacity.last() > 0.975f) {
                opacity.set(1.0f)
                fadein = false
            }
        }
        if (dead) {
            deActivate()
            return -1
        }

//        effects.update();
//        showhp.update();
        if (!dying) {
            if (!mobModel.canfly) {
                if (phobj.isFlagNotSet(PhysicsObject.Flag.TURN_AT_EDGES)) {
                    lookLeft = !lookLeft
                    phobj.setFlag(PhysicsObject.Flag.TURN_AT_EDGES)
                    if (stance == Stance.HIT) stance = Stance.STAND
                }
            }
            when (stance) {
                Stance.MOVE -> if (mobModel.canfly) {
                    phobj.hforce = if (lookLeft) mobModel.flyspeed else -mobModel.flyspeed
                    when (flydirection) {
                        FlyDirection.UPWARDS -> phobj.vforce = mobModel.flyspeed
                        FlyDirection.DOWNWARDS -> phobj.vforce = -mobModel.flyspeed
                    }
                } else {
                    phobj.hforce = if (lookLeft) mobModel.speed else -mobModel.speed
                }
                Stance.HIT -> if (mobModel.canmove) {
                    val KBFORCE = if (phobj.onground) 0.2 else 0.1
                    phobj.hforce = (if (lookLeft) -KBFORCE else KBFORCE).toFloat()
                }
                Stance.JUMP -> phobj.vforce = 5.0f
            }
            physics.moveObject(phobj)

//            if (control)
//            {
            stanceCounter++
            val next: Boolean
            next = when (stance) {
                Stance.HIT -> stanceCounter > 60
                Stance.JUMP -> phobj.onground
                else -> aniend && stanceCounter > stanceLength
            }
            if (next) {
                nextMove()
                //                    updateMovement();
            }
            //            }
        } else {
            phobj.normalize()
            physics.fht.updateFH(phobj)
        }
        return phobj.fhlayer
    }

    private fun nextMove() {
        if (mobModel.canmove) {
            when (stance) {
                Stance.HIT, Stance.STAND -> {
                    stance = Stance.MOVE
                    lookLeft = Randomizer.nextBoolean()
                }
                Stance.MOVE, Stance.JUMP -> if (mobModel.canjump && phobj.onground && Randomizer.below(0.25f)) {
                    stance = Stance.JUMP
                } else {
                    when (Randomizer.nextInt(3)) {
                        0 -> stance = Stance.STAND
                        1 -> {
                            stance = Stance.MOVE
                            lookLeft = false
                        }
                        2 -> {
                            stance = Stance.MOVE
                            lookLeft = true
                        }
                    }
                }
            }
            if (stance == Stance.MOVE && mobModel.canfly) flydirection = Randomizer.nextEnum(FlyDirection::class.java)
        } else {
            stance = Stance.STAND
        }
        stanceLength = 400 * Randomizer.nextExponential().toInt()
        stanceCounter = 0
    }

    private fun getHeadPosition(pos: Point): Point {
        val head = Point(animations[stance]!!.head)
        pos.offsetThisX(if (lookLeft && !mobModel.noflip) -head.x else head.x)
        pos.offsetThisY(head.y)
        return pos
    }

    private fun setStance(stancebyte: Int) {
        var stancebyte = stancebyte
        lookLeft = stancebyte % 2 == 0
        if (!lookLeft) stancebyte -= 1
        if (stancebyte < Stance.MOVE.ordinal) stancebyte = Stance.MOVE.ordinal
        stance = Stance.values()[stancebyte]
    }

    fun setControl(mode: Byte) {
        control = mode > 0
        aggro = mode.toInt() == 2
    }

    val isAlive: Boolean
        get() = isActive && !dying

    override fun getCollider(): Rectangle {
        val bounds = animations[stance]!!.bounds
        if (lookLeft) {
            bounds.setLeft(-bounds.left())
            bounds.setRight(-bounds.right())
        }
        bounds.shift(position)
        return bounds
    }

    fun isInRange(collider: ColliderComponent): Boolean {
        return if (!isActive) false else collider.collider.overlaps(getCollider())
    }

    fun createTouchAttack(): MobAttack {
        if (!mobModel.touchdamage) return MobAttack()
        val minattack = (mobModel.watk * 0.8f).toInt()
        val maxattack = mobModel.watk.toInt()
        val attack = Randomizer.nextInt(minattack, maxattack)
        return MobAttack(attack, position, id, oid)
    }

    init {
        for (stance in Stance.values()) {
            val aniModel = mobModel.animations[stance]
            if (aniModel != null) {
                animations[stance] = Animation(aniModel)
            }
        }
        opacity = Linear()
        id = mobModel.id
        position = pos
        setControl(mode)
        phobj.fhid = fh
        phobj.setFlag(PhysicsObject.Flag.TURN_AT_EDGES)
        hppercent = 0
        dying = false
        dead = false
        fading = false
        setStance(st)
        flydirection = FlyDirection.STRAIGHT
        stanceCounter = 0
        if (mobModel.canfly) phobj.type = PhysicsObject.Type.FLYING
        if (newspawn) {
            fadein = true
            opacity.set(0.0f)
        } else {
            fadein = false
            opacity.set(1.0f)
        }

//        if (control && stance == Stance.STAND)
        nextMove()
    }
}
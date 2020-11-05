package com.bapplications.maplemobile.gameplay.player

import androidx.lifecycle.LiveData
import com.bapplications.maplemobile.constatns.Configuration
import com.bapplications.maplemobile.gameplay.GameMap
import com.bapplications.maplemobile.gameplay.components.ColliderComponent
import com.bapplications.maplemobile.gameplay.map.Layer
import com.bapplications.maplemobile.gameplay.map.map_objects.mobs.Attack.MobAttack
import com.bapplications.maplemobile.gameplay.map.map_objects.mobs.Attack.MobAttackResult
import com.bapplications.maplemobile.gameplay.physics.Physics
import com.bapplications.maplemobile.gameplay.player.inventory.*
import com.bapplications.maplemobile.gameplay.player.look.Char
import com.bapplications.maplemobile.gameplay.player.look.CharLook
import com.bapplications.maplemobile.gameplay.player.look.Expression
import com.bapplications.maplemobile.gameplay.player.state.PlayerState
import com.bapplications.maplemobile.input.EventsQueue.Companion.instance
import com.bapplications.maplemobile.input.InputAction
import com.bapplications.maplemobile.input.events.*
import com.bapplications.maplemobile.input.events.EventListener
import com.bapplications.maplemobile.utils.*
import java.util.*

class Player(entry: CharEntry) : Char(entry.id, CharLook(entry.look), entry.stats.name),
        ColliderComponent, EventListener {
    var map: GameMap? = null
    var stats: PlayerViewModel? = null
        private set
    val inventory: Inventory
    private val myExpressions: TreeSet<Expression>
    private var lastUpdate: Int = 0
    fun setStats(stats: PlayerViewModel) {
        this.stats = stats
        stats.setStat(PlayerViewModel.Id.MAX_HP, 100.toShort())
        stats.setStat(PlayerViewModel.Id.MAX_MP, 50.toShort())
        stats.setStat(PlayerViewModel.Id.HP, 32.toShort())
        stats.setStat(PlayerViewModel.Id.MP, 42.toShort())
        stats.setStat(PlayerViewModel.Id.EXP, 113.toShort())
        stats.setStat(PlayerViewModel.Id.LEVEL, 12.toShort())
        stats.setStat(PlayerViewModel.Id.JOB, 220.toShort())
        stats.name.postValue("LapLap")
    }

    override fun update(physics: Physics, deltaTime: Int): Byte {
        val res = super.update(physics, deltaTime)
        lastUpdate += deltaTime
        if (lastUpdate > Configuration.UPDATE_DIFF_TIME) {
            lastUpdate -= Configuration.UPDATE_DIFF_TIME
            instance.enqueue(PlayerStateUpdateEvent(0, state!!, position))
        }
        return res
    }

    fun draw(layer: Layer, viewpos: Point, alpha: Float) {
        if (layer == getLayer()) super.draw(viewpos, alpha)
        if (Configuration.SHOW_PLAYER_RECT) {
            collider.draw(viewpos)
            val origin = DrawableCircle.createCircle(position, Color.GREEN)
            origin.draw(DrawArgument(viewpos))
        }
    }

    override val stanceSpeed: Float
        get() =//        if (attacking)
//            return get_real_attackspeed();
            when (state) {
                State.WALK -> Math.abs(phobj.hspeed)
                State.LADDER, State.ROPE -> Math.abs(phobj.vspeed)
                else -> 1.0f
            }

    override var lookLeft: Boolean = true
        set(value) {
            if (!isAttacking) super.lookLeft = value
        }

    override var state: State
        get() = super.state
        set(value){
            if (!isAttacking) {
                super.state = value
            }
        }
    fun getStat(id: PlayerViewModel.Id?): LiveData<Short> {
        return stats!!.getStat(id!!)
    }

    fun addStat(id: PlayerViewModel.Id?, `val`: Short): PlayerViewModel {
        return stats!!.addStat(id!!, `val`)
    }

    fun changeEquip(to: Slot): Boolean {
        val changed = inventory.equipItem(to.item as Equip)
        if (changed) {
            look.addEquip(to.itemId)
            inventory.getInventory(InventoryType.Id.EQUIP).popItem(to.slotId)
        }
        return changed
    }

    fun dropItem(slot: Slot): Boolean {
        val invType = slot.inventoryType
        // it actually just like slot
        val dropped = inventory.getInventory(invType).popItem(slot.slotId)
        if (dropped.isEmpty) {
            return false
        }
        instance
                .enqueue(DropItemEvent(dropped.itemId, position,
                        0, invType.ordinal, slot.slotId, map!!.mapId))
        return true
    }

    // todo change signature
    fun addItem() {
        inventory.addItem(Equip(1002357, -1L, null, 0.toShort(), 7.toByte(), 0.toByte()), 1.toShort())
        inventory.addItem(Equip(1050045, -1L, null, 0.toShort(), 7.toByte(), 0.toByte()), 1.toShort())
        inventory.addItem(Equip(1002017, -1L, null, 0.toShort(), 7.toByte(), 0.toByte()), 1.toShort())
        inventory.addItem(Equip(1092045, -1L, null, 0.toShort(), 7.toByte(), 0.toByte()), 1.toShort())
        inventory.addItem(Equip(1050087, -1L, null, 0.toShort(), 7.toByte(), 0.toByte()), 1.toShort())
        inventory.addItem(Equip(1002575, -1L, null, 0.toShort(), 7.toByte(), 0.toByte()), 1.toShort())
        inventory.addItem(Equip(1002356, -1L, null, 0.toShort(), 7.toByte(), 0.toByte()), 1.toShort())
        inventory.addItem(Equip(1050040, -1L, null, 0.toShort(), 7.toByte(), 0.toByte()), 1.toShort())
        inventory.addItem(Item(2000000, -1L, null, 0.toShort()), 76.toShort())
        inventory.addItem(Item(2000001, -1L, null, 0.toShort()), 50.toShort())
        inventory.addItem(Item(2000002, -1L, null, 0.toShort()), 100.toShort())
        inventory.addItem(Item(2002000, -1L, null, 0.toShort()), 100.toShort())
        inventory.addItem(Item(2070006, -1L, null, 0.toShort()), 800.toShort())
        inventory.addItem(Item(4020006, -1L, null, 0.toShort()), 19.toShort())
        inventory.addItem(Item(3010072, -1L, null, 0.toShort()), 1.toShort())
        inventory.addItem(Item(3010106, -1L, null, 0.toShort()), 1.toShort())
        inventory.addItem(Item(5021011, -1L, null, 0.toShort()), 1.toShort())
        inventory.addItem(Item(5030000, -1L, null, 0.toShort()), 1.toShort())
        inventory.addItem(Item(5000000, -1L, null, 0.toShort()), 1.toShort())
        inventory.addItem(Item(5000028, -1L, null, 0.toShort()), 1.toShort())
    }

    val expressions: Collection<Expression>
        get() = myExpressions

    fun canClimb(): Boolean {
        return !climb_cooldown.isTrue
    }

    fun damage(attack: MobAttack): MobAttackResult {
        val damage = 1 //stats.calculate_damage(attack.watk);
        addStat(PlayerViewModel.Id.HP, (-damage).toShort())
        showDamage(damage)
        val fromleft = attack.origin.x > phobj.getX()
        val missed = damage <= 0
        val immovable = ladder != null || state === State.DIED
        val knockback = !missed && !immovable
        if (knockback /*&& Randomizer.above(stats.getStance())*/) {
            phobj.hspeed = (if (fromleft) -1.5 else 1.5).toFloat()
            phobj.vforce -= 3.5f
        }
        val direction = (if (fromleft) 0 else 1).toByte()
        return MobAttackResult(attack, damage, direction.toShort())
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

    override fun onEventReceive(event: Event) {
        when (event.type) {
            EventType.PressButton -> {
                val (charid, buttonPressed, pressed) = event as PressButtonEvent
                if (charid == 0) {
                    if (pressed) {
                        clickedButton(InputAction.byKey(buttonPressed))
                    } else {
                        releasedButtons(InputAction.byKey(buttonPressed))
                    }
                }
            }
            EventType.ExpressionButton -> {
                val (charid, expression) = event as ExpressionButtonEvent
                if (charid == 0) {
                    setExpression(expression)
                }
            }
        }
    }

    init {
        isAttacking = false
        underwater = false
        state = State.STAND
        inventory = Inventory()
        addItem()
        myExpressions = TreeSet()
        myExpressions.addAll(Arrays.asList(*Expression.values()))
        instance.registerListener(EventType.PressButton, this)
        instance.registerListener(EventType.ExpressionButton, this)
    }
}
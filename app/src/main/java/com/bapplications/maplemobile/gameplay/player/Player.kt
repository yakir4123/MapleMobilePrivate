package com.bapplications.maplemobile.gameplay.player

import android.util.Log
import androidx.lifecycle.LiveData
import com.bapplications.maplemobile.constatns.Configuration
import com.bapplications.maplemobile.gameplay.GameMap
import com.bapplications.maplemobile.gameplay.map.Layer
import com.bapplications.maplemobile.gameplay.map.look.ItemDrop
import com.bapplications.maplemobile.gameplay.map.map_objects.Drop
import com.bapplications.maplemobile.gameplay.map.map_objects.mobs.Attack.MobAttack
import com.bapplications.maplemobile.gameplay.map.map_objects.mobs.Attack.MobAttackResult
import com.bapplications.maplemobile.gameplay.physics.Physics
import com.bapplications.maplemobile.gameplay.player.inventory.*
import com.bapplications.maplemobile.gameplay.player.look.Char
import com.bapplications.maplemobile.gameplay.player.look.CharLook
import com.bapplications.maplemobile.gameplay.player.look.Expression
import com.bapplications.maplemobile.input.EventsQueue
import com.bapplications.maplemobile.input.InputAction
import com.bapplications.maplemobile.input.events.*
import com.bapplications.maplemobile.input.events.EventListener
import com.bapplications.maplemobile.utils.*
import java.util.*

class Player(entry: CharEntry) : Char(entry.id, CharLook(entry.look), entry.stats.name),
        EventListener {
    lateinit var map: GameMap
    lateinit var stats: PlayerViewModel
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
            EventsQueue.instance.enqueue(PlayerStateUpdateEvent(0, state, position))
        }
        return res
    }

    fun draw(layer: Layer, viewpos: Point, alpha: Float) {
        if (layer == this.layer) super.draw(viewpos, alpha)
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
        return stats.getStat(id!!)
    }

    fun addStat(id: PlayerViewModel.Id?, `val`: Short): PlayerViewModel {
        return stats.addStat(id!!, `val`)
    }

    fun getEquippedInventory(): EquippedInventory {
        return inventory.equippedInventory
    }

    fun addItems() {
        val equips = listOf(1050045, 1082028, 1002017, 1092045, 1072024, 1382009, 1050018, 1002357,
                1072171, 1082223, 1472054, 1050087, 1002575, 1002356, 1050040, 1092000, 1492010,
                1492011, 1452017, 1452060, 1102006, 1102020, 1102045, 1102051, 1102073, 1102092,
                1040005, 1041143, 1042001, 1042007, 1042012, 1042018, 1060055, 1060066, 1060103,
                1060122, 1061139
        )
        for(item in equips) {
            inventory.addItem(Equip(item, -1L, null, 0.toShort(),
                    7.toByte(), 0.toByte()), 1.toShort())
        }
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

    override fun onEventReceive(event: Event) {
        when (event.type) {
            EventType.PressButton -> {
                val (charid, buttonPressed, pressed) = event as PressButtonEvent
                if (charid == 0) {
                    if (pressed) {
                        clickedButton(InputAction.byKey(buttonPressed)!!)
                    } else {
                        releasedButtons(InputAction.byKey(buttonPressed)!!)
                    }
                }
            }
            EventType.ExpressionButton -> {
                val (charid, expression) = event as ExpressionButtonEvent
                if (charid == 0) {
                    setExpression(expression)
                }
            }
            EventType.EquipItem -> {
                val (charid, slotid, itemid) = event as EquipItemEvent
                if(charid == oid) {
                    val slot = inventory.getInventory(InventoryType.Id.EQUIP).items[slotid]
                    if(itemid != slot.itemId) {
                        Log.e("Player::onEventRecieved", "EquipItem event received itemid = $itemid different than the item in slotid $slotid with item ${slot.itemId}")
                        return
                    }
                    val successfulChange = inventory.equipItem(slot.item as Equip)
                    if (successfulChange) {
                        look.addEquip(slot.itemId)
                        inventory.getInventory(InventoryType.Id.EQUIP).popItem(slot.slotId)
                    }
                }
            }
            EventType.UnequipItem -> {
                val (charid, slotid) = event as UnequipItemEvent
                if(charid == oid) {
                    val slot = inventory.getInventory(InventoryType.Id.EQUIPPED).items[slotid]
                    val changed = inventory.unequipItem(slot.item as Equip)
                    if (changed) {
                        look.removeEquip(slot.itemId)
                    }
                }
            }
        }
    }

    // ask the server if he can pick it
    fun tryPickupDrop(drop: Drop) {

        EventsQueue.instance.enqueue(PickupItemEvent(0, drop.oid, map.mapId))
        drop.onPickProcess = true
    }

    fun pickupDrop(drop: Drop) {
        resetPickupTimer()

        when(drop) {
            // should ask from the server  to pick with oid
            is ItemDrop -> {
                if(EquipData.isEquip(drop.itemId)){
                    inventory.addItem(Equip(drop.itemId, -1L, null, 0, 7, 0), 1)
                } else {
                    inventory.addItem(Item(drop.itemId, -1, null, 0), 1)
                }
            }
        }
    }

    private fun resetPickupTimer() {
        timedPressedButton[InputAction.LOOT_KEY]?.reset()
    }

    override fun clickedButton(key: InputAction): Boolean {
        val res =  super.clickedButton(key)
        if(key.key == InputAction.Key.LOOT) {
            timedPressedButton[key]?.setOnUpdate {
                stats.lootPercent.postValue(it.getPercent())
            }
        }
        return res
    }

    fun canWearItem(item: Item?): Boolean {
        return true;
    }

    fun canPickupItem(item: Item?): Boolean {
        return true
    }

    init {
        underwater = false
        isAttacking = false
        state = State.STAND

        inventory = Inventory()
        addItems()

        myExpressions = TreeSet()
        myExpressions.addAll(listOf(*Expression.values()))
        EventsQueue.instance.registerListener(EventType.PressButton, this)
        EventsQueue.instance.registerListener(EventType.ExpressionButton, this)
        EventsQueue.instance.registerListener(EventType.DropItem, this)
        EventsQueue.instance.registerListener(EventType.EquipItem, this)
        EventsQueue.instance.registerListener(EventType.UnequipItem, this)
    }
}
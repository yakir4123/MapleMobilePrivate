package com.bapplications.maplemobile.input.events

import com.bapplications.maplemobile.gameplay.player.Stance
import com.bapplications.maplemobile.gameplay.player.look.Char
import com.bapplications.maplemobile.gameplay.player.look.Expression
import com.bapplications.maplemobile.input.InputAction
import com.bapplications.maplemobile.utils.Point

enum class EventType {
    DropItem,
    ItemDropped,
    EquipItem,
    UnequipItem,
    PickupItem,

    PressButton,
    ExpressionButton,

    PlayerConnect,
    PlayerConnected,
    OtherPlayerConnected,
    PlayerStateUpdate
}

open class Event(val type: EventType)

interface EventListener {
    fun onEventReceive(event: Event)
}

// todo :: change from sending charid to username & hash(password)
data class PlayerConnectEvent(val charid: Int) : Event(EventType.PlayerConnect)
data class PlayerConnectedEvent(val charid: Int, val hair: Int,
                                val skin: Int, val face: Int) : Event(EventType.PlayerConnected)
data class OtherPlayerConnectedEvent(val charid: Int, val hair: Int,
                                     val skin: Int, val face: Int,
                                     val state: Char.State, val pos: Point) : Event(EventType.OtherPlayerConnected)
data class PlayerStateUpdateEvent(val charid: Int, val state: Char.State,
                                  val pos: Point) : Event(EventType.PlayerStateUpdate)

data class PressButtonEvent (val charid: Int, val buttonPressed: InputAction.Key,
                             val pressed: Boolean): Event(EventType.PressButton)
data class ExpressionButtonEvent (val charid: Int, val expression: Expression): Event(EventType.ExpressionButton)

data class DropItemEvent (val itemid: Int, val startDropPos: Point, val owner: Int,
               val slotId: Int, val mapId: Int) : Event(EventType.DropItem)
data class ItemDroppedEvent (val oid: Int, val id: Int, val start: Point,
                             val owner: Int, val mapId: Int): Event(EventType.ItemDropped)
data class EquipItemEvent (val charid: Int, val slotId: Int, val itemId: Int): Event(EventType.EquipItem)
data class UnequipItemEvent (val charid: Int, val slotId: Int, val itemId: Int): Event(EventType.UnequipItem)

data class PickupItemEvent(val charid: Int, val oid: Int, val mapId: Int) : Event(EventType.PickupItem)
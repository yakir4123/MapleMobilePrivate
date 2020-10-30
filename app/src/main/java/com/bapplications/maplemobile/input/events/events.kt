package com.bapplications.maplemobile.input.events

import com.bapplications.maplemobile.gameplay.player.look.Expression
import com.bapplications.maplemobile.input.InputAction
import com.bapplications.maplemobile.utils.Point

enum class EventType {
    DropItem,
    ItemDropped,
    PressButton,
    ExpressionButton
}

open class Event(val type: EventType)

abstract class EventMessage(type: EventType) : Event(type) {
    abstract fun sender(): Any
}

interface EventListener {
    fun onEventReceive(event: Event)
}

data class DropItemEvent (val itemid: Int, val startDropPos: Point, val owner: Int,
               val invType: Int, val slotId: Int, val mapId: Int) : Event(EventType.DropItem)

data class ItemDroppedEvent (val oid: Int, val id: Int, val start: Point,
                             val owner: Int, val mapId: Int): Event(EventType.ItemDropped)


data class PressButtonEvent (val buttonPressed: InputAction.Key,
                             val pressed: Boolean): Event(EventType.PressButton)

data class ExpressionButtonEvent (val expression: Expression): Event(EventType.ExpressionButton)


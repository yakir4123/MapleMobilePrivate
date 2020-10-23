package com.bapplications.maplemobile.input.events

import com.bapplications.maplemobile.utils.Point

enum class EventType {
    DropItem,
    ItemDropped
}

open class Event(val type: EventType)

abstract class EventMessage(type: EventType) : Event(type) {
    abstract fun sender(): Any
}

interface EventListener {
    fun onEventReceive(event: Event)
}

class DropItemEvent (val itemid: Int, val startDropPos: Point, val owner: Int,
               val invType: Int, val slotId: Int, val mapId: Int) : Event(EventType.DropItem)

class ItemDroppedEvent (val oid: Int, val id: Int, val start: Point,
                        val owner: Int, val mapId: Int): Event(EventType.ItemDropped)


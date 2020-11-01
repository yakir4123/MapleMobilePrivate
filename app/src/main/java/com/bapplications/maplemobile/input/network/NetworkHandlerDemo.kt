package com.bapplications.maplemobile.input.network

import com.bapplications.maplemobile.gameplay.player.look.Char
import com.bapplications.maplemobile.input.EventsQueue
import com.bapplications.maplemobile.input.events.*
import com.bapplications.maplemobile.utils.Point
import com.bapplications.maplemobile.utils.Randomizer

class NetworkHandlerDemo : EventListener {

    init {
        EventsQueue.instance.registerListener(EventType.DropItem, this)
        EventsQueue.instance.registerListener(EventType.PressButton, this)
        EventsQueue.instance.registerListener(EventType.PlayerConnect, this)
        EventsQueue.instance.registerListener(EventType.ExpressionButton, this)
    }

    override fun onEventReceive(event: Event) {
        when(event) {
            is DropItemEvent -> handleDropItem(event)
            is PressButtonEvent -> handlePressButtonEvent(event)
            is ExpressionButtonEvent -> handleExpressionButtonEvent(event)
            is PlayerConnectEvent -> handlePlayerConnectEvent(event)
            else -> return
        }
    }


    private fun handlePlayerConnectEvent(event: PlayerConnectEvent) {
        if(event.charid == 0) {
            EventsQueue.instance.enqueue(
                    PlayerConnectedEvent(event.charid,
                            30065,
                            0,
                            20002)
            )
        } else {
            EventsQueue.instance.enqueue(
                    OtherPlayerConnectedEvent(event.charid,
                            30064,
                            0,
                            20001,
                            Char.State.STAND,
                            Point(5094f, 117f))
            )

        }
    }

    private fun handlePressButtonEvent(event: PressButtonEvent) {
        if(event.charid == 0){
            EventsQueue.instance.enqueue(
                    PressButtonEvent(1,
                            event.buttonPressed,
                            event.pressed)
            )
        }
    }

    private fun handleExpressionButtonEvent(event: ExpressionButtonEvent) {
        if(event.charid == 0){
            EventsQueue.instance.enqueue(
                    ExpressionButtonEvent(1, event.expression)
            )
        }
    }

    private fun handleDropItem(event: DropItemEvent) {
            EventsQueue.instance.enqueue(
                    ItemDroppedEvent(Randomizer.nextInt(1000),
                            event.itemid,
                            Point(event.startDropPos),
                            event.owner,
                            event.mapId)
            )

    }


}
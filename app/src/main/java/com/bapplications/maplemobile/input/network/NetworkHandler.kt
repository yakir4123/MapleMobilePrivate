package com.bapplications.maplemobile.input.network

import android.util.Log

import com.bapplications.maplemobile.utils.Point
import com.bapplications.maplemobile.input.events.*
import com.bapplications.maplemobile.input.EventsQueue
import com.bapplications.maplemobile.input.InputAction
import com.bapplications.maplemobile.gameplay.player.look.Char
import com.bapplications.maplemobile.gameplay.player.look.Expression

import io.grpc.ManagedChannel
import io.grpc.stub.StreamObserver
import io.grpc.ManagedChannelBuilder

import messaging.Service
import messaging.MapleServiceGrpc
import messaging.Service.RequestEvent
import messaging.Service.ResponseEvent

class NetworkHandler(host: String, port: Int) : EventListener {

    private val mChannel: ManagedChannel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build()
    private val asyncStub: MapleServiceGrpc.MapleServiceStub
    private var requestStream: StreamObserver<RequestEvent>? = null

    init {
        asyncStub = MapleServiceGrpc.newStub(mChannel)
        EventsQueue.instance.registerListener(EventType.DropItem, this)
        EventsQueue.instance.registerListener(EventType.PickupItem, this)

        EventsQueue.instance.registerListener(EventType.EquipItem, this)
        EventsQueue.instance.registerListener(EventType.UnequipItem, this)

        EventsQueue.instance.registerListener(EventType.PressButton, this)
        EventsQueue.instance.registerListener(EventType.ExpressionButton, this)

        EventsQueue.instance.registerListener(EventType.PlayerConnect, this)
        EventsQueue.instance.registerListener(EventType.PlayerStateUpdate, this)
        startStreaming()
    }

    override fun onEventReceive(event: Event) {
        when(event) {
            is DropItemEvent -> handleDropItem(event)
            is PressButtonEvent -> handlePressButtonEvent(event)
            is ExpressionButtonEvent -> handleExpressionButtonEvent(event)
            is PlayerConnectEvent -> handlePlayerConnectEvent(event)
            is PlayerStateUpdateEvent -> handlePlayerStateUpdateEvent(event)
            is EquipItemEvent -> handleEquipItem(event)
            is UnequipItemEvent -> handleUnequipItem(event)
            is PickupItemEvent -> handlePickupItem(event)
            else -> return
        }
    }

    private fun startStreaming() {
        requestStream = asyncStub.eventsStream(object : StreamObserver<ResponseEvent> {
            override fun onNext(value: ResponseEvent) {
                when (value.eventCase) {
                    ResponseEvent.EventCase.DROPITEM ->
                        EventsQueue.instance.enqueue(
                                ItemDroppedEvent(value.dropItem.oid,
                                        value.dropItem.id,
                                        Point(value.dropItem.start),
                                        value.dropItem.owner,
                                        value.dropItem.mapid)
                        )
                    ResponseEvent.EventCase.PRESSBUTTON ->
                        EventsQueue.instance.enqueue(
                                PressButtonEvent(value.pressButton.charid,
                                        InputAction.Key.values()[value.pressButton.button],
                                        value.pressButton.pressed)
                        )
                    ResponseEvent.EventCase.EXPRESSIONBUTTON ->
                        EventsQueue.instance.enqueue(
                                ExpressionButtonEvent(value.expressionButton.charid,
                                        Expression.values()[value.expressionButton.expression]
                                )
                        )
                    ResponseEvent.EventCase.PLAYERCONNECTED ->
                        EventsQueue.instance.enqueue(
                                PlayerConnectedEvent(value.playerConnected.charid,
                                        value.playerConnected.hair,
                                        value.playerConnected.skin,
                                        value.playerConnected.face)
                        )
                    ResponseEvent.EventCase.OTHERPLAYERCONNECTED ->
                        EventsQueue.instance.enqueue(
                                OtherPlayerConnectedEvent(value.otherPlayerConnected.charid,
                                        value.otherPlayerConnected.hair,
                                        value.otherPlayerConnected.skin,
                                        value.otherPlayerConnected.face,
                                        Char.State.values()[value.otherPlayerConnected.state],
                                        Point(value.otherPlayerConnected.pos)
                                )
                        )
                    ResponseEvent.EventCase.OTHERPLAYERSTATEUPDATED ->
                        EventsQueue.instance.enqueue(
                                PlayerStateUpdateEvent(value.otherPlayerStateUpdated.charid,
                                        Char.State.values()[value.otherPlayerStateUpdated.state],
                                        Point(value.otherPlayerStateUpdated.pos))
                        )
                    ResponseEvent.EventCase.EQUIPITEM ->
                        EventsQueue.instance.enqueue(
                                EquipItemEvent(value.equipItem.cid,
                                        value.equipItem.slotid,
                                        value.equipItem.itemid)
                        )
                    ResponseEvent.EventCase.UNEQUIPITEM ->
                        EventsQueue.instance.enqueue(
                                UnequipItemEvent(value.unequipItem.cid,
                                        value.unequipItem.slotid,
                                        value.unequipItem.itemid)
                        )
                    ResponseEvent.EventCase.PICKUPITEM ->
                        EventsQueue.instance.enqueue(
                                PickupItemEvent(value.pickupItem.charid,
                                        value.pickupItem.oid,
                                        value.pickupItem.mapid)
                        )
                    else -> return
                }
            }

            override fun onError(t: Throwable) {
            }

            override fun onCompleted() {
            }
        })
    }

    private fun handlePlayerConnectEvent(event: PlayerConnectEvent) {
        Thread {
            requestStream?.onNext(
                    RequestEvent.newBuilder().setPlayerConnect(Service.RequestPlayerConnect
                            .newBuilder()
                            .setCharid(event.charid)
                    ).build()
            )
        }.start()
    }

    private fun handlePlayerStateUpdateEvent(event: PlayerStateUpdateEvent) {
        if(event.charid != 0)
            return
        Thread {
            requestStream?.onNext(
                    RequestEvent.newBuilder().setPlayerStateUpdated(Service.UpdatePlayerState
                            .newBuilder()
                            .setState(event.state.ordinal)
                            .setPos(Service.Point.newBuilder()
                                    .setX(event.pos.x)
                                    .setY(event.pos.y))
                    ).build()
            )
        }.start()
    }

    private fun handlePressButtonEvent(event: PressButtonEvent) {
        if(event.charid != 0){
            return
        }
        Thread {
            requestStream?.onNext(
                    try {
                        RequestEvent.newBuilder().setPressButton(Service.PressButton
                                .newBuilder()
                                .setButton(event.buttonPressed.ordinal)
                                .setPressed(event.pressed)
                        ).build()
                    } catch (e: KotlinNullPointerException) {
                        // to do:: check this out why it happend
                        Log.e("error", "nullPointer")
                        RequestEvent.newBuilder().setPressButton(Service.PressButton
                                .newBuilder()
                                .setButton(event.buttonPressed.ordinal)
                                .setPressed(event.pressed)
                        ).build()
                    }
            )
        }.start()
    }

    private fun handleExpressionButtonEvent(event: ExpressionButtonEvent) {
        if(event.charid != 0) {
            return
        }
        Thread {
            requestStream?.onNext(
                    RequestEvent.newBuilder().setExpressionButton(Service.ExpressionButton
                            .newBuilder()
                            .setExpression(event.expression.ordinal)
                    ).build()
            )
        }.start()
    }

    private fun handleDropItem(event: DropItemEvent) {
        Thread {
            requestStream?.onNext(
                    RequestEvent.newBuilder().setDropItem(Service.RequestDropItem
                            .newBuilder()
                            .setId(event.itemid)
                            .setStart(Service.Point.newBuilder()
                                    .setX(event.startDropPos.x)
                                    .setY(event.startDropPos.y))
                            .setOwner(event.owner)
                            .setSlotid(event.slotId)
                            .setMapid(event.mapId)
                    ).build()
            )
        }.start()
    }

    fun handleEquipItem(event: EquipItemEvent) {
        if(event.charid != 0)
            return
        Thread {
            requestStream?.onNext(
                    RequestEvent.newBuilder().setEquipItem(Service.EquipItem
                            .newBuilder()
                            .setCid(event.charid)
                            .setSlotid(event.slotId)
                            .setItemid(event.itemId)
                    ).build()
            )
        }.start()
    }

    private fun handleUnequipItem(event: UnequipItemEvent) {
        if(event.charid != 0)
            return
        Thread {
            requestStream?.onNext(
                    RequestEvent.newBuilder().setUnequipItem(Service.UnequipItem
                            .newBuilder()
                            .setCid(event.charid)
                            .setSlotid(event.slotId)
                            .setItemid(event.itemId)
                    ).build()
            )
        }.start()
    }

    private fun handlePickupItem(event: PickupItemEvent) {
        if(event.charid != 0)
            return
        Thread {
            requestStream?.onNext(
                    RequestEvent.newBuilder().setPickupItem(Service.PickupItem
                            .newBuilder()
                            .setCharid(event.charid)
                            .setOid(event.oid)
                            .setMapid(event.mapId)
                    ).build()
            )
        }.start()
    }
}
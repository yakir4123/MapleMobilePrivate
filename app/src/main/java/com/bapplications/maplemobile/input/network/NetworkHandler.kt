package com.bapplications.maplemobile.input.network

import com.bapplications.maplemobile.utils.Point
import com.bapplications.maplemobile.constatns.Configuration
import com.bapplications.maplemobile.input.EventsQueue
import com.bapplications.maplemobile.input.events.*
import com.bapplications.maplemobile.ui.GameActivity

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
        startStreaming()
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
                    else -> return
                }
            }

            override fun onError(t: Throwable) {
            }

            override fun onCompleted() {
            }
        })
    }

    override fun onEventReceive(event: Event) {
        when(event.type) {
            EventType.DropItem -> {
                val _event: DropItemEvent = event as DropItemEvent
                Thread {
                    requestStream?.onNext(
                            RequestEvent.newBuilder().setDropItem(Service.RequestDropItem
                                    .newBuilder()
                                    .setId(_event.itemid)
                                    .setStart(Service.Point.newBuilder()
                                            .setX(_event.startDropPos.x)
                                            .setY(_event.startDropPos.y))
                                    .setOwner(_event.owner)
                                    .setInvtype(_event.invType)
                                    .setSlotid(_event.slotId)
                                    .setMapid(_event.mapId)
                            ).build()
                    )
                }.start()
            }
            else -> return
        }
    }
}
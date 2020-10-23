package com.bapplications.maplemobile

import com.bapplications.maplemobile.constatns.Configuration
import com.bapplications.maplemobile.gameplay.GameMap
import com.bapplications.maplemobile.utils.Point
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.grpc.stub.StreamObserver
import messaging.MapleServiceGrpc
import messaging.Service
import messaging.Service.RequestEvent
import messaging.Service.ResponseEvent

class NetworkHandlerPOC(host: String, port: Int) {

    private object HOLDER {
        val INSTANCE = NetworkHandlerPOC(Configuration.HOST, Configuration.PORT)
    }

    companion object {
        val instance: NetworkHandlerPOC by lazy { HOLDER.INSTANCE }
    }

    lateinit var gameMap: GameMap
    private val mChannel: ManagedChannel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build()
    private val asyncStub: MapleServiceGrpc.MapleServiceStub
    private var requestStream: StreamObserver<RequestEvent>? = null

    init {
        asyncStub = MapleServiceGrpc.newStub(mChannel)
        startStreaming()
    }


    private fun startStreaming() {
        requestStream = asyncStub.eventsStream(object : StreamObserver<ResponseEvent> {
            override fun onNext(value: ResponseEvent) {
                gameMap.spawnItemDrop(value.dropItem.oid, value.dropItem.id, Point(value.dropItem.start), value.dropItem.owner)
            }

            override fun onError(t: Throwable) {
            }

            override fun onCompleted() {
            }
        })
    }

    fun dropItem(itemid: Int, startDropPos: Point, owner: Int, invType: Int, slotId: Int) {
        Thread {
            requestStream?.onNext(
                    RequestEvent.newBuilder().setDropItem(Service.RequestDropItem
                            .newBuilder()
                            .setId(itemid)
                            .setStart(Service.Point.newBuilder().setX(startDropPos.x)
                                    .setY(startDropPos.y))
                            .setOwner(owner)
                            .setInvtype(invType)
                            .setSlotid(slotId)
                    ).build()
            )
        }.start()

    }
}
package com.bapplications.maplemobile.gameplay

import com.bapplications.maplemobile.constatns.Configuration
import com.bapplications.maplemobile.gameplay.player.CharEntry
import com.bapplications.maplemobile.gameplay.player.Player
import com.bapplications.maplemobile.input.EventsQueue
import com.bapplications.maplemobile.input.events.*
import com.bapplications.maplemobile.input.events.EventListener
import com.bapplications.maplemobile.input.network.NetworkHandler
import com.bapplications.maplemobile.ui.GameActivityUIManager
import com.bapplications.maplemobile.ui.interfaces.GameEngineListener
import java.util.*
import java.util.function.Consumer

class GameEngine private constructor() : EventListener {
    val camera: Camera = Camera()
    var player: Player? = null
        private set
    private var currMap: GameMap
    private val networkHandler: NetworkHandler
    
    fun startGame() {
        currMap.init(Configuration.START_MAP)
        listeners.forEach(Consumer { listener: GameEngineListener -> listener.onGameStarted() })
    }

    fun update(deltatime: Int) {
        EventsQueue.instance.dequeueAll()
        currMap.update(deltatime)
    }

    fun drawFrame() {
        currMap.draw(1f)
    }

    fun destroy() {}
    @JvmOverloads
    fun initMap(mapId: Int = currMap.mapId) {
        if (mapId != currMap.mapId) {
            listeners.forEach(Consumer { listener: GameEngineListener -> listener.onChangedMap(mapId) })
        }
        currMap = GameMap(camera)
        currMap.init(mapId)
        listeners.forEach(Consumer { listener: GameEngineListener  -> listener.onMapLoaded(currMap) })
    }

    fun changeMap(mapId: Int, portalName: String) {
        initMap(mapId)
        currMap.enterMap(player!!, currMap.getPortalByName(portalName))
    }

    fun loadPlayer() {
        EventsQueue.instance.enqueue(PlayerConnectEvent(0))
        if (false) {
            // for development this will be a new char instead from reading from a db
            val ce = CharEntry(0)
            ce.look.faceid = 20000
            ce.look.hairid = 30020
            loadPlayer(ce)
        }
    }

    fun loadPlayer(entry: CharEntry) {
        player = Player(entry)
    }

    override fun onEventReceive(event: Event) {
        when (event.type) {
            EventType.PlayerConnected -> {
                val (charid, hair, skin, face) = event as PlayerConnectedEvent
                val ce = CharEntry(charid)
                ce.look.hairid = hair
                ce.look.faceid = face
                ce.look.skin = skin.toByte()
                loadPlayer(ce)
                listeners.forEach(Consumer { listener: GameEngineListener -> listener.onPlayerLoaded(player!!) })
                currMap.enterMap(player!!, currMap.getPortalByName("sp"))
            }
        }
    }

    var listeners: MutableList<GameEngineListener> = ArrayList()
    fun registerListener(listener: GameEngineListener) {
        listeners.add(listener)
    }

    fun removeListener(listener: GameEngineListener) {
        listeners.remove(listener)
    }

    companion object {
        @JvmStatic
        var instance: GameEngine? = null
            get() {
                if (field == null) field = GameEngine()
                return field
            }
            private set
    }

    init {
        currMap = GameMap(camera)
        networkHandler = NetworkHandler(Configuration.HOST, Configuration.PORT)
        //        new NetworkHandlerDemo();
        EventsQueue.instance.registerListener(EventType.PlayerConnected, this)
    }
}
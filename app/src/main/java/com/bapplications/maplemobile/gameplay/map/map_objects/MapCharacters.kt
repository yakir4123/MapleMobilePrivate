package com.bapplications.maplemobile.gameplay.map.map_objects

import com.bapplications.maplemobile.gameplay.map.MapObject
import com.bapplications.maplemobile.gameplay.physics.Physics
import com.bapplications.maplemobile.gameplay.player.CharEntry
import com.bapplications.maplemobile.input.EventsQueue
import com.bapplications.maplemobile.input.events.Event
import com.bapplications.maplemobile.input.events.EventListener
import com.bapplications.maplemobile.input.events.EventType
import com.bapplications.maplemobile.input.events.OtherPlayerConnectedEvent
import com.bapplications.maplemobile.utils.Point
import java.util.*

class MapCharacters : EventListener {

    var chars = MapObjects<OtherChar>()
    var spawns: Queue<CharSpawn> = LinkedList()

    init {
        EventsQueue.instance.registerListener(EventType.OtherPlayerConnected, this)
    }

    fun update(physics: Physics, deltatime: Int) {
        while (!spawns.isEmpty()) {
            val spawn = spawns.poll()
            val cid = spawn.cid
            val ochar: OtherChar? = getChar(cid) as OtherChar?

            if (ochar == null) {
                chars.add(spawn.instantiate())
            }
        }
        chars.update(physics, deltatime)
    }

    private fun getChar(cid: Int): MapObject? {
        return chars.get(cid);
    }

    override fun onEventReceive(event: Event) {
        when(event) {
            is OtherPlayerConnectedEvent -> {
                val look = CharEntry.LookEntry()
                look.hairid = event.hair
                look.faceid = event.face
                look.skin = event.skin.toByte()
                spawns.add(CharSpawn(event.charid, look, 1, 1, "", 1, Point()))
            }
        }
    }
}
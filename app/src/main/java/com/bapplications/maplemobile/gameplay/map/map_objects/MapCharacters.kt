package com.bapplications.maplemobile.gameplay.map.map_objects

import com.bapplications.maplemobile.constatns.Configuration
import java.util.*

import com.bapplications.maplemobile.utils.Point
import com.bapplications.maplemobile.input.events.*
import com.bapplications.maplemobile.input.EventsQueue
import com.bapplications.maplemobile.input.InputAction
import com.bapplications.maplemobile.gameplay.map.Layer
import com.bapplications.maplemobile.gameplay.map.MapObject
import com.bapplications.maplemobile.gameplay.map.map_objects.spawns.CharSpawn
import com.bapplications.maplemobile.gameplay.physics.Physics
import com.bapplications.maplemobile.gameplay.player.CharEntry
import com.bapplications.maplemobile.input.events.EventListener

class MapCharacters : EventListener {

    var chars = MapObjects<OtherChar>()
    var spawns: Queue<CharSpawn> = LinkedList()

    init {
        EventsQueue.instance.registerListener(EventType.PressButton, this)
        EventsQueue.instance.registerListener(EventType.ExpressionButton, this)
        EventsQueue.instance.registerListener(EventType.OtherPlayerConnected, this)
        EventsQueue.instance.registerListener(EventType.PlayerStateUpdate, this)
        EventsQueue.instance.registerListener(EventType.EquipItem, this)
        EventsQueue.instance.registerListener(EventType.UnequipItem, this)
    }

    fun update(physics: Physics, deltatime: Int) {
        while (!spawns.isEmpty()) {
            val spawn = spawns.poll()
            val cid = spawn.cid
            var ochar: OtherChar? = getChar(cid) as OtherChar?

            if (ochar == null) {
                ochar = spawn.instantiate()
                chars.add(ochar)
            }
        }
        chars.update(physics, deltatime)
    }

    fun draw(layer: Layer, viewpos: Point, alpha: Float) {
        for (ochar in chars.objects.values) {
            ochar.draw(layer, viewpos, alpha)
        }
    }


    fun getChar(cid: Int): MapObject? {
        return chars[cid]
    }

    override fun onEventReceive(event: Event) {
        when(event) {
            is OtherPlayerConnectedEvent -> {
                val look = CharEntry.LookEntry()
                look.hairid = event.hair
                look.faceid = event.face
                look.skin = event.skin.toByte()
                spawns.add(CharSpawn(event.charid, look, 1, 1, "", event.state, event.pos))
            }
            is PressButtonEvent -> {
                if (event.charid != 0) {
                    if (event.pressed) {
                        (chars[event.charid] as OtherChar?)?.clickedButton(InputAction.byKey(event.buttonPressed)!!)
                    } else {
                        (chars[event.charid] as OtherChar?)?.releasedButtons(InputAction.byKey(event.buttonPressed)!!)
                    }
                }
            }
            is PlayerStateUpdateEvent -> {
                if (event.charid != 0) {
                    val ochar : OtherChar? = chars.get(event.charid) as OtherChar?
                        if(ochar?.let{ event.pos.dist(it.position) > Configuration.MIN_DIST_UPDATE} == true) {
                            ochar.position = event.pos
                        }
                }
            }
            is EquipItemEvent -> {
                if (event.charid != 0) {
                    val ochar : OtherChar? = chars[event.charid] as OtherChar?
                    ochar?.look?.addEquip(event.itemId)
                }
            }
            is UnequipItemEvent -> {
                if (event.charid != 0) {
                    val ochar : OtherChar? = chars[event.charid] as OtherChar?
                    ochar?.look?.removeEquip(event.itemId)
                }
            }
        }
    }
}
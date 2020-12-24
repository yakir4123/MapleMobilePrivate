package com.bapplications.maplemobile.gameplay.map.map_objects

import com.bapplications.maplemobile.constatns.Loaded
import com.bapplications.maplemobile.gameplay.map.Portal
import com.bapplications.maplemobile.gameplay.map.Portal.WarpInfo
import com.bapplications.maplemobile.gameplay.player.Player
import com.bapplications.maplemobile.gameplay.textures.Animation
import com.bapplications.maplemobile.pkgnx.NXNode
import com.bapplications.maplemobile.utils.Point
import java.util.*

class MapPortals(src: NXNode, mapid: Int) {
    private var cooldown: Short
    private val portalsById = HashMap<Byte, Portal>()
    private val portalIdsByName = HashMap<String, Byte>()
    fun update(playerpos: Point?, deltatime: Int) {
        animations[Portal.Type.REGULAR]!!.update(deltatime)
        animations[Portal.Type.HIDDEN]!!.update(deltatime)
        for (portal in portalsById.values) {
            when (portal.type) {
                Portal.Type.HIDDEN, Portal.Type.TOUCH -> portal.update(playerpos)
            }
        }
        if (cooldown > 0) cooldown--
    }

    fun draw(viewpos: Point?, inter: Float) {
        for (portal in portalsById.values) portal.draw(viewpos, inter)
    }


    fun collidePortal(player: Player) : Boolean {
        if(cooldown.toInt() != 0) return false
        for (portal in portalsById.values) {
            if (portal.collider.overlaps(player.collider) &&
                    (portal.warpInfo.intramap || portal.warpInfo.valid))
                return true
        }
        return false
    }

    fun findWarpAt(player: Player): WarpInfo {
        if (cooldown.toInt() == 0) {
            cooldown = WARPCD
            for (portal in portalsById.values) {
                if (portal.collider.overlaps(player.collider)) return portal.warpInfo
            }
        }
        return WarpInfo()
    }

    fun getPortalByName(toname: String): Portal {
        val pid = portalIdsByName[toname]!!
        return getPortalById(pid)
    }

    private fun getPortalById(pid: Byte): Portal {
        return portalsById[pid]!!
    }

    fun getPortalIdByName(toname: String): Byte {
        return portalIdsByName[toname]!!
    }

    val nextMaps: Set<Int>
        get() {
            val nextMaps: MutableSet<Int> = HashSet()
            for (portal in portalsById.values) {
                nextMaps.add(portal.warpInfo.mapid)
            }
            return nextMaps
        }

    companion object {
        private const val WARPCD: Short = 48
        private val animations = HashMap<Portal.Type, Animation>()
        fun init() {
            val src = Loaded.getFile(Loaded.WzFileName.MAP).root.getChild<NXNode>("MapHelper.img").getChild<NXNode>("portal").getChild<NXNode>("game")
            animations[Portal.Type.HIDDEN] = Animation(src.getChild<NXNode>("ph").getChild<NXNode>("default").getChild("portalContinue"), "0")
            animations[Portal.Type.REGULAR] = Animation(src.getChild("pv"), "0")
        }
    }

    init {
        for (sub in src) {
            val portal_id = sub.name.toInt().toByte()
            if (portal_id < 0) continue
            val type = Portal.typeById(sub.getChild<NXNode>("pt").get(0L).toInt())
            val name = sub.getChild<NXNode>("pn").get("")
            val target_name = sub.getChild<NXNode>("tn").get("")
            val targeMapid = sub.getChild<NXNode>("tm").get(999999999L).toInt()
            val position = Point(sub)
            position.y *= -1
            val animation = animations[type]
            val intramap = targeMapid == mapid
            portalsById[portal_id] = Portal(animation, type, name, intramap, position, targeMapid, target_name)
            portalIdsByName[name] = portal_id
        }
        cooldown = WARPCD
    }
}
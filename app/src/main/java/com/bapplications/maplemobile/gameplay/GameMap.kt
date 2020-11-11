package com.bapplications.maplemobile.gameplay

import android.util.Log
import com.bapplications.maplemobile.constatns.Loaded
import com.bapplications.maplemobile.gameplay.audio.Music
import com.bapplications.maplemobile.gameplay.audio.Sound
import com.bapplications.maplemobile.gameplay.map.Layer
import com.bapplications.maplemobile.gameplay.map.MapInfo
import com.bapplications.maplemobile.gameplay.map.MapObject
import com.bapplications.maplemobile.gameplay.map.Portal
import com.bapplications.maplemobile.gameplay.map.map_objects.*
import com.bapplications.maplemobile.gameplay.map.map_objects.mobs.MobSpawn
import com.bapplications.maplemobile.gameplay.physics.Physics
import com.bapplications.maplemobile.gameplay.player.Player
import com.bapplications.maplemobile.input.EventsQueue.Companion.instance
import com.bapplications.maplemobile.input.InputAction
import com.bapplications.maplemobile.input.events.*
import com.bapplications.maplemobile.pkgnx.NXNode
import com.bapplications.maplemobile.utils.Point
import com.bapplications.maplemobile.utils.Rectangle
import com.bapplications.maplemobile.utils.StaticUtils
import kotlinx.coroutines.*

class GameMap(camera: Camera) : EventListener {
    var mapId = 0
        private set
    var state: State
        private set
    private var mobs: MapMobs? = null
    lateinit var player: Player
        private set
    val camera: Camera
    private var drops: MapDrops? = null
    private var mapInfo: MapInfo? = null
    private var physics: Physics? = null
    private var portals: MapPortals? = null
    private var tilesobjs: MapTilesObjs? = null
    private var characters: MapCharacters? = null
    private var backgrounds: MapBackgrounds? = null

    enum class State {
        INACTIVE, TRANSITION, ACTIVE
    }

    fun init(mapid: Int) {
        // drops.init();
        loadMap(mapid)
    }

    override fun onEventReceive(event: Event) {
        when (event.type) {
            EventType.ItemDropped -> {
                val (oid, id, start, owner, _mapId) = event as ItemDroppedEvent
                if (mapId == _mapId) {
                    spawnItemDrop(oid, id, start, owner)
                }
            }
            EventType.PickupItem -> {
                val (cid, oid, _mapId) = event as PickupItemEvent
                Log.e("GameMap", "Pick up item: $cid, $oid, $_mapId")
                val drop = drops!![oid] as Drop?
                drop?.let {
                    val char: MapObject? = if (cid == player.oid) {
                        player.pickupDrop(it)
                        player
                    } else {
                        characters?.getChar(cid)
                    }
                    if (mapId == _mapId) {
                        // pickup animation
                        char?.let { drops?.remove(oid, Drop.State.PICKEDUP, it) }
                    }
                }
            }
        }
    }

    private fun spawnMobs(src: NXNode) {
        var oid = 100 // todo: needs a way to calculate that
        for (spawnNode in src) {
            if (spawnNode.getChild<NXNode>("type").get("") != "m") {
                continue
            }
            val id = spawnNode.getChild<NXNode>("id").get("")
            val flip = spawnNode.getChild<NXNode>("f").get(0L).toShort()
            val p = Point(spawnNode)
            val spawn = MobSpawn(oid++, id.toInt(), 0.toByte(), 0.toByte(), flip, true, 0.toByte(), p.flipY())
            mobs!!.spawn(spawn)
        }
    }

    fun spawnItemDrop(oid: Int, id: Int, start: Point?, owner: Int) {
        val spawn = DropSpawn(oid, id, id == 0, owner, start!!, Drop.State.DROPPED, true)
        drops!!.spawn(spawn)
    }

    fun loadMap(mapid: Int) {
        mapId = mapid
        val strid = StaticUtils.extendId(mapid, 9)
        val prefix = strid[0]
        val src = if (mapid == -1) null else Loaded.getFile(Loaded.WzFileName.MAP).root.getChild<NXNode>("Map").getChild<NXNode>("Map$prefix").getChild<NXNode>("$strid.img")

        // in case of no map exist with this mapid
        if (src != null && !src.isNotExist) {
            try {
                mobs = MapMobs()
                drops = MapDrops()
                characters = MapCharacters()
                physics = Physics(src.getChild("foothold"))
                backgrounds = MapBackgrounds(src.getChild("back"))
                portals = MapPortals(src.getChild("portal"), mapid)
                mapInfo = MapInfo(src, physics!!.fht.walls, physics!!.fht.borders)
                tilesobjs = MapTilesObjs(src, Rectangle(mapInfo!!.walls, mapInfo!!.borders))
                spawnMobs(src.getChild("life"))
            } catch (e: Exception) {
                Log.e("GameMap", "Error loading map $mapid")
                throw e
            }
        }
    }

    fun respawn(position: Point?) {
        Music.play(mapInfo!!.bgm)

        val startpos = physics!!.getYBelow(position)
        player.respawn(startpos, mapInfo!!.isUnderwater)
        camera.setView(mapInfo!!.walls, mapInfo!!.borders)
        camera.update(player.position.negateSign())
    }

    fun update(deltatime: Int) {
        if (state != State.ACTIVE) return

        runBlocking {
////        combat.update();
            async {
                backgrounds!!.update(deltatime)
            }
            ////        effect.update();

            async {
                tilesobjs!!.update(deltatime)
            }
////        reactors.update(physics);
////        npcs.update(physics);
            async {
                characters!!.update(physics!!, deltatime)
            }
            async {
                drops!!.update(physics!!, deltatime)
            }
            async {
                mobs!!.update(physics, deltatime)
                player.update(physics!!, deltatime)
            }
            async {
                portals!!.update(player.position, deltatime)
            }
            async {
                camera.update(player.position)
            }
        }
        player?.stats?.canUseUpArrow?.postValue(portals?.collidePortal(player)!!
                || player.isClimbing || mapInfo!!.findLadder(player.position, true) != null)

        checkDrops()

        if (!player.isClimbing /* && !player.is_sitting()*/ && !player.isAttacking) {
            if (player.isPressed(InputAction.DOWN_ARROW_KEY)) checkLadders(false)
            else if (player.isPressed(InputAction.UP_ARROW_KEY)) checkLadders(true)
            if (player.isPressed(InputAction.UP_ARROW_KEY)) enterPortal()


//            if (player.isPressed(InputAction.SIT))
//            check_seats();

//            if (player.isPressed(InputAction.ATTACK))
//            combat.use_move(0);
        }
        if (player.isInvincible()) return
        val oid = mobs!!.findColliding(player)
        if (oid != 0) {
            val attack = mobs!!.createAttack(oid)
            if (attack.isValid) {
                val result = player.damage(attack)
            }
        }
    }

    private fun checkDrops() {
        val drop = drops!!.inRange(player)

        // button is set to visible but he didnt click yet
        if((drop != null || player.isPressed(InputAction.LOOT_KEY))
                != player.stats.canLoot.value) {
            player.stats.canLoot.postValue(drop != null)
        }
        // pressed pick up button now try to pick it
        if(drop != null && !drop.onPickProcess && !drop.isPicked() && player.isPressed(InputAction.LOOT_KEY)) {
            player.tryPickupDrop(drop)
        }
    }

    fun getPortalByName(portalName: String): Portal {
        return portals!!.getPortalByName(portalName)
    }

    private fun enterPortal() {
        if (player.isAttacking) return
        val warpinfo = portals!!.findWarpAt(player)
        if (warpinfo.intramap) {
            val spawnpoint = portals!!.getPortalByName(warpinfo.toname)
            val startpos = physics!!.getYBelow(spawnpoint.spawnPosition)
            player.respawn(startpos, mapInfo!!.isUnderwater)
        } else if (warpinfo.valid) {
            Sound(Sound.Name.PORTAL).play()
            GameEngine.instance?.changeMap(warpinfo.mapid, warpinfo.toname)
        }
    }

    fun draw(alpha: Float) {
        if (state != State.ACTIVE) return
        val viewpos = camera.position(alpha)
        backgrounds!!.drawBackgrounds(viewpos, alpha)
        for (id in Layer.values()) {
            tilesobjs!!.draw(id, camera.getPositionRect(), viewpos, alpha)
            physics!!.draw(viewpos)
//            reactors.draw(id, viewx, viewy, alpha);
//            npcs.draw(id, viewx, viewy, alpha);
            mobs!!.draw(id, viewpos, alpha)
            characters!!.draw(id, viewpos, alpha)
            player.draw(id, viewpos, alpha)
            drops!!.draw(id, viewpos, alpha)
        }
        //
//        combat.draw(viewx, viewy, alpha);
        portals!!.draw(viewpos, alpha)
        backgrounds!!.drawForegrounds(viewpos, alpha)
        //        effect.draw();

        // for debugging purposes
//        camera.draw();
    }

    fun clear() {
        state = State.INACTIVE
        instance.unregisterListener(EventType.ItemDropped, this)
        instance.unregisterListener(EventType.PickupItem, this)

        //        Texture.clear();
//        chars.clear();
//        npcs.clear();
//        mobs.clear();
//        drops.clear();
//        reactors.clear();
    }

    fun checkLadders(up: Boolean) {
        if (player.isClimbing || player.isAttacking) return
        player.ladder = mapInfo!!.findLadder(player.position, up)
    }

    fun enterMap(player: Player, portal: Portal) {
        this.player = player
        player.map = this
        state = State.ACTIVE
        respawn(portal.spawnPosition)
    }

    init {
        state = State.INACTIVE
        this.camera = camera
        instance.registerListener(EventType.ItemDropped, this)
        instance.registerListener(EventType.PickupItem, this)
    }
}
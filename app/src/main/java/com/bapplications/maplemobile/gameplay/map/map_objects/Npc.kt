package com.bapplications.maplemobile.gameplay.map.map_objects

import android.graphics.Bitmap
import com.bapplications.maplemobile.constants.Configuration
import com.bapplications.maplemobile.constants.Loaded
import com.bapplications.maplemobile.gameplay.components.ColliderComponent
import com.bapplications.maplemobile.gameplay.map.MapObject
import com.bapplications.maplemobile.gameplay.physics.Physics
import com.bapplications.maplemobile.gameplay.textures.Animation
import com.bapplications.maplemobile.pkgnx.NXNode
import com.bapplications.maplemobile.utils.*

class Npc (val npcid: Int, oid: Int, var flip: Boolean, val fh: Short, val control: Boolean, position: Point)
    : MapObject(oid) {

    val icon: Bitmap
    private val animations = mutableMapOf<String, Animation>()
    private val states = mutableListOf<String>()
    private val speaks = mutableMapOf<String, MutableList<String>>()

    private val info: NXNode
    private val hidename: Boolean
    private val scripted: Boolean
    private val collider: Rectangle
    val name: String
    val func: String
    var stance: String? = null
        set(value) {
            if( field == value) {
                return
            }
            field = value
            animations[field]?.reset()
        }

    init {
        val strid = StaticUtils.extendId(npcid, 7) + ".img"

        var src = Loaded.getFile(Loaded.WzFileName.NPC).root.getChild<NXNode>(strid)
        val strsrc = Loaded.getFile(Loaded.WzFileName.STRING).root.getChild<NXNode>("Npc.img")
                .getChild<NXNode>(npcid)

        var link = src.getChild<NXNode>("info").getChild<NXNode>("link").get("")

        if (link.isNotEmpty())
        {
            link += ".img"
            src = Loaded.getFile(Loaded.WzFileName.NPC).root.getChild(link)
        }

        info = src.getChild("info")

        hidename = info.getChild<NXNode>("hideName").bool
        scripted = info.getChild<NXNode>("script").childCount > 0
                || info.getChild<NXNode>("shop").bool

        for (npcnode in src)
        {
            val state = npcnode.name

            if (state != "info")
            {
                animations[state] = Animation(npcnode, 0, false)
                states.add(state)
            }

            for (speaknode in npcnode.getChild<NXNode>("speak")) {
                speaks[state]?.add(strsrc[speaknode.get("")])
            }
        }


        name = strsrc.getChild<NXNode>("name").get("")
        func = strsrc.getChild<NXNode>("func").get("")

        stance = "stand";

        icon = animations[stance]!!.frame.bmap
//        animations.forEach { (st, anim) ->
//            if (st != stance) {
//                anim.recycle()
//            } else {
//                anim.recycle(0)
//            }
//        }

        phobj.fhid = fh
        this.position = Point(position)

        collider = animations[stance]!!.bounds
        collider.shift(position)
    }

    override fun draw(view: Point, alpha: Float) {
        val absp = phobj.getAbsolute(view, alpha)
        val dargs = DrawArgument(absp, flip)
        animations[stance]?.draw(dargs, alpha)

        if (Configuration.SHOW_NPCS_RECT) {
            collider.draw(view)
            var origin = DrawableCircle.createCircle(position, Color.GREEN)
            origin.draw(dargs)
            origin = DrawableCircle.createCircle(absp, Color.RED)
            origin.draw(dargs)
        }
    }

    override fun update(physics: Physics, deltaTime: Int): Byte
    {
        if (!isActive)
            return phobj.fhlayer;

        physics.moveObject(phobj);

        if (animations.containsKey(stance))
        {
            val aniend = animations[stance]?.update(deltaTime) ?: false

            if (aniend && states.size > 0)
            {
                val nextStance = Randomizer.nextInt(states.size)
                val newStance = states[nextStance]
                stance = newStance
            }
        }

        return phobj.fhlayer;
    }

    override fun getCollider(): Rectangle {
        return collider
    }

    fun isInRange(pos: Point): Boolean {
        return if (!isActive) false else collider.contains(pos)
    }

    fun clear() {
        icon.recycle()
    }
}
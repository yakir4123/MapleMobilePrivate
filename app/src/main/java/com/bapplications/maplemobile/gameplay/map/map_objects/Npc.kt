package com.bapplications.maplemobile.gameplay.map.map_objects

import com.bapplications.maplemobile.constatns.Loaded
import com.bapplications.maplemobile.gameplay.map.MapObject
import com.bapplications.maplemobile.gameplay.physics.Physics
import com.bapplications.maplemobile.gameplay.textures.Animation
import com.bapplications.maplemobile.pkgnx.NXNode
import com.bapplications.maplemobile.pkgnx.nodes.NXLongNode
import com.bapplications.maplemobile.pkgnx.nodes.NXStringNode
import com.bapplications.maplemobile.utils.*

class Npc (val npcid: Int, oid: Int, var flip: Boolean, val fh: Short, val control: Boolean, position: Point)
    : MapObject(oid) {

    private val animations = mutableMapOf<String, Animation>()
    private val states = mutableListOf<String>()
    private val speaks = mutableMapOf<String, MutableList<String>>()

    private val info: NXNode
    private val hidename: Boolean
    private val scripted: Boolean

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

        if (link.length > 0)
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
                animations[state] = Animation(npcnode)
                states.add(state);
            }

            for (speaknode in npcnode.getChild<NXNode>("speak")) {
                speaks[state]?.add(strsrc[speaknode.get("")]);
            }
        }

        name = strsrc.getChild<NXNode>("name").get("")
        func = strsrc.getChild<NXNode>("func").get("")

        stance = "stand";

        phobj.fhid = fh
        this.position = Point(position);
    }

    override fun draw(view: Point, alpha: Float) {
        val absp = phobj.getAbsolute(view, alpha);
        animations[stance]?.draw(DrawArgument(absp, flip), alpha);
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
        TODO("Not yet implemented")
    }
}
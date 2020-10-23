package com.bapplications.maplemobile.gameplay.model_pools

import com.bapplications.maplemobile.utils.StaticUtils
import com.bapplications.maplemobile.constatns.Loaded
import com.bapplications.maplemobile.gameplay.audio.Sound
import com.bapplications.maplemobile.gameplay.map.map_objects.mobs.Mob
import com.bapplications.maplemobile.pkgnx.NXNode
import java.util.*

class MobModel(val id: Int){
    var animations = EnumMap<Mob.Stance, AnimationModel>(Mob.Stance::class.java)

    var name: String? = null
    var hitsound: Sound? = null
    var diesound: Sound? = null
    var level: Short = 0
    var speed = 0f
    var flyspeed = 0f
    var watk: Short = 0
    var matk: Short = 0
    var wdef: Short = 0
    var mdef: Short = 0
    var accuracy: Short = 0
    var avoid: Short = 0
    var knockback: Short = 0
    var undead = false
    var touchdamage = false
    var noflip = false
    var notattack = false
    var canmove = false
    var canjump = false
    var canfly = false

    init  {
        val strid = StaticUtils.extendId(id, 7)
        val src = Loaded.getFile(Loaded.WzFileName.MOB).root.getChild<NXNode>("$strid.img")
        val linkedNodes: NXNode

        val info = src.getChild<NXNode>("info")

        level = info.getChild<NXNode>("level").get(0L).toShort()
        watk = info.getChild<NXNode>("PADamage").get(0L).toShort()
        matk = info.getChild<NXNode>("MADamage").get(0L).toShort()
        wdef = info.getChild<NXNode>("PDDamage").get(0L).toShort()
        mdef = info.getChild<NXNode>("MDDamage").get(0L).toShort()
        accuracy = info.getChild<NXNode>("acc").get(0L).toShort()
        avoid = info.getChild<NXNode>("eva").get(0L).toShort()
        knockback = info.getChild<NXNode>("pushed").get(0L).toShort()
        speed = info.getChild<NXNode>("speed").get(0L).toFloat()
        flyspeed = info.getChild<NXNode>("flySpeed").get(0L).toFloat()
        touchdamage = info.getChild<NXNode>("bodyAttack").get(0L) > 0
        undead = info.getChild<NXNode>("undead").get(0L) > 0
        noflip = info.getChild<NXNode>("noFlip").get(0L) > 0
        notattack = info.getChild<NXNode>("notAttack").get(0L) > 0
        canjump = src.isChildExist("jump")

        linkedNodes = if (info.isChildExist("link")) {
            Loaded.getFile(Loaded.WzFileName.MOB).root.getChild(info.getChild<NXNode>("link").get("").toString() + ".img")
        } else {
            src
        }

        canfly = linkedNodes.isChildExist("fly")
        canmove = linkedNodes.isChildExist("move") || canfly

        if (canfly) {
            putAnimation(Mob.Stance.STAND, linkedNodes.getChild<NXNode>("fly"))
            putAnimation(Mob.Stance.MOVE, linkedNodes.getChild<NXNode>("fly"))
        } else {
            putAnimation(Mob.Stance.STAND, linkedNodes.getChild<NXNode>("stand"))
            putAnimation(Mob.Stance.MOVE, linkedNodes.getChild<NXNode>("move"))
        }

        putAnimation(Mob.Stance.JUMP, linkedNodes.getChild<NXNode>("jump"))
        putAnimation(Mob.Stance.HIT, linkedNodes.getChild<NXNode>("hit1"))
        putAnimation(Mob.Stance.DIE, linkedNodes.getChild<NXNode>("die1"))

        name = Loaded.getFile(Loaded.WzFileName.STRING).root.getChild<NXNode>("Mob.img").getChild<NXNode>(id).getChild<NXNode>("name").get<String>("")

        val sndsrc = Loaded.getFile(Loaded.WzFileName.SOUND).root.getChild<NXNode>("Mob.img").getChild<NXNode>(strid)

        hitsound = Sound(sndsrc.getChild<NXNode>("Damage"))
        diesound = Sound(sndsrc.getChild<NXNode>("Die"))

        speed += 100
        speed *= 0.001f

        flyspeed += 100
        flyspeed *= 0.0005f

        fixAnimatins()
    }

    fun putAnimation(stance: Mob.Stance, src: NXNode?) {
        if (src == null || src.isNotExist) {
            return
        }
        animations[stance] = AnimationModel(src)
    }


    private fun fixAnimatins() {
        animations.values.forEach(AnimationModel::shiftByHead)
    }

}
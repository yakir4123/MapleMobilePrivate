package com.bapplications.maplemobile.gameplay.player.look

import com.bapplications.maplemobile.constatns.Loaded
import com.bapplications.maplemobile.gameplay.player.Stance
import com.bapplications.maplemobile.gameplay.player.inventory.EquipData
import com.bapplications.maplemobile.gameplay.player.inventory.EquipSlot
import com.bapplications.maplemobile.gameplay.player.inventory.ItemData
import com.bapplications.maplemobile.gameplay.textures.Texture
import com.bapplications.maplemobile.pkgnx.NXNode
import com.bapplications.maplemobile.pkgnx.nodes.NXBitmapNode
import com.bapplications.maplemobile.pkgnx.nodes.NXPointNode
import com.bapplications.maplemobile.utils.DrawArgument
import com.bapplications.maplemobile.utils.Point
import java.util.*

class Clothing(val id: Int, drawInfo: BodyDrawInfo) {
    val vSlot: String
    private var walk: Stance.Id? = null
    private var stand: Stance.Id? = null
    var isTwoHanded = false
    private val transparent: Boolean
    val eqSlot: EquipSlot.Id
    private val stances:
            EnumMap<Stance.Id,
                    EnumMap<Layer,
                            HashMap<Byte,
                                    MutableSet<Texture>
                                    ?>
                            ?>
                    ?> = EnumMap(Stance.Id::class.java)
    fun draw(stance: Stance.Id?, layer: Layer?, frame: Byte, args: DrawArgument?) {
        val layers = stances[stance]?.get(layer) ?: return
        for (tex in layers[frame]!!) tex.draw(args)
    }

    enum class Layer {
        CAPE, SHOES, PANTS, TOP, MAIL, MAILARM, EARRINGS, FACEACC, EYEACC, PENDANT, BELT, MEDAL, RING, CAP, CAP_BELOW_BODY, CAP_OVER_HAIR, GLOVE, WRIST, GLOVE_OVER_HAIR, WRIST_OVER_HAIR, GLOVE_OVER_BODY, WRIST_OVER_BODY, SHIELD, BACKSHIELD, SHIELD_BELOW_BODY, SHIELD_OVER_HAIR, WEAPON, BACKWEAPON, WEAPON_BELOW_ARM, WEAPON_BELOW_BODY, WEAPON_OVER_HAND, WEAPON_OVER_BODY, WEAPON_OVER_GLOVE, NUM_LAYERS
    }

    companion object {
        private val transparents_items: MutableSet<Int> = HashSet()
        private val sublayerNames: MutableMap<String, Layer> = HashMap()

        init {
            transparents_items.add(1002186)

            // WEAPON
            sublayerNames["weaponOverHand"] = Layer.WEAPON_OVER_HAND
            sublayerNames["weaponOverGlove"] = Layer.WEAPON_OVER_GLOVE
            sublayerNames["weaponOverBody"] = Layer.WEAPON_OVER_BODY
            sublayerNames["weaponBelowArm"] = Layer.WEAPON_BELOW_ARM
            sublayerNames["weaponBelowBody"] = Layer.WEAPON_BELOW_BODY
            sublayerNames["backWeaponOverShield"] = Layer.BACKWEAPON
            // SHIELD
            sublayerNames["shieldOverHair"] = Layer.SHIELD_OVER_HAIR
            sublayerNames["shieldBelowBody"] = Layer.SHIELD_BELOW_BODY
            sublayerNames["backShield"] = Layer.BACKSHIELD
            // GLOVE
            sublayerNames["gloveWrist"] = Layer.WRIST
            sublayerNames["gloveOverHair"] = Layer.GLOVE_OVER_HAIR
            sublayerNames["gloveOverBody"] = Layer.GLOVE_OVER_BODY
            sublayerNames["gloveWristOverHair"] = Layer.WRIST_OVER_HAIR
            sublayerNames["gloveWristOverBody"] = Layer.WRIST_OVER_BODY
            // CAP
            sublayerNames["capOverHair"] = Layer.CAP_OVER_HAIR
            sublayerNames["capBelowBody"] = Layer.CAP_BELOW_BODY
        }
    }

    init {
        val equipData = ItemData.get(id) as EquipData
        eqSlot = equipData.eqSlot
        if (eqSlot == EquipSlot.Id.WEAPON) {
//            twohanded = WeaponData.get(itemid).isTwoHanded();
        } else {
            isTwoHanded = false
        }
        val NON_WEAPON_TYPES = 15
        val WEAPON_OFFSET = NON_WEAPON_TYPES + 15
        val WEAPON_TYPES = 20
        val layers = arrayOf(
                Layer.CAP,
                Layer.FACEACC,
                Layer.EYEACC,
                Layer.EARRINGS,
                Layer.TOP,
                Layer.MAIL,
                Layer.PANTS,
                Layer.SHOES,
                Layer.GLOVE,
                Layer.SHIELD,
                Layer.CAPE,
                Layer.RING,
                Layer.PENDANT,
                Layer.BELT,
                Layer.MEDAL
        )
        val chlayer: Layer
        val index = id / 10000 - 100
        chlayer = if (index < NON_WEAPON_TYPES) layers[index] else if (index >= WEAPON_OFFSET && index < WEAPON_OFFSET + WEAPON_TYPES) Layer.WEAPON else Layer.CAPE
        val strid = "0$id"
        val category = equipData.category
        val src = Loaded.getFile(Loaded.WzFileName.CHARACTER).root.getChild<NXNode>(category).getChild<NXNode>("$strid.img")
        val info = src.getChild<NXNode>("info")
        vSlot = info.getChild<NXNode>("vslot").get("")
        val standno = info.getChild<NXNode>("stand").get(0L).toInt()
        stand = when (standno) {
            1 -> Stance.Id.STAND1
            2 -> Stance.Id.STAND2
            else -> if (isTwoHanded) Stance.Id.STAND2 else Stance.Id.STAND1
        }
        val walkno = info.getChild<NXNode>("walk").get(0L).toInt()
        walk = when (walkno) {
            1 -> Stance.Id.WALK1
            2 -> Stance.Id.WALK2
            else -> if (isTwoHanded) Stance.Id.WALK2 else Stance.Id.WALK1
        }
        for (stance in Stance.mapStances.keys) {
            val stancename = Stance.mapStances[stance]
            val stancenode = src.getChild<NXNode>(stancename)
            if (stancenode.isNotExist) continue
            var framenode = stancenode.getChild<NXNode>(0)
            var frame: Byte = 0
            while (framenode.isExist) {
                framenode = stancenode.getChild(frame.toInt())
                for (partnode in framenode) {
                    val part = partnode.name
                    if (partnode.isNotExist || partnode !is NXBitmapNode) continue
                    var z: Layer?
                    val zs = partnode.getChild<NXNode>("z").get("")
                    if (part == "mailArm") {
                        z = Layer.MAILARM
                    } else {
                        z = sublayerNames[zs]
                        if (z == null) {
                            z = chlayer
                        }
                    }
                    var parent = ""
                    var parentpos = Point()
                    for (mapnode in partnode.getChild<NXNode>("map")) {
                        if (mapnode is NXPointNode) {
                            parent = mapnode.getName()
                            parentpos = Point(mapnode)
                        }
                    }
                    val mapnode = partnode.getChild<NXNode>("map")
                    var shift = Point()
                    when (eqSlot) {
                        EquipSlot.Id.FACE -> shift = parentpos.negateSign()
                        EquipSlot.Id.SHOES, EquipSlot.Id.GLOVES, EquipSlot.Id.TOP, EquipSlot.Id.BOTTOM, EquipSlot.Id.CAPE -> shift = drawInfo.getBodyPosition(stance, frame).minus(parentpos)
                        EquipSlot.Id.HAT, EquipSlot.Id.EARRINGS, EquipSlot.Id.EYEACC -> //                            shift = drawInfo.getFacePos(stance, (byte) frame).minus(parentpos);
                            shift = drawInfo.getHairPos(stance, frame).minus(parentpos)
                        EquipSlot.Id.SHIELD, EquipSlot.Id.WEAPON -> {
                            when (parent) {
                                "handMove" -> shift.offset(drawInfo.getHandPosition(stance, frame))
                                "hand" -> shift.offset(drawInfo.getArmPosition(stance, frame))
                                "navel" -> shift.offset(drawInfo.getBodyPosition(stance, frame))
                            }
                            shift.offset(parentpos.negateSign())
                        }
                    }
                    val tex = Texture(partnode)
                    tex.shift(shift)
                    if (stances[stance] == null) {
                        stances[stance] = EnumMap(Layer::class.java)
                    }
                    if (stances[stance]!![z] == null) {
                        stances[stance]!![z] = HashMap()
                    }
                    if (stances[stance]!![z]!![frame] == null) {
                        stances[stance]!![z]!![frame] = HashSet()
                    }
                    stances[stance]!![z]!![frame]!!.add(tex)
                }
                ++frame
            }
        }
        transparent = transparents_items.contains(id)
    }
}
package com.bapplications.maplemobile.gameplay.textures

import android.util.Pair
import com.bapplications.maplemobile.pkgnx.NXNode
import com.bapplications.maplemobile.utils.Point
import com.bapplications.maplemobile.utils.Rectangle

class Frame : Texture {
    lateinit var head: Point
        private set
    var delay: Short = 0
    lateinit var bounds: Rectangle
        private set
    private var scales: Pair<Byte, Byte>? = null
    private var opacities: Pair<Byte, Byte>? = null

    constructor(src: NXNode, recycle: Boolean = true) : super(src, true, recycle) {
        bounds = Rectangle(src)
        head = Point(src.getChild("head"))
        head.flipY()
        delay = try {
            src.getChild<NXNode>("delay").get(0L).toShort()
        } catch (e: ClassCastException) {
            // There are cases where delay is string node and not a numeric node
            src.getChild<NXNode>("delay").get("0").toShort()
        }
        if (delay.toInt() == 0) delay = 100
        val a0 = src.getChild<NXNode>("a0")
        val a1 = src.getChild<NXNode>("a1")
        val hasa0 = !a0.isNotExist
        val hasa1 = !a1.isNotExist
        opacities = if (hasa0 && hasa1) {
            Pair(a0.get(0L).toByte(), a1.get(0L).toByte())
        } else if (hasa0) {
            val a0v = a0.get(0L).toByte()
            Pair(a0v, (255 - a0v).toByte())
        } else if (hasa1) {
            val a1v = a1.get(0L).toByte()
            Pair((255 - a1v).toByte(), a1v)
        } else {
            Pair(255.toByte(), 255.toByte())
        }
        val z0 = src.getChild<NXNode>("z0")
        val z1 = src.getChild<NXNode>("z1")
        val hasz0 = !z0.isNotExist
        val hasz1 = !z1.isNotExist
        if (hasz0 && hasz1) {
            scales = Pair<Byte, Byte>(z0.get(0L).toByte(), z1.get(0L).toByte())
        } else if (hasz0) {
            val z0v = z0.get(0L).toByte()
            scales = Pair<Byte, Byte>(z0v, (100 - z0v).toByte())
        } else if (hasz1) {
            val z1v = z1.get(0L).toByte()
            scales = Pair<Byte, Byte>((100 - z1v).toByte(), z1v)
        } else {
            scales = Pair<Byte, Byte>(100.toByte(), 100.toByte())
        }
    }

    constructor() : super() {
        delay = 0
        opacities = Pair(0.toByte(), 0.toByte())
        scales = Pair(0.toByte(), 0.toByte())
    }

    fun startOpacity(): Byte {
        return opacities!!.first
    }

    fun startScale(): Byte {
        return scales!!.first
    }

    fun opcstep(timestep: Int): Float {
        return timestep * (opacities!!.second - opacities!!.first).toFloat() / delay
    }

    fun scalestep(timestep: Int): Float {
        return timestep * (scales!!.second!! - scales!!.first).toFloat() / delay
    }
}
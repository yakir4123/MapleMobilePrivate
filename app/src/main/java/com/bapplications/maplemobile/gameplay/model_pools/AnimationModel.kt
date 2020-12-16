package com.bapplications.maplemobile.gameplay.model_pools

import com.bapplications.maplemobile.gameplay.textures.Frame
import com.bapplications.maplemobile.utils.StaticUtils
import com.bapplications.maplemobile.pkgnx.NXNode
import com.bapplications.maplemobile.pkgnx.nodes.NXBitmapNode
import com.bapplications.maplemobile.utils.Point
import java.util.*

open class AnimationModel {
    protected var frames: MutableList<Frame> = mutableListOf();
    var zigzag = false
    val animated: Boolean

    constructor(src: NXNode, z: Any? = "0") {
        zigzag = src.getChild<NXNode>("zigzag").get(0L) > 0
        if (src is NXBitmapNode) {
            frames.add(Frame(src))
        } else {
            val frameids: MutableSet<Short> = HashSet()
            for (sub in src) {
                if (sub is NXBitmapNode) {
                    val fid = StaticUtils.orDefault(sub.getName(), -1).toShort()
                    if (fid >= 0) frameids.add(fid)
                }
            }
            for (fid in frameids) {
                val sub = src.getChild<NXNode>("" + fid)
                val frame = Frame(sub)
                frame.setZ(z)
                frames.add(frame)
            }
            if (frames.isEmpty()) frames.add(Frame())
        }
        animated = size() > 1
    }

    operator fun get(index: Int): Frame = frames[index]

    fun size(): Int = frames.size

    fun shiftByHead() {
        for (frame in frames) {
            frame.shiftY(-frame.head.y)
        }
    }

    fun getZ(): Any? {
        return frames[0].getZ()
    }

    fun dimensions(frame: Int) : Point = frames[frame].dimension

}
package com.bapplications.maplemobile.gameplay.textures

import com.bapplications.maplemobile.gameplay.model_pools.AnimationModel
import com.bapplications.maplemobile.pkgnx.NXNode
import com.bapplications.maplemobile.utils.*

open class Animation() {
    protected var pos: Point = Point()
        set(value){
            value.y *= -1f
            field = value
        }
    protected var framestep = 0
    protected var lookLeft = true
    protected var delay: Short = 0
    protected var opacity: Linear = Linear()
    protected var xyscale: Linear = Linear()
    protected lateinit var model: AnimationModel
    protected var frameNumber : Nominal<Short> = Nominal<Short>()

    constructor(model: AnimationModel) : this() {
        this.model = model
        reset()
    }

    constructor(src: NXNode, z: Any? = 0) : this(AnimationModel(src, z))


    fun reset() {
        frameNumber.set(0.toShort())
        opacity.set(model[0].startOpacity().toFloat())
        xyscale.set(model[0].startScale().toFloat())
        delay = model[0].delay
        framestep = 1
    }

    fun draw(args: DrawArgument, alpha: Float) {
        val interframe = frameNumber[alpha]
        args.setDirection(lookLeft)
        args.offsetPosition(pos)
        model[interframe.toInt()].draw(args)
        args.minusPosition(pos)

    }

    open fun update(deltatime: Int): Boolean {
        val framedata = getFrameNumber()
        opacity.plus(framedata.opcstep(deltatime))
        if (opacity.last() < 0.0f) opacity.set(0.0f) else if (opacity.last() > 255.0f) opacity.set(255.0f)
        xyscale.plus(framedata.scalestep(deltatime))
        if (xyscale.last() < 0.0f) opacity.set(0.0f)
        return if (deltatime >= delay) {
            val lastframe = (model.size() - 1).toShort()
            val nextframe: Short
            val ended: Boolean
            if (model.zigzag && lastframe > 0) {
                if (framestep == 1 && frameNumber.equals(lastframe)) {
                    framestep = -framestep
                    ended = false
                } else if (framestep == -1 && frameNumber.equals(0.toShort())) {
                    framestep = -framestep
                    ended = true
                } else {
                    ended = false
                }
                nextframe = frameNumber.plus(framestep.toShort()) as Short
            } else {
                if (frameNumber.equals(lastframe)) {
                    nextframe = 0
                    ended = true
                } else {
                    nextframe = frameNumber.plus(1.toShort()) as Short
                    ended = false
                }
            }
            val delta = deltatime - delay
            val threshold = delta.toFloat() / deltatime
            frameNumber.next(nextframe, threshold)
            delay = try {
                model[nextframe.toInt()].delay
            } catch (e: ArrayIndexOutOfBoundsException) {
                1
            }
            if (delay >= delta) {
                delay = (delay - delta).toShort()
            }
            opacity.set(model[nextframe.toInt()].startOpacity().toFloat())
            xyscale.set(model[nextframe.toInt()].startScale().toFloat())
            ended
        } else {
            frameNumber.normalize()
            delay = (delay - deltatime).toShort()

            false
        }
    }

    val dimensions: Point
        get() = getFrameNumber().dimension

    private fun getFrameNumber(): Frame {
        return model[frameNumber.get().toInt()]
    }

    fun getZ(): Any? {
        return model.getZ()
    }

    val frame: Frame
        get() = model[frameNumber.get().toInt()]
    val head: Point
        get() = frame.head
    val bounds: Rectangle
        get() = Rectangle(frame.bounds)
}
package com.bapplications.maplemobile.utils

import android.util.Log

class TimedBool {
    var last: Long = 0
    var delay: Long = 0
    var isTrue = false
        private set

    var onUpdate: ((timedBool: TimedBool) -> Any)? = null
    var onComplete: ((timedBool: TimedBool) -> Any)? = null

    fun setOnUpdate(onUpdate: ((timedBool: TimedBool) -> Any)?): TimedBool {
        this.onUpdate = onUpdate
        return this
    }

    fun setOnComplete(onComplete: ((timedBool: TimedBool) -> Any)?): TimedBool {
        this.onComplete = onComplete
        return this
    }

    fun setFor(millis: Long) : TimedBool {
        last = millis
        delay = millis
        isTrue = true
        return this
    }

    fun update(deltatime: Int) {
        if (isTrue) {
            if (deltatime >= delay) {
                isTrue = false
                delay = 0
                onComplete?.invoke(this)
            } else {
                delay -= deltatime.toLong()
                onUpdate?.invoke(this)
            }
        }
    }

    fun set(b: Boolean) {
        isTrue = b
        delay = 0
        last = 0
    }

    fun equals(b: Boolean): Boolean {
        return isTrue == b
    }

    fun alpha(): Float {
        return 1.0f - delay.toFloat() / last
    }

    fun getPercent() : Float {
        return 100f * delay / last
    }

    fun reset() {
        delay = last
        isTrue = true
    }
}
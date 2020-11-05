package com.bapplications.maplemobile.utils

class TimedBool {
    private var last: Long = 0
    private var delay: Long = 0
    var isTrue = false
        private set

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
            } else {
                delay -= deltatime.toLong()
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
}
package com.bapplications.maplemobile.utils

class Range (first: Float, second: Float) {
    var upper: Float = 0f
    var lower: Float = 0f

    fun size(): Float = upper - lower

    fun intersect(v: Range): Boolean = contains(v.lower) || contains(v.upper) ||
            v.contains(lower) || v.contains(upper)

    operator fun contains(v: Float): Boolean = v > lower && v < upper

    operator fun compareTo(x: Float): Int {
        if(x < lower) {
            return 1
        } else if( upper < x) {
            return -1
        }
        return 0
    }

    val center : Float
        get() = lower + (upper - lower) / 2

    val isDot: Boolean
        get() = lower == upper

    init {
        if (first <= second) {
            lower = first
            upper = second
        } else {
            lower = second
            upper = first
        }
    }

    override fun toString(): String {
        return "[$lower, $upper]"
    }
}



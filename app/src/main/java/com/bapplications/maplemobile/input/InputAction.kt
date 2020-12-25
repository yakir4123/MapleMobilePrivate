package com.bapplications.maplemobile.input

import android.util.Log

open class InputAction internal constructor(val key: Key, val type: Type, val time: Long = 0) {
    enum class Key {
        UP, DOWN, LEFT, RIGHT, JUMP, LOOT, EXPRESSION
    }

    enum class Type {
        SINGLE_CLICK, CONTINUES_CLICK, TIMED_CLICK
    }

    companion object {
        @JvmField
        var UP_ARROW_KEY = InputAction(Key.UP, Type.CONTINUES_CLICK)
        @JvmField
        var LEFT_ARROW_KEY = InputAction(Key.LEFT, Type.CONTINUES_CLICK)
        @JvmField
        var DOWN_ARROW_KEY = InputAction(Key.DOWN, Type.CONTINUES_CLICK)
        @JvmField
        var RIGHT_ARROW_KEY = InputAction(Key.RIGHT, Type.CONTINUES_CLICK)
        @JvmField
        var JUMP_KEY = InputAction(Key.JUMP, Type.SINGLE_CLICK)
        @JvmField
        var LOOT_KEY = InputAction(Key.LOOT, Type.TIMED_CLICK, 5000)
        fun byKey(key: Key?): InputAction? {
            when (key) {
                Key.UP -> return UP_ARROW_KEY
                Key.DOWN -> return DOWN_ARROW_KEY
                Key.LEFT -> return LEFT_ARROW_KEY
                Key.RIGHT -> return RIGHT_ARROW_KEY
                Key.JUMP -> return JUMP_KEY
                Key.LOOT -> return LOOT_KEY
                else -> Log.e("null error", "Did you add a new type of key and you didn't add this to byKey function?")
            }
            return null
        }
    }
}
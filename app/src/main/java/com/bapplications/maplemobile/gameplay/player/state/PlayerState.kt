package com.bapplications.maplemobile.gameplay.player.state

import com.bapplications.maplemobile.gameplay.player.look.Char
import com.bapplications.maplemobile.input.InputAction

interface PlayerState {
    fun initialize(player: Char)
    fun update(player: Char)
    fun updateState(player: Char)
    fun sendAction(player: Char, key: InputAction): Boolean {
        return false
    }
}
package com.bapplications.maplemobile.gameplay.inputs

import com.bapplications.maplemobile.gameplay.player.Player
import com.bapplications.maplemobile.views.GameViewController
import kotlin.collections.HashMap

class InputHandler {

    private val onClick = HashMap<GameViewController, (Player) -> Boolean>()
    private val onRelease = HashMap<GameViewController, (Player) -> Boolean>()

    fun bindInput(controller: GameViewController,
                  onClickAction: (Player) -> Boolean) {
        bindInput(controller, onClickAction, { _ -> true})
    }

    fun bindInput(controller: GameViewController,
                  onClickAction: (Player) -> Boolean,
                  onReleaseAction: (Player) -> Boolean = { _ -> true}) {
        onClick[controller] = onClickAction
        onRelease[controller] = onReleaseAction
    }

    fun handleClick() : Map<GameViewController, (Player) -> Boolean> {
        return onClick.filterKeys{it.isPressed }
    }

    fun handleReleased() : Map<GameViewController, (Player) -> Boolean> {
        return onRelease.filterKeys{it.isReleased }
    }



}
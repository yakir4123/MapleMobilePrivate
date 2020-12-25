package com.bapplications.maplemobile.gameplay.player.state

import com.bapplications.maplemobile.gameplay.physics.PhysicsObject
import com.bapplications.maplemobile.gameplay.player.look.Char
import com.bapplications.maplemobile.input.InputAction

class PlayerClimbState : PlayerState {
    override fun initialize(player: Char) {
        player.phobj.type = PhysicsObject.Type.FIXATED
    }

    override fun update(player: Char) {
        if (player.isPressed(InputAction.UP_ARROW_KEY) && !player.isPressed(InputAction.DOWN_ARROW_KEY)) {
            player.phobj.vspeed = -player.climbForce
        } else if (player.isPressed(InputAction.DOWN_ARROW_KEY) && !player.isPressed(InputAction.UP_ARROW_KEY)) {
            player.phobj.vspeed = player.climbForce
        } else {
            player.phobj.vspeed = 0.0f
        }
    }

    override fun updateState(player: Char) {
        val y = player.phobj.position.y.toShort()
        val downwards = player.isPressed(InputAction.DOWN_ARROW_KEY)
        val ladder = player.ladder
        if (ladder != null && ladder.fellOff(y, downwards)) cancelLadder(player)
    }

    override fun sendAction(player: Char, key: InputAction): Boolean {
        if (player.isAttacking) return false
        if (key === InputAction.JUMP_KEY && player.hasWalkInput()) {
            player.playJumpSound()
            val walkforce = player.walkForce * 8.0f
            player.lookLeft = player.isPressed(InputAction.LEFT_ARROW_KEY)
            player.phobj.hspeed = if (player.isPressed(InputAction.LEFT_ARROW_KEY)) -walkforce else walkforce
            player.phobj.vspeed = -player.jumpForce / 1.5f
            cancelLadder(player)
            return true
        }
        return super.sendAction(player, key)
    }

    private fun cancelLadder(player: Char) {
        player.state = Char.State.FALL
        player.ladder = null
    }
}
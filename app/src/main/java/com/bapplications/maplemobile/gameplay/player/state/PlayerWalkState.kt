package com.bapplications.maplemobile.gameplay.player.state

import com.bapplications.maplemobile.gameplay.physics.PhysicsObject
import com.bapplications.maplemobile.gameplay.player.look.Char
import com.bapplications.maplemobile.input.InputAction

class PlayerWalkState : PlayerState {
    override fun initialize(player: Char) {}
    override fun update(player: Char) {
        if (!player.phobj.enablejd) player.phobj.setFlag(PhysicsObject.Flag.CHECKBELOW)
        if (player.isAttacking) return
        if (player.hasWalkInput()) {
            if (player.isPressed(InputAction.RIGHT_ARROW_KEY)) {
                player.lookLeft = false
                player.phobj.hforce += player.walkForce
            } else if (player.isPressed(InputAction.LEFT_ARROW_KEY)) {
                player.lookLeft = true
                player.phobj.hforce -= player.walkForce
            }
        } else {
            if (player.isPressed(InputAction.DOWN_ARROW_KEY)) player.state = Char.State.PRONE
        }
    }

    override fun updateState(player: Char) {
        if (player.phobj.onground) {
            if (!player.hasWalkInput() || player.phobj.hspeed == 0.0f) player.state = Char.State.STAND
        } else {
            player.state = Char.State.FALL
        }
    }

    override fun sendAction(player: Char, key: InputAction): Boolean {
        if (player.isAttacking) return false
        if (key === InputAction.JUMP_KEY) {
            player.playJumpSound()
            player.phobj.vforce = -player.jumpForce
            return true
        }
        return super.sendAction(player, key)
    }
}
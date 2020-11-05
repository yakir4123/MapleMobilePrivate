package com.bapplications.maplemobile.gameplay.player.state

import com.bapplications.maplemobile.gameplay.physics.PhysicsObject
import com.bapplications.maplemobile.gameplay.player.look.Char
import com.bapplications.maplemobile.input.InputAction

class PlayerStandState : PlayerState {
    override fun initialize(player: Char) {
        player.phobj.type = PhysicsObject.Type.NORMAL
        player.phobj.vspeed = 0f
    }

    override fun update(player: Char) {
        if (!player.phobj.enablejd) player.phobj.setFlag(PhysicsObject.Flag.CHECKBELOW)
        if (player.isAttacking) return
        if (player.isPressed(InputAction.RIGHT_ARROW_KEY)) {
            player.lookLeft = false
            player.state = Char.State.WALK
        } else if (player.isPressed(InputAction.LEFT_ARROW_KEY)) {
            player.lookLeft = true
            player.state = Char.State.WALK
        }
        if (player.isPressed(InputAction.DOWN_ARROW_KEY)
                && !player.isPressed(InputAction.UP_ARROW_KEY)
                && !player.hasWalkInput()) player.state = Char.State.PRONE
    }

    override fun updateState(player: Char) {
        if (!player.phobj.onground) player.state = Char.State.FALL
    }

    override fun sendAction(player: Char, key: InputAction): Boolean {
        if (player.isAttacking) return false
        when(key) {
            InputAction.JUMP_KEY -> {
                player.playJumpSound()
                player.phobj.vforce = -player.jumpForce
                return true
            }
        }
        return super.sendAction(player, key)
    }
}
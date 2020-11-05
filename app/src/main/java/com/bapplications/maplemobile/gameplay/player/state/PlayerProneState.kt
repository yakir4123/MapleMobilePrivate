package com.bapplications.maplemobile.gameplay.player.state

import com.bapplications.maplemobile.gameplay.physics.PhysicsObject
import com.bapplications.maplemobile.gameplay.player.look.Char
import com.bapplications.maplemobile.input.InputAction

class PlayerProneState : PlayerState {
    override fun initialize(player: Char) {}
    override fun update(player: Char) {
        if (!player.phobj.enablejd) player.phobj.setFlag(PhysicsObject.Flag.CHECKBELOW)
        if (player.isPressed(InputAction.UP_ARROW_KEY) || !player.isPressed(InputAction.DOWN_ARROW_KEY)) player.state = Char.State.STAND
        if (player.isPressed(InputAction.LEFT_ARROW_KEY)) {
            player.lookLeft = true
            player.state = Char.State.WALK
        }
        if (player.isPressed(InputAction.RIGHT_ARROW_KEY)) {
            player.lookLeft = false
            player.state = Char.State.WALK
        }
    }

    override fun updateState(player: Char) {}
    override fun sendAction(player: Char, key: InputAction): Boolean {
        if (key === InputAction.JUMP_KEY && player.isPressed(InputAction.DOWN_ARROW_KEY)
                && player.phobj.enablejd) {
            player.playJumpSound()
            player.phobj.y.set(player.phobj.groundbelow)
            player.state = Char.State.FALL
            return true
        }
        return super.sendAction(player, key)
    }
}
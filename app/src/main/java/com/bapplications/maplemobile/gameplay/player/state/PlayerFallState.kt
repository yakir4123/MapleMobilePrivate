package com.bapplications.maplemobile.gameplay.player.state

import com.bapplications.maplemobile.gameplay.physics.PhysicsObject
import com.bapplications.maplemobile.gameplay.player.look.Char
import com.bapplications.maplemobile.input.InputAction

class PlayerFallState : PlayerState {
    override fun initialize(player: Char) {
        player.phobj.type = PhysicsObject.Type.NORMAL
    }

    override fun update(player: Char) {
        if (player.isAttacking) return
        val hspeed = player.phobj.hspeed
        if (player.isPressed(InputAction.LEFT_ARROW_KEY) && hspeed > 0.0) player.phobj.hspeed -= 0.025f else if (player.isPressed(InputAction.RIGHT_ARROW_KEY) && hspeed < 0.0) player.phobj.hspeed += 0.025f
        if (player.isPressed(InputAction.LEFT_ARROW_KEY)) player.lookLeft = true else if (player.isPressed(InputAction.RIGHT_ARROW_KEY)) player.lookLeft = false
    }

    override fun updateState(player: Char) {
        if (player.phobj.onground) {
            if (player.isPressed(InputAction.DOWN_ARROW_KEY) && !player.hasWalkInput()) {
                player.state = Char.State.PRONE
            } else {
                player.state = Char.State.STAND
            }
        }
        //        else if (player.isUnderwater())
//        {
//            player.setState(Char.State.SWIM);
//        }
    }

    override fun sendAction(player: Char, key: InputAction): Boolean {
        return super.sendAction(player, key)
    }
}
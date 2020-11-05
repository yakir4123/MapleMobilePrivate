package com.bapplications.maplemobile.gameplay.player.state;

import com.bapplications.maplemobile.input.InputAction;
import com.bapplications.maplemobile.gameplay.physics.PhysicsObject;
import com.bapplications.maplemobile.gameplay.player.look.Char;
import com.bapplications.maplemobile.gameplay.player.Player;

public class PlayerFallState implements PlayerState {

    @Override
    public void initialize(Char player) {
        player.getPhobj().type = PhysicsObject.Type.NORMAL;
    }

    @Override
    public void update(Char player) {
        if (player.isAttacking())
            return;

        float hspeed = player.getPhobj().hspeed;

        if (player.isPressed(InputAction.LEFT_ARROW_KEY) && hspeed > 0.0)
            player.getPhobj().hspeed -= 0.025;
        else if (player.isPressed(InputAction.RIGHT_ARROW_KEY) && hspeed < 0.0)
            player.getPhobj().hspeed += 0.025;

        if (player.isPressed(InputAction.LEFT_ARROW_KEY))
            player.setLookLeft(true);
        else if (player.isPressed(InputAction.RIGHT_ARROW_KEY))
            player.setLookLeft(false);

    }

    @Override
    public void updateState(Char player) {
        if (player.getPhobj().onground)
        {
            if (player.isPressed(InputAction.DOWN_ARROW_KEY) && !player.hasWalkInput()){
                player.setState(Char.State.PRONE);
            } else {
                player.setState(Char.State.STAND);
            }
        }
//        else if (player.isUnderwater())
//        {
//            player.setState(Char.State.SWIM);
//        }

    }

    @Override
    public boolean sendAction(Char player, InputAction key) {
        return false;
    }
}
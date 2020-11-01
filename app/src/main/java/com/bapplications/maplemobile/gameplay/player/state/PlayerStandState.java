package com.bapplications.maplemobile.gameplay.player.state;

import com.bapplications.maplemobile.input.InputAction;
import com.bapplications.maplemobile.gameplay.physics.PhysicsObject;
import com.bapplications.maplemobile.gameplay.player.look.Char;
import com.bapplications.maplemobile.gameplay.player.Player;

public class PlayerStandState implements PlayerState {


    @Override
    public void initialize(Char player) {
        player.getPhobj().type = PhysicsObject.Type.NORMAL;
        player.getPhobj().vspeed = 0;
    }

    @Override
    public void update(Char player) {
        if (!player.getPhobj().enablejd)
            player.getPhobj().setFlag(PhysicsObject.Flag.CHECKBELOW);

        if (player.isAttacking())
            return;

        if (player.isPressed(InputAction.RIGHT_ARROW_KEY))
        {
            player.setLookLeft(false);
            player.setState(Char.State.WALK);
        }
        else if (player.isPressed(InputAction.LEFT_ARROW_KEY))
        {
            player.setLookLeft(true);
            player.setState(Char.State.WALK);
        }

        if (player.isPressed(InputAction.DOWN_ARROW_KEY)
                && !player.isPressed(InputAction.UP_ARROW_KEY)
                && !player.hasWalkInput())
            player.setState(Char.State.PRONE);

    }

    @Override
    public void updateState(Char player) {
        if (!player.getPhobj().onground)
            player.setState(Char.State.FALL);
    }

    @Override
    public boolean sendAction(Char player, InputAction key) {

        if (player.isAttacking())
            return false;

        if (key == InputAction.JUMP_KEY)
        {
            player.playJumpSound();

            player.getPhobj().vforce = -player.getJumpForce();
            return true;
        }
        return false;
    }
}
package com.bapplications.maplemobile.gameplay.player.state;

import com.bapplications.maplemobile.input.InputAction;
import com.bapplications.maplemobile.gameplay.physics.PhysicsObject;
import com.bapplications.maplemobile.gameplay.player.look.Char;
import com.bapplications.maplemobile.gameplay.player.Player;

public class PlayerWalkState implements PlayerState {
    @Override
    public void initialize(Char player) {

    }

    @Override
    public void update(Char player) {

        if (!player.getPhobj().enablejd)
            player.getPhobj().setFlag(PhysicsObject.Flag.CHECKBELOW);

        if (player.isAttacking())
            return;

        if (player.hasWalkInput())
        {
            if (player.isPressed(InputAction.RIGHT_ARROW_KEY))
            {
                player.setLookLeft(false);
                player.getPhobj().hforce += player.getWalkForce();
            }
            else if (player.isPressed(InputAction.LEFT_ARROW_KEY))
            {
                player.setLookLeft(true);
                player.getPhobj().hforce -= player.getWalkForce();
            }
        }
        else
        {
            if (player.isPressed(InputAction.DOWN_ARROW_KEY))
                player.setState(Char.State.PRONE);
        }

    }

    @Override
    public void updateState(Char player) {

        if (player.getPhobj().onground)
        {
            if (!player.hasWalkInput() || player.getPhobj().hspeed == 0.0f)
                player.setState(Char.State.STAND);
        }
        else
        {
            player.setState(Char.State.FALL);
        }
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
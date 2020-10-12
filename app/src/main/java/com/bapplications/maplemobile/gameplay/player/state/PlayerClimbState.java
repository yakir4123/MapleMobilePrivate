package com.bapplications.maplemobile.gameplay.player.state;

import com.bapplications.maplemobile.gameplay.inputs.InputAction;
import com.bapplications.maplemobile.gameplay.map.Ladder;
import com.bapplications.maplemobile.gameplay.physics.PhysicsObject;
import com.bapplications.maplemobile.gameplay.player.Char;
import com.bapplications.maplemobile.gameplay.player.Player;

public class PlayerClimbState implements PlayerState{
    @Override
    public void initialize(Player player) {
        player.getPhobj().type = PhysicsObject.Type.FIXATED;
    }

    @Override
    public void update(Player player) {
        if (player.isPressed(InputAction.UP_ARROW_KEY) && !player.isPressed(InputAction.DOWN_ARROW_KEY))
        {
            player.getPhobj().vspeed = -player.getClimbForce();
        }
        else if (player.isPressed(InputAction.DOWN_ARROW_KEY) && !player.isPressed(InputAction.UP_ARROW_KEY))
        {
            player.getPhobj().vspeed = player.getClimbForce();
        }
        else
        {
            player.getPhobj().vspeed = 0.0f;
        }

    }

    @Override
    public void updateState(Player player) {
        short y = (short) player.getPhobj().getPosition().y;
        boolean downwards = player.isPressed(InputAction.DOWN_ARROW_KEY);
        Ladder ladder = player.getLadder();

        if (ladder != null && ladder.fellOff(y, downwards))
            cancelLadder(player);

    }

    @Override
    public boolean sendAction(Player player, InputAction key) {

        if (player.isAttacking())
            return false;

        if (key == InputAction.JUMP_KEY && player.hasWalkInput())
        {
            player.playJumpSound();

            float walkforce = player.getWalkForce() * 8.0f;

            player.setLookLeft(player.isPressed(InputAction.LEFT_ARROW_KEY));

            player.getPhobj().hspeed = player.isPressed(InputAction.LEFT_ARROW_KEY) ? -walkforce : walkforce;
            player.getPhobj().vspeed = -player.getJumpForce() / 1.5f;

            cancelLadder(player);
            return true;
        }
        return false;
    }


    void cancelLadder(Player player)
    {
        player.setState(Char.State.FALL);
        player.setLadder(null);
        player.setClimbCooldown();
    }
}
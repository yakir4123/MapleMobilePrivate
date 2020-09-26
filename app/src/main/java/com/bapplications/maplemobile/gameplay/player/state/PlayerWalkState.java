package com.bapplications.maplemobile.gameplay.player.state;

import com.bapplications.maplemobile.gameplay.physics.PhysicsObject;
import com.bapplications.maplemobile.gameplay.player.Char;
import com.bapplications.maplemobile.gameplay.player.Player;
import com.bapplications.maplemobile.views.KeyAction;

public class PlayerWalkState implements PlayerState {
    @Override
    public void initialize(Player player) {

    }

    @Override
    public void update(Player player) {

        if (!player.getPhobj().enablejd)
            player.getPhobj().setFlag(PhysicsObject.Flag.CHECKBELOW);

        if (player.isAttacking())
            return;

        if (player.hasWalkInput())
        {
            if (player.isPressed(KeyAction.RIGHT_ARROW_KEY))
            {
                player.setDirection(false);
                player.getPhobj().hforce += player.getWalkForce();
            }
            else if (player.isPressed(KeyAction.LEFT_ARROW_KEY))
            {
                player.setDirection(true);
                player.getPhobj().hforce -= player.getWalkForce();
            }
        }
        else
        {
            if (player.isPressed(KeyAction.DOWN_ARROW_KEY))
                player.setState(Char.State.PRONE);
        }

    }

    @Override
    public void updateState(Player player) {

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
    public void sendAction(Player player, KeyAction key) {

        if (player.isAttacking())
            return;

        if (key == KeyAction.JUMP_KEY)
        {
            player.playJumpSound();

            player.getPhobj().vforce = -player.getJumpForce();
        }
    }

}

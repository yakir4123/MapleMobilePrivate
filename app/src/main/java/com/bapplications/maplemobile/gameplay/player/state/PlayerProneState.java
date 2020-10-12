package com.bapplications.maplemobile.gameplay.player.state;

import com.bapplications.maplemobile.gameplay.inputs.InputAction;
import com.bapplications.maplemobile.gameplay.physics.PhysicsObject;
import com.bapplications.maplemobile.gameplay.player.Char;
import com.bapplications.maplemobile.gameplay.player.Player;

public class PlayerProneState implements PlayerState {
    @Override
    public void initialize(Player player) {

    }

    @Override
    public void update(Player player) {

        if (!player.getPhobj().enablejd)
            player.getPhobj().setFlag(PhysicsObject.Flag.CHECKBELOW);

        if (player.isPressed(InputAction.UP_ARROW_KEY) || !player.isPressed(InputAction.DOWN_ARROW_KEY))
            player.setState(Char.State.STAND);

        if (player.isPressed(InputAction.LEFT_ARROW_KEY))
        {
            player.setLookLeft(true);
            player.setState(Char.State.WALK);
        }

        if (player.isPressed(InputAction.RIGHT_ARROW_KEY))
        {
            player.setLookLeft(false);
            player.setState(Char.State.WALK);
        }
    }

    @Override
    public void updateState(Player player) {

    }

    @Override
    public boolean sendAction(Player player, InputAction key) {

        if (key == InputAction.JUMP_KEY
                && player.isPressed(InputAction.DOWN_ARROW_KEY)
                && player.getPhobj().enablejd) {

            player.playJumpSound();

            player.getPhobj().y.set(player.getPhobj().groundbelow);
            player.setState(Char.State.FALL);
            return true;
        }
        return false;

    }
}
package com.bapplications.maplemobile.gameplay.player.state;

import com.bapplications.maplemobile.gameplay.physics.PhysicsObject;
import com.bapplications.maplemobile.gameplay.player.Char;
import com.bapplications.maplemobile.gameplay.player.Player;
import com.bapplications.maplemobile.views.KeyAction;

public class PlayerProneState implements PlayerState {
    @Override
    public void initialize(Player player) {

    }

    @Override
    public void update(Player player) {

        if (player.getPhobj().enablejd == false)
            player.getPhobj().setFlag(PhysicsObject.Flag.CHECKBELOW);

        if (player.isPressed(KeyAction.UP_ARROW_KEY) || !player.isPressed(KeyAction.DOWN_ARROW_KEY))
        player.setState(Char.State.STAND);

        if (player.isPressed(KeyAction.LEFT_ARROW_KEY))
        {
            player.setDirection(false);
            player.setState(Char.State.WALK);
        }

        if (player.isPressed(KeyAction.RIGHT_ARROW_KEY))
        {
            player.setDirection(true);
            player.setState(Char.State.WALK);
        }
    }

    @Override
    public void updateState(Player player) {

    }

    @Override
    public void sendAction(Player player, KeyAction key) {

        if (key == KeyAction.JUMP_KEY
                && player.isPressed(KeyAction.DOWN_ARROW_KEY)
                && player.getPhobj().enablejd) {

            player.playJumpSound();

            player.getPhobj().y.set(player.getPhobj().groundbelow);
            player.setState(Char.State.FALL);
        }

    }
}

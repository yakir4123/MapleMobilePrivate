package com.bapplications.maplemobile.gameplay.player.state;

import com.bapplications.maplemobile.gameplay.physics.PhysicsObject;
import com.bapplications.maplemobile.gameplay.player.Char;
import com.bapplications.maplemobile.gameplay.player.Player;
import com.bapplications.maplemobile.views.KeyAction;

public class PlayerFallState implements PlayerState {

    @Override
    public void initialize(Player player) {
        player.getPhobj().type = PhysicsObject.Type.NORMAL;
    }

    @Override
    public void update(Player player) {
        if (player.isAttacking())
            return;

        float hspeed = player.getPhobj().hspeed;

        if (player.isPressed(KeyAction.LEFT_ARROW_KEY) && hspeed > 0.0)
            player.getPhobj().hspeed -= 0.025;
        else if (player.isPressed(KeyAction.RIGHT_ARROW_KEY) && hspeed < 0.0)
            player.getPhobj().hspeed += 0.025;

        if (player.isPressed(KeyAction.LEFT_ARROW_KEY))
            player.setDirection(true);
        else if (player.isPressed(KeyAction.RIGHT_ARROW_KEY))
            player.setDirection(false);

    }

    @Override
    public void updateState(Player player) {
        if (player.getPhobj().onground)
        {
//            if (player.is_key_down(KeyAction::Id::DOWN) && !haswalkinput(player)){
//                player.set_state(Char.State.PRONE);
//            } else {
                player.setState(Char.State.STAND);
        }
//        else if (player.isUnderwater())
//        {
//            player.setState(Char.State.SWIM);
//        }

    }

    @Override
    public void sendAction(Player player, KeyAction key) {

    }
}

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

//        float hspeed = player.getPhobj().hspeed;

//        if (hasleftinput(player) && hspeed > 0.0)
//            hspeed -= 0.025;
//        else if (hasrightinput(player) && hspeed < 0.0)
//            hspeed += 0.025;

//        if (hasleftinput(player))
//            player.set_direction(false);
//        else if (hasrightinput(player))
//            player.set_direction(true);

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

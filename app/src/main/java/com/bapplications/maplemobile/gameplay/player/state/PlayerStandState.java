package com.bapplications.maplemobile.gameplay.player.state;

import com.bapplications.maplemobile.gameplay.physics.PhysicsObject;
import com.bapplications.maplemobile.gameplay.player.Char;
import com.bapplications.maplemobile.gameplay.player.Player;

public class PlayerStandState implements PlayerState {

    @Override
    public void initialize(Player player) {
        player.getPhobj().type = PhysicsObject.Type.NORMAL;
        player.getPhobj().vspeed = 0;
    }

    @Override
    public void update(Player player) {
        if (!player.getPhobj().enablejd)
            player.getPhobj().setFlag(PhysicsObject.Flag.CHECKBELOW);

        if (player.isAttacking())
            return;

//        if (hasrightinput(player))
//        {
//            player.set_direction(true);
//            player.set_state(Char::State::WALK);
//        }
//        else if (hasleftinput(player))
//        {
//            player.set_direction(false);
//            player.set_state(Char::State::WALK);
//        }

//        if (player.is_key_down(KeyAction::Id::DOWN) && !player.is_key_down(KeyAction::Id::UP) && !haswalkinput(player))
//        player.set_state(Char::State::PRONE);

    }

    @Override
    public void updateState(Player player) {
        if (!player.getPhobj().onground)
            player.setState(Char.State.FALL);

    }
}

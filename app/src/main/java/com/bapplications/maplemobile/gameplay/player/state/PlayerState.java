package com.bapplications.maplemobile.gameplay.player.state;

import com.bapplications.maplemobile.gameplay.player.Player;
import com.bapplications.maplemobile.input.InputAction;

public interface PlayerState {

    void initialize(Player player);
    void update(Player player);
    void updateState(Player player);

    boolean sendAction(Player player, InputAction key);
}

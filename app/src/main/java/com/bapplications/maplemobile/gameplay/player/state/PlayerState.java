package com.bapplications.maplemobile.gameplay.player.state;

import com.bapplications.maplemobile.gameplay.player.look.Char;
import com.bapplications.maplemobile.input.InputAction;

public interface PlayerState {

    void initialize(Char player);
    void update(Char player);
    void updateState(Char player);

    boolean sendAction(Char player, InputAction key);
}

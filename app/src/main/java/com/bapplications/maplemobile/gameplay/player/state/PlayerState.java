package com.bapplications.maplemobile.gameplay.player.state;

import com.bapplications.maplemobile.gameplay.player.Player;
import com.bapplications.maplemobile.views.KeyAction;

public interface PlayerState {

    void initialize(Player player);
    void update(Player player);
    void updateState(Player player);

    void sendAction(Player player, KeyAction key);
}

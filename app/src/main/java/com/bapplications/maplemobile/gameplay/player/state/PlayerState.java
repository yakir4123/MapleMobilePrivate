package com.bapplications.maplemobile.gameplay.player.state;

import com.bapplications.maplemobile.gameplay.player.Player;

public interface PlayerState {

    void initialize(Player player);
    void update(Player player);
    void updateState(Player player);
}

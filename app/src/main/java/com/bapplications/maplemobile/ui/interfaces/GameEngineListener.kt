package com.bapplications.maplemobile.ui.interfaces

import com.bapplications.maplemobile.gameplay.GameMap
import com.bapplications.maplemobile.gameplay.player.Player

interface GameEngineListener {
    fun onGameStarted()
    fun onMapLoaded(map: GameMap)
    fun onPlayerLoaded(player: Player)
    fun onChangedMap(mapId: Int)
}
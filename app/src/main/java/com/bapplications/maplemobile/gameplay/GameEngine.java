package com.bapplications.maplemobile.gameplay;

import com.bapplications.maplemobile.gameplay.player.Char;
import com.bapplications.maplemobile.gameplay.player.CharEntry;

public class GameEngine {

    private Stage stage;

    public GameEngine() {
        stage = new Stage();
        startGame();
    }


    public void startGame()
    {
        Char.init();
        stage.init();
    }



    public void update(int deltatime) {
        stage.update(deltatime);
    }
    public void drawFrame ()
    {
        stage.draw(1f);
//        UI::get().draw(alpha);

    }

    public void destroy() {

    }

    public Stage getStage() {
        return stage;
    }

    public void changeMap(int mapId) {
        stage.clear();
        stage.load(mapId,  0);

    }

    public void loadPlayer(int charId) {
        // for development this will be a new char instead from reading from a db
        CharEntry ce = new CharEntry(charId);
        stage.loadPlayer(ce);
    }
}

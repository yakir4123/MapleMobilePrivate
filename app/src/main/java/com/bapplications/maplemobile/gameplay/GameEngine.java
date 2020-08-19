package com.bapplications.maplemobile.gameplay;

public class GameEngine {

    private Stage stage;

    public GameEngine() {
        stage = new Stage();
        startGame();
    }


    public void startGame()
    {
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

}

package com.bapplications.maplemobile.opengl;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import com.bapplications.maplemobile.constatns.Loaded;
import com.bapplications.maplemobile.gameplay.GameEngine;
import com.bapplications.maplemobile.gameplay.audio.Sound;
import com.bapplications.maplemobile.gameplay.map.MapPortals;
import com.bapplications.maplemobile.gameplay.player.Char;
import com.bapplications.maplemobile.opengl.utils.RedCircle;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GameGLRenderer implements GLSurfaceView.Renderer {

    private static final String TAG = "GameGLRenderer";
    private static GameGLRenderer instance;

    private GameEngine engine;

    private long fpsTime = System.nanoTime();
    private int frames;
    private long before;
    private RedCircle tria;

    public static GameGLRenderer createInstance(){
        instance = new GameGLRenderer();
        return instance;
    }

    public static GameGLRenderer getInstance(){
        return instance;
    }

    private GameGLRenderer(){
        engine = new GameEngine();
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        GLES20.glClearColor(0.09019f, 0.10588f, 0.13333f, 0.0f);

        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendEquation(GLES20.GL_FUNC_ADD);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);


        GLState.initGL();
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {

        Log.d(TAG,"surface changed");

        // Adjust the viewport
        GLES20.glViewport(0, 0, width, height);
        Loaded.SCREEN_HEIGHT = height;
        Loaded.SCREEN_WIDTH = width;
        Loaded.SCREEN_RATIO = ((float)width) / height;

        GLState.setSpriteSquareRes();

        Matrix.orthoM(GLState._projectionMatrix, 0, -1, 1, -1, 1, 0, 1);

        // Set the camera position (View matrix)
        Matrix.setLookAtM(GLState._viewMatrix, 0, 0, 0, 1, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        // Calculate the projection and view transformation
        Matrix.multiplyMM(GLState._MVPMatrix, 0, GLState._projectionMatrix, 0, GLState._viewMatrix, 0);


        long start = System.currentTimeMillis();
        Char.init();
        Sound.init();
        MapPortals.init();
        long diff = System.currentTimeMillis() - start;
        Log.d(TAG, "diff init: "  + diff); // 0.3 ~ 1 s
        start = System.currentTimeMillis();
        engine.loadPlayer(0);
        engine.changeMap(50000);
//        engine.changeMap(100000000);
        diff = System.currentTimeMillis() - start;
        Log.d(TAG, "diff load map: "  + diff); // 8 s
    }

    @Override
    public void onDrawFrame(GL10 gl10) {

        // Draw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        long now = System.currentTimeMillis();
        engine.update((int) (now - before));
        engine.drawFrame();
        before = now;

        if (now - fpsTime >= 1000)
        {
            Log.d(TAG,String.format("fps: %d", frames));
            frames = 1;
            fpsTime = now;
        }
        else
        {
            frames++;
        }
    }

    public void setGameEngine (GameEngine engine)
    {
        this.engine = engine;
    }

    public GameEngine getGameEngine() {
        return engine;
    }
}

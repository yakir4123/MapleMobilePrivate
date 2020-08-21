package com.bapplications.maplemobile.opengl;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import com.bapplications.maplemobile.constatns.Loaded;
import com.bapplications.maplemobile.gameplay.GameEngine;
import com.bapplications.maplemobile.gameplay.player.Char;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GameGLRenderer implements GLSurfaceView.Renderer {

    private static final String TAG = "GameGLRenderer";
    private static GameGLRenderer instance;

    private GameEngine _engine;

    private long fpsTime = System.nanoTime();
    private int frames;
    private long before;

    public static GameGLRenderer createInstance(){
        instance = new GameGLRenderer();
        return instance;
    }

    public static GameGLRenderer getInstance(){
        return instance;
    }

    private GameGLRenderer(){
        _engine = new GameEngine();
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

        Char.init();
        _engine.loadPlayer(0);
        _engine.changeMap(100000000);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {

        // Draw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        long now = System.nanoTime();
        _engine.update((int) (now - before)/1000000);
        _engine.drawFrame();
        before = now;

        if (now - fpsTime >= 1000000000)
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
        _engine = engine;
    }

    public GameEngine getGameEngine() {
        return _engine;
    }
}

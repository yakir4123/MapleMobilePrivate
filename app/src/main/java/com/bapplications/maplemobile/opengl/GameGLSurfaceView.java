package com.bapplications.maplemobile.opengl;


import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.bapplications.maplemobile.game.GameEngine;

public class GameGLSurfaceView extends GLSurfaceView {

    // Set the Renderer for drawing on the GLSurfaceView
    private final GameGLRenderer mRenderer;
    private GameEngine engine;

    public GameGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setEGLContextClientVersion(2);

        setEGLConfigChooser(false);
        getHolder().setFormat(PixelFormat.RGBA_8888);

        mRenderer = GameGLRenderer.createInstance();
        setRenderer(mRenderer);
//        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        engine = new GameEngine();
        mRenderer.setGameEngine(engine);
    }

    public void exitGame() {
        queueEvent(() -> engine.destroy());
    }

    public GameEngine getGameEngine() {
        return engine;
    }

    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private float previousX;
    private float previousY;

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, you are only
        // interested in events where the touch position changed.

        float x = e.getX();
        float y = e.getY();

        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:

                float dx = x - previousX;
                float dy = y - previousY;

                // reverse direction of rotation above the mid-line
                if (y > getHeight() / 2) {
                    dx = dx * -1 ;
                }

                // reverse direction of rotation to left of the mid-line
                if (x < getWidth() / 2) {
                    dy = dy * -1 ;
                }
                requestRender();
                engine.getStage().getCamera().offsetPosition(-dx, -dy);
        }

        previousX = x;
        previousY = y;
        return true;
    }
}
package com.bapplications.maplemobile.opengl;


import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.bapplications.maplemobile.gameplay.GameEngine;

public class GameGLSurfaceView extends GLSurfaceView {

    // Set the Renderer for drawing on the GLSurfaceView
    private final GameGLRenderer mRenderer;

    public GameGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setEGLContextClientVersion(2);

        setEGLConfigChooser(false);
        getHolder().setFormat(PixelFormat.RGBA_8888);

        mRenderer = GameGLRenderer.createInstance();
        setRenderer(mRenderer);
//        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    public void exitGame() {
        queueEvent(() -> getGameEngine().destroy());
    }

    public GameEngine getGameEngine() {
        return mRenderer.getGameEngine();
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
//                getGameEngine().getStage().getCamera().offsetPosition(-dx, -dy);
        }

        previousX = x;
        previousY = y;
        return true;
    }
}
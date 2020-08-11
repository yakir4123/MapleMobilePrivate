package com.bapplications.maplemobile.opengl;


import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Range;

public class GameGLSurfaceView extends GLSurfaceView {

    // Set the Renderer for drawing on the GLSurfaceView
    private final GameGLRenderer mRenderer;

    public GameGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
         mRenderer = GameGLRenderer.createInstance(this);

        // Create an OpenGL ES 3.0 context.
        setEGLContextClientVersion(3);
        //fix for error No Config chosen, but I don't know what this does.
        super.setEGLConfigChooser(8 , 8, 8, 8, 16, 0);
        setRenderer(mRenderer);

        // Render the view only when there is a change in the drawing data
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

}
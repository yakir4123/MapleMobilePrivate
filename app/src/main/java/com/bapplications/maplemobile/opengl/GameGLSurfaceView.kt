package com.bapplications.maplemobile.opengl

import android.content.Context
import android.graphics.PixelFormat
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.Toast
import com.bapplications.maplemobile.gameplay.GameEngine
import com.bapplications.maplemobile.opengl.GameGLRenderer.Companion.createInstance

class GameGLSurfaceView(context: Context?, attrs: AttributeSet?) : GLSurfaceView(context, attrs) {
    // Set the Renderer for drawing on the GLSurfaceView
    private val mRenderer: GameGLRenderer
    fun exitGame() {
        queueEvent { gameEngine!!.destroy() }
    }

    val gameEngine: GameEngine?
        get() = mRenderer.gameEngine

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let { mRenderer.gameEngine?.onTouchEvent(it)}
        return super.onTouchEvent(event)
    }

    init {
        setEGLContextClientVersion(2)
        setEGLConfigChooser(false)
        holder.setFormat(PixelFormat.RGBA_8888)
        mRenderer = createInstance()
        setRenderer(mRenderer)
        renderMode = RENDERMODE_CONTINUOUSLY
    }
}
package com.bapplications.maplemobile.opengl

import android.content.Context
import android.graphics.PixelFormat
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import com.bapplications.maplemobile.gameplay.GameEngine
import com.bapplications.maplemobile.opengl.GameGLRenderer.Companion.createInstance

class GameGLSurfaceView(context: Context?, attrs: AttributeSet?) : GLSurfaceView(context, attrs) {
    // Set the Renderer for drawing on the GLSurfaceView
    val mRenderer: GameGLRenderer?
    fun exitGame() {
        queueEvent { gameEngine!!.destroy() }
    }

    val gameEngine: GameEngine?
        get() = mRenderer!!.gameEngine

    init {
        setEGLContextClientVersion(2)
        setEGLConfigChooser(false)
        holder.setFormat(PixelFormat.RGBA_8888)
        mRenderer = createInstance()
        setRenderer(mRenderer)
        renderMode = RENDERMODE_CONTINUOUSLY
    }
}
package com.bapplications.maplemobile.opengl

import android.util.Log
import android.opengl.Matrix
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import com.bapplications.maplemobile.constatns.Loaded
import com.bapplications.maplemobile.gameplay.GameEngine
import com.bapplications.maplemobile.gameplay.audio.Sound
import com.bapplications.maplemobile.constatns.Configuration
import com.bapplications.maplemobile.gameplay.player.look.Char
import com.bapplications.maplemobile.gameplay.map.map_objects.MapPortals

class GameGLRenderer private constructor() : GLSurfaceView.Renderer {
    val gameEngine: GameEngine? = GameEngine.instance
    private var fpsTime = System.currentTimeMillis()
    private var frames = 0
    private var before: Long = 0
    private var lag: Double = 0.0
    var start = System.currentTimeMillis()

    override fun onSurfaceCreated(gl10: GL10, eglConfig: EGLConfig) {
        Log.d(TAG, "surface CREATED")
        glClearColor(0.09019f, 0.10588f, 0.13333f, 0.0f)
        glEnable(GL_BLEND)
        glBlendEquation(GL_FUNC_ADD)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
        glEnable(GL_DEPTH_TEST)
        GLState.initGL()
        Log.d("RENDERER", "OnSurfaceCreated")
    }

    override fun onSurfaceChanged(gl10: GL10, width: Int, height: Int) {
        Log.d(TAG, "surface changed")

        // Adjust the viewport
        glViewport(0, 0, width, height)
        Loaded.SCREEN_HEIGHT = height
        Loaded.SCREEN_WIDTH = width
        Loaded.SCREEN_RATIO = width.toFloat() / height
        gameEngine!!.camera.setCameraSize()
        GLState.setSpriteSquareRes()
        Matrix.orthoM(GLState._projectionMatrix, 0, -width / 2f, width / 2f, -height / 2f, height / 2f, 0f, 1f)

        // Set the camera position (View matrix)
        Matrix.setLookAtM(GLState._viewMatrix, 0, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1.0f, 0.0f)
        start = System.currentTimeMillis()
        // Calculate the projection and view transformation
        Matrix.multiplyMM(GLState._MVPMatrix, 0, GLState._projectionMatrix, 0, GLState._viewMatrix, 0)
        Char.init()
        Sound.init()
        MapPortals.init()
        startGame()
    }

    private fun startGame() {
        gameEngine!!.startGame()
        gameEngine.loadPlayer()
        val diff = System.currentTimeMillis() - start
        Log.d(TAG, "diff init: $diff")
    }

    override fun onDrawFrame(gl10: GL10) {
        // Draw background color
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        val now = System.currentTimeMillis()
        val elapsed = now - before.toDouble()
        before = now
        lag += elapsed
        gameEngine!!.update(Configuration.MS_PER_UPDATE.toInt())
//        while (lag >= Configuration.MS_PER_UPDATE) {
//            gameEngine!!.update(Configuration.MS_PER_UPDATE.toInt())
//            Log.d("onDrawFrame", "lag = $lag")
//            lag -= Configuration.MS_PER_UPDATE
//        }
//        Log.d("onDrawFrame", "draw, lag = $lag")
        //        engine.update((int) (now - before));
        // 1000 / 6 to  make it look like its 60fps all the time.. may look slow if the fps is slower than that

//        engine.update(Configuration.MS_PER_FRAME);
        gameEngine.drawFrame()
        before = now
        if (now - fpsTime >= 1000) {
            Log.d(TAG, String.format("fps: %d", frames))
            frames = 1
            fpsTime = now
        } else {
            frames++
        }
    }

    companion object {
        private const val TAG = "GameGLRenderer"
        var instance: GameGLRenderer? = null
            private set

        @JvmStatic
        fun createInstance(): GameGLRenderer? {
            instance = GameGLRenderer()
            return instance
        }
    }

}
package com.bapplications.maplemobile.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bapplications.maplemobile.R
import com.bapplications.maplemobile.opengl.GameGLSurfaceView

class GameFragment : Fragment() {
    private lateinit var gameGLSurfaceView: GameGLSurfaceView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var inflated =  inflater.inflate(R.layout.fragment_game, container, false)
        gameGLSurfaceView = inflated.findViewById(R.id.game_view)
        return inflated
    }

    fun setUIManager(uiManager: GameActivityUIManager) {
        gameGLSurfaceView.gameEngine?.registerListener(uiManager)
    }

    fun getGameEngine() = gameGLSurfaceView.gameEngine

    fun runOnGLThread(run: Runnable) = gameGLSurfaceView.queueEvent(run);


    override fun onPause() {
        super.onPause()
        if(gameGLSurfaceView.mRenderer != null){
            gameGLSurfaceView.onPause()
        }
    }

    override fun onResume() {
        super.onResume()
        if(gameGLSurfaceView.mRenderer != null){
            gameGLSurfaceView.onResume()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = GameFragment()
    }

    interface runOnGLThread {
        fun runOnGLThread(run: Runnable)
    }
}
package com.bapplications.maplemobile.views;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bapplications.maplemobile.R;
import com.bapplications.maplemobile.constatns.Configuration;
import com.bapplications.maplemobile.constatns.Loaded;
import com.bapplications.maplemobile.databinding.ActivityGameBinding;
import com.bapplications.maplemobile.game.GameEngine;
import com.bapplications.maplemobile.opengl.GameGLSurfaceView;

import java.io.IOException;

import androidx.appcompat.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity {

    private static final String TAG = "GameActivity";
    private RelativeLayout _root;

    private GameGLSurfaceView gameGLSurfaceView;
    private GameEngine engine;

    @Override
    protected void onCreate (Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        Configuration.WZ_DIRECTORY = getExternalFilesDir(null).getAbsolutePath();
        try {
            Loaded.loadFile("Map", Configuration.WZ_DIRECTORY + "/Map.nx");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "Loading files complete");


        ActivityGameBinding binding = ActivityGameBinding.inflate(getLayoutInflater());

        _root = binding.getRoot();
        _root.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        ViewTreeObserver vto = _root.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                _root.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        setContentView(_root);

        gameGLSurfaceView = findViewById(R.id.game_view);
        binding.setMap.setOnClickListener(view -> {
            ChangeMapPopup popUpClass = new ChangeMapPopup();
            popUpClass.showPopupWindow(view);
            popUpClass.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gameGLSurfaceView.queueEvent(() -> gameGLSurfaceView.getGameEngine().changeMap(popUpClass.getMapId()));
                    popUpClass.dismiss();
                }
            });
        });
    }
;
    @Override
    protected void onResume ()
    {
        super.onResume();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        toggleFullscreen(true);
    }

    @Override
    protected void onPause ()
    {

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        super.onPause();
    }

    @Override
    protected void onDestroy ()
    {

        gameGLSurfaceView.exitGame();
        super.onDestroy();
    }


    private void toggleFullscreen (boolean fullScreen)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            if (fullScreen)
            {
                getWindow().getDecorView()
                        .setSystemUiVisibility(
                                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            }
            else
            {
                getWindow().getDecorView()
                        .setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            }
        }
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        toggleFullscreen(true);
    }
}
package com.bapplications.maplemobile.views;

import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.bapplications.maplemobile.R;
import com.bapplications.maplemobile.constatns.Configuration;
import com.bapplications.maplemobile.constatns.Loaded;
import com.bapplications.maplemobile.databinding.ActivityGameBinding;
import com.bapplications.maplemobile.gameplay.GameEngine;
import com.bapplications.maplemobile.gameplay.audio.Music;
import com.bapplications.maplemobile.opengl.GameGLSurfaceView;
import com.bapplications.maplemobile.opengl.utils.DrawableCircle;

import java.io.IOException;

import androidx.appcompat.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity {

    private static final String TAG = "GameActivity";
    private RelativeLayout root;

    private GameGLSurfaceView gameGLSurfaceView;
    private GameActivityUIControllers controllers;

    @Override
    protected void onCreate (Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        Configuration.WZ_DIRECTORY = getExternalFilesDir(null).getAbsolutePath();
        Configuration.CACHE_DIRECTORY = getCacheDir().getAbsolutePath();
        try {
            Loaded.loadFile("Map", Configuration.WZ_DIRECTORY + "/Map.nx");
            Loaded.loadFile("Mob", Configuration.WZ_DIRECTORY + "/Mob.nx");
            Loaded.loadFile("Sound", Configuration.WZ_DIRECTORY + "/Sound.nx");
            Loaded.loadFile("String", Configuration.WZ_DIRECTORY + "/String.nx");
            Loaded.loadFile("Character", Configuration.WZ_DIRECTORY + "/Character.nx");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "Loading files complete");


        ActivityGameBinding binding = ActivityGameBinding.inflate(getLayoutInflater());

        binding.setLifecycleOwner(this);
        root = binding.rootLayout;
        root.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        ViewTreeObserver vto = root.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                root.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        setContentView(root);
        DrawableCircle.init(BitmapFactory.decodeResource(getResources(),
                        R.drawable.red_circle));

        controllers = new GameActivityUIControllers(this, binding);
        gameGLSurfaceView = findViewById(R.id.game_view);
        gameGLSurfaceView.getRenderer().registerListener(controllers);
        getGameEngine().getCurrMap().getCamera().setTextView(findViewById(R.id.camera_pos_tv), this);
        getGameEngine().setControllers(controllers);
        binding.setMap.setOnClickListener(view -> {
            ChangeMapPopup popUpClass = new ChangeMapPopup();
            popUpClass.showPopupWindow(view);
            popUpClass.setOnClickListener(v -> {
                gameGLSurfaceView.queueEvent(() -> gameGLSurfaceView.getGameEngine().changeMap(popUpClass.getMapId(), "sp"));
                popUpClass.dismiss();
            });
        });
    }


    public GameEngine getGameEngine() {
        return gameGLSurfaceView.getGameEngine();
    }

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
        Music.pauseBgm();
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
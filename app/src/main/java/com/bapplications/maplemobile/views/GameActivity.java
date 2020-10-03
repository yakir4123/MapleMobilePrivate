package com.bapplications.maplemobile.views;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;

import com.bapplications.maplemobile.R;
import com.bapplications.maplemobile.constatns.Configuration;
import com.bapplications.maplemobile.constatns.Loaded;
import com.bapplications.maplemobile.databinding.ActivityGameBinding;
import com.bapplications.maplemobile.gameplay.GameEngine;
import com.bapplications.maplemobile.gameplay.audio.Music;
import com.bapplications.maplemobile.opengl.utils.DrawableCircle;
import com.bapplications.maplemobile.views.popup.ChangeMapPopup;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

public class GameActivity extends AppCompatActivity {

    private static final String TAG = "GameActivity";
    private ConstraintLayout root;

    private GameActivityUIManager uiManager;
    private GameFragment gameFragment;

    @Override
    protected void onCreate (Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        Configuration.WZ_DIRECTORY = getExternalFilesDir(null).getAbsolutePath();
        Configuration.CACHE_DIRECTORY = getCacheDir().getAbsolutePath();
        try {
            Loaded.loadFile(Loaded.WzFileName.MAP, Configuration.WZ_DIRECTORY + "/Map.nx");
            Loaded.loadFile(Loaded.WzFileName.MOB, Configuration.WZ_DIRECTORY + "/Mob.nx");
            Loaded.loadFile(Loaded.WzFileName.SOUND, Configuration.WZ_DIRECTORY + "/Sound.nx");
            Loaded.loadFile(Loaded.WzFileName.STRING, Configuration.WZ_DIRECTORY + "/String.nx");
            Loaded.loadFile(Loaded.WzFileName.CHARACTER, Configuration.WZ_DIRECTORY + "/Character.nx");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "Loading files complete");

        ActivityGameBinding binding = ActivityGameBinding.inflate(getLayoutInflater());

        uiManager = new GameActivityUIManager(this, binding);

        // attach biniding is async so I need to check whether uiManger initialized before or after gameFragment
        if(gameFragment != null) {
            gameFragment.setUIManager(uiManager);
        }
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


        binding.setMap.setOnClickListener(view -> {
            ChangeMapPopup popUpClass = new ChangeMapPopup();
            popUpClass.showPopupWindow(view);
            popUpClass.setOnClickListener(v -> {

                // change map
                popUpClass.dismiss();
            });
        });
    }


    public GameEngine getGameEngine() {
        return gameFragment.getGameEngine();
    }

    @Override
    protected void onResume ()
    {
        super.onResume();
//        gameGLSurfaceView.onResume();
        uiManager.setGameActivity(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        toggleFullscreen(true);

    }

    @Override
    protected void onPause ()
    {
//        gameGLSurfaceView.onPause();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Music.pauseBgm();
        uiManager.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy ()
    {
//        gameGLSurfaceView.exitGame();
        super.onDestroy();
    }


    private void toggleFullscreen (boolean fullScreen)
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

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        toggleFullscreen(true);
    }


    @Override
    public void onAttachFragment(@NotNull Fragment fragment) {
        if (fragment instanceof GameFragment) {
            gameFragment = (GameFragment) fragment;
            // attach fragment is async so I need to check whether uiManger initialized before or after gameFragment
            if(uiManager != null) {
                gameFragment.setUIManager(uiManager);
            }
        }
    }

}
package com.bapplications.maplemobile.activities;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bapplications.maplemobile.constatns.Configuration;
import com.bapplications.maplemobile.constatns.Loaded;
import com.bapplications.maplemobile.databinding.ActivityGameBinding;

import java.io.IOException;

import androidx.appcompat.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity {

    private static final String TAG = "GameActivity";
    private ActivityGameBinding binding;
    private RelativeLayout _root;

    @Override
    protected void onCreate (Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Configuration.WZ_DIRECTORY = getExternalFilesDir(null).getAbsolutePath();
        try {
            Loaded.loadFile("Map", Configuration.WZ_DIRECTORY + "/Map.nx");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "Loading files complete");
        binding = ActivityGameBinding.inflate(getLayoutInflater());

        _root = binding.getRoot();

        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();

        Toast.makeText(this, "EGL V = " + configurationInfo.getGlEsVersion(), Toast.LENGTH_LONG).show();

        ViewTreeObserver vto = _root.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    _root.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    _root.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }

                Loaded.SCREEN_WIDTH  = _root.getMeasuredWidth();
                Loaded.SCREEN_HEIGHT = _root.getMeasuredHeight();

            }
        });
        setContentView(_root);
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
}
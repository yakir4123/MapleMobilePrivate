package com.bapplications.maplemobile.views;

import android.annotation.SuppressLint;
import android.util.Log;

import android.view.MotionEvent;
import android.view.View;

public class GameViewController {

    private KeyAction key;
    private boolean isPressed = false;

    @SuppressLint("ClickableViewAccessibility")
    public GameViewController(KeyAction key, View view, UIControllers uiControllers) {
        this.key = key;
        switch (key.getType()) {
            case CONTINUES_CLICK:
                view.setOnTouchListener((view1, motionEvent) -> {
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        isPressed = true;
                    } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        isPressed = false;
                    }
                    return true;
                });
                break;
            case SINGLE_CLICK:
                view.setOnClickListener(view12 -> uiControllers.onClick(key));
                break;
        }
    }

    public boolean isPressed() {
        return isPressed;
    }
}

package com.bapplications.maplemobile.views;

import android.annotation.SuppressLint;

import android.view.MotionEvent;
import android.view.View;

public class GameViewButton {

    private KeyAction key;
    private boolean isPressed = false;

    @SuppressLint("ClickableViewAccessibility")
    public GameViewButton(KeyAction key, View button, GameActivityUIControllers gameActivityUiControllers) {
        this.key = key;
        switch (key.getType()) {
            case CONTINUES_CLICK:
                button.setOnTouchListener((view1, motionEvent) -> {
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        isPressed = true;
                    } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        isPressed = false;
                    }
                    return true;
                });
                break;
            case SINGLE_CLICK:
                button.setOnClickListener(view12 -> gameActivityUiControllers.onClick(key));
                break;
        }
    }

    public boolean isPressed() {
        return isPressed;
    }
}

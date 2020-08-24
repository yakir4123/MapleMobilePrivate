package com.bapplications.maplemobile.views;

import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UIControllers {

    private HashMap<KeyAction, GameViewController> controllers = new HashMap<>();
    private List<UIKeyListener> listeners = new ArrayList<>();

    public void put(KeyAction key, View view) {
        if(controllers.containsKey(key)){
            throw new IllegalArgumentException("Controllers already has this key");
        }

        controllers.put(key, new GameViewController(key, view, this));
    }

    public boolean isPressed(KeyAction key) {
        return controllers.get(key).isPressed();
    }

    public void registerListener(UIKeyListener listener) {
        listeners.add(listener);
    }

    public void onClick(KeyAction key) {
        for(UIKeyListener listener: listeners){
            listener.onAction(key);
        }
    }

    public interface UIKeyListener {
        void onAction(KeyAction key);
    }
}

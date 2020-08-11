package com.bapplications.maplemobile.opengl.utils;

import com.bapplications.maplemobile.StaticUtils;

public class Linear {

    private float now;
    private float before;

    public Linear(){
        now = 0;
        before = 0;
    }

    public void set(float value){
        before = now;
        now = value;
    }

    public float get() {
        return now;
    }

    public float get(float alpha)
    {
        return StaticUtils.lerp(before, now, alpha);
    }
}

package com.bapplications.maplemobile.opengl.utils;

public class Color {

    private int color;
    public Color(float R, float G, float B, float A){
        color = android.graphics.Color.argb(A, R, G, B);
    }

    public int getEnc(){
        return color;
    }
    public int alpha() {
        return (color >> 24) & 0xff;
    }
}

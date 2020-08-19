package com.bapplications.maplemobile.opengl.utils;

public class Color {

    private final float a;
    private final float r;
    private final float g;
    private final float b;
    private int color;
    public Color(float R, float G, float B, float A){
        color = android.graphics.Color.argb(A, R, G, B);
        r = R;
        g = G;
        b = B;
        a = A;
    }

    public int getEnc(){
        return color;
    }
    public int alpha() {
        return (color >> 24) & 0xff;
    }

    public Color mul(Color o) {
        return new Color(r * o.r, g * o.g, b * o.b, a * o.a);
    }
}

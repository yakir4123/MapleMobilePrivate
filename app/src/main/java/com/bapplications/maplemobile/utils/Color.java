package com.bapplications.maplemobile.utils;

public class Color {

    public static final int RED = android.graphics.Color.argb(255, 255, 0, 0);
    public static final int GREEN = android.graphics.Color.argb(255, 0, 255, 0);
    public static final int BLUE = android.graphics.Color.argb(255, 0, 0, 255);
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

    public static int alpha(int color) {
        return android.graphics.Color.alpha(color);
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

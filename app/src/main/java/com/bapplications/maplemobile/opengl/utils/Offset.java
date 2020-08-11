package com.bapplications.maplemobile.opengl.utils;

public class Offset {

    public int left;
    public int right;
    public int top;
    public int bottom;

    public Offset(int x, int y, int width, int height){
        left = x;
        right = x + width;
        top = y;
        bottom = y + height;
    }

    public Offset() {
        left = 0;
        right = 0;
        top = 0;
        bottom = 0;
    }
}
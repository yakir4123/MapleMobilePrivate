package com.bapplications.maplemobile.opengl.utils;

public class Leftover {
    public int left;
    public int right;
    public int top;
    public int bottom;

    Leftover()
    {
        left = 0;
        right = 0;
        top = 0;
        bottom = 0;
    }

    public Leftover(int x, int y, int width, int height) {
        left = x;
        right = x + width;
        top = y;
        bottom = y + height;
    }

    public short width()
    {
        return (short) (right - left);
    }

    public short height()
    {
        return (short) (bottom - top);
    }
}
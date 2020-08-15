package com.bapplications.maplemobile.opengl.utils;

public class Nominal {
    int now;
    int before;
    float threshold;


    public Nominal(){}

    public void set(int value) {
        now = value;
        before = value;
    }
    public int get(float alpha)
    {
        return alpha >= threshold ? now : before;
    }
}

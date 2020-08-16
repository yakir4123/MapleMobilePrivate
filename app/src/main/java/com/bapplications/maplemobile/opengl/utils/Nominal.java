package com.bapplications.maplemobile.opengl.utils;

public class Nominal {
    int now;
    int before;
    float threshold;


    public Nominal(){}

    public boolean equals(int value){
        return now == value;
    }

    public void set(int value) {
        now = value;
        before = value;
    }

    public int get() {
        return now;
    }

    public int get(float alpha)
    {
        return alpha >= threshold ? now : before;
    }

    public int plus(int value) {
        return now + value;

    }

    public void next(int value, float thrs)
    {
        before = now;
        now = value;
        threshold = thrs;
    }

    public void normalize() {
        before = now;
    }
}

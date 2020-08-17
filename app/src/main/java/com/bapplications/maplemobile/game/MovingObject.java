package com.bapplications.maplemobile.game;

import com.bapplications.maplemobile.opengl.utils.Linear;

public class MovingObject {

    public int hspeed;
    public int vspeed;
    private Linear x;
    private Linear y;

    public MovingObject() {
        x = new Linear();
        y = new Linear();
    }

    public void set_x(int x) {
        this.x.set(x);
    }

    public void set_y(int y) {
        this.y.set(y);
    }


    public boolean hmobile() {
        return hspeed != 0;
    }

    public short getAbsoluteX(float viewx, float alpha) {
        double interx = x.normalized() ? Math.round(x.get()) : x.get(alpha);

        return (short)(Math.round(interx + viewx));
    }

    public boolean vmobile() {
        return vspeed != 0.0;
    }

    public double getAbsoluteY(float viewy, float alpha) {
        double interx = y.normalized() ? Math.round(y.get()) : y.get(alpha);

        return (short)(Math.round(interx + viewy));
    }

    public void move() {
        x.plus(hspeed);
        y.plus(vspeed);
    }
}

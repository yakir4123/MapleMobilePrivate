package com.bapplications.maplemobile.gameplay.map;

import com.bapplications.maplemobile.opengl.utils.Linear;
import com.bapplications.maplemobile.opengl.utils.Point;

public class MovingObject {

    public float hspeed;
    public float vspeed;
    public Linear x;
    public Linear y;

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

    public short getAbsoluteY(float viewy, float alpha) {
        double interx = y.normalized() ? Math.round(y.get()) : y.get(alpha);

        return (short)(Math.round(interx + viewy));
    }

    public Point getAbsolute(Point viewpos, float alpha) {
        return new Point(getAbsoluteX(viewpos.x, alpha), getAbsoluteY(viewpos.y, alpha));
    }

    public boolean vmobile() {
        return vspeed != 0.0;
    }

    public void move() {
        x.set(x.plus(hspeed));
        y.set(y.minus(vspeed));
    }

}

package com.bapplications.maplemobile.opengl.utils;

import com.bapplications.maplemobile.constatns.Loaded;

public class Point{

    public float x;
    public float y;
    public Point(Point p) {
        x = p.x;
        y = p.y;
    }

    public Point(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Point() {
        this(0, 0);
    }

    public Point plus(Point p){
        return new Point(x + p.x, y + p.y);
    }

    public Point offsetThisY(float y){
        this.y += y;
        return this;
    }

    public Point offsetThisX(float x){
        this.x += x;
        return this;
    }

    public Point minus(Point p) {
        return new Point(x - p.x, y - p.y);
    }

    public float[] toGLRatio() {
        return new float[]{x / Loaded.SCREEN_WIDTH , y / Loaded.SCREEN_HEIGHT};
    }

    public Point negateSign() {
        return scalarMul(-1);
    }

    public Point scalarMul(float a){
        return new Point(x*a, y*a);
    }

    public void offset(float x, float y){
        this.x += x;
        this.y += y;

    }
}

package com.bapplications.maplemobile.opengl.utils;

import com.bapplications.maplemobile.constatns.Loaded;

public class Point{

    public float x;
    public float y;
    public Point(Object o) {
        if ( o instanceof Point) {
            Point p = (Point)o;
            x = p.x;
            y = p.y;
        } else {
            throw new IllegalArgumentException("Got Object that is not a Point");
        }
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

    public Point minus(Point p) {
        return new Point(x - p.x, y - p.y);
    }

    public Point offsetThisY(float y){
        this.y += y;
        return this;
    }

    public Point offsetThisX(float x){
        this.x += x;
        return this;
    }

    public float[] toGLRatio() {
        return new float[]{2 * x / Loaded.SCREEN_WIDTH, 2 * y / Loaded.SCREEN_HEIGHT};
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

    public Point mul(Point o) {
        return new Point(this.x * o.x, this.y * o.y);
    }

    public boolean equals(Point p) {
        return x == p.x && y == p.y;
    }
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

}

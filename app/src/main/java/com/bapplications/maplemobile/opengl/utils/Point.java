package com.bapplications.maplemobile.opengl.utils;

public class Point extends android.graphics.Point {

    public Point(android.graphics.Point p) {
        super(p);
    }

    public Point(int x, int y) {
        super(x, y);
    }

    public Point plus(android.graphics.Point p){
        return new Point(x + p.x, y + p.y);
    }

    public Point minus(Point p) {
        return new Point(x - p.x, y - p.y);
    }
}

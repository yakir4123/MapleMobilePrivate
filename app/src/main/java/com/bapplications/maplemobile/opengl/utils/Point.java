package com.bapplications.maplemobile.opengl.utils;

import com.bapplications.maplemobile.constatns.Loaded;
import com.bapplications.maplemobile.opengl.GLState;
import com.bapplications.maplemobile.pkgnx.NXNode;
import com.bapplications.maplemobile.pkgnx.nodes.NXLongNode;
import com.bapplications.maplemobile.pkgnx.nodes.NXPointNode;

public class Point{

    public float x;
    public float y;
    public Point(Object o) {
        if ( o instanceof Point) {
            Point p = (Point)o;
            x = p.x;
            y = p.y;
        } else {
            if(o != null)
                throw new IllegalArgumentException("Got Object that is not a Point");
            x = 0;
            y = 0;
        }
    }

    public Point(NXNode src){
        if( src instanceof NXPointNode){
            x = ((Point)src.get()).x;
            y = ((Point)src.get()).y;
            return;
        }
        if (src.isNull()) {
            x = 0;
            y = 0;
            return;
        }
        if(src.isChildExist("origin")){
            Point o = src.getChild("origin").get(new Point());
            x = o.x;
            y = o.y;
            return;
        }
        if(src.isChildExist("x") && src.isChildExist("y")){
            x = src.getChild("x").get(0L).floatValue();
            y = src.getChild("y").get(0L).floatValue();
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
        return new float[]{
                2 * GLState.SCALE * x / Loaded.SCREEN_WIDTH,
                2 * GLState.SCALE * y / Loaded.SCREEN_HEIGHT};
    }

    public Point negateSign() {
        return scalarMul(-1);
    }

    public Point scalarMul(float a){
        return new Point(x*a, y*a);
    }

    public void offset(Point p){
        if (p != null)
            offset(p.x, p.y);
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

    public Point flipY() {
        y *= -1;
        return this;
    }


    public Point flipX() {
        x *= -1;
        return this;
    }
}

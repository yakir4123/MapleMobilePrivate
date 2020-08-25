package com.bapplications.maplemobile.opengl.utils;

import android.util.Range;

import com.bapplications.maplemobile.pkgnx.NXNode;
import com.bapplications.maplemobile.pkgnx.nodes.NXPointNode;

public class Rectangle {

    private Point left_top;
    private Point right_bottom;

    public Rectangle(NXPointNode sourceLeftTop, NXPointNode sourceRightBottom) {
        left_top = new Point(sourceLeftTop.getPoint());
        right_bottom = new Point(sourceRightBottom.getPoint());
    }

    public Rectangle(NXNode source){
        left_top = new Point(source.getChild("lt").get(new Point()));
        right_bottom = new Point(source.getChild("rb").get(new Point()));
    }

    public Rectangle(Point leftTop, Point rightBottom) {
        left_top = new Point(leftTop);
        right_bottom = new Point(rightBottom);
    }
    public Rectangle(float left, float right, float top, float bottom){
        left_top = new Point(left, top);
        right_bottom = new Point(right, bottom);
    }
    public Rectangle() {}

    public float width()
    {
        return Math.abs(left() - right());
    }

    public float height()
    {
        return Math.abs(top() - bottom());
    }

    public float left() {
        return left_top.x;
    }

    public float top()
    {
        return left_top.y;
    }

    public float right()
    {
        return right_bottom.x;
    }

    public float bottom()
    {
        return right_bottom.y;
    }

    public boolean contains(Point p)
    {
        return !straight() &&
                        p.x >= left() && p.x <= right() &&
                        p.y <= top() && p.y >= bottom();
    }

    public boolean overlaps(Rectangle ar)
    {
        try {
            get_horizontal().intersect(new Range<Float>(ar.left(), ar.right()));
            get_vertical().intersect(new Range<Float>(ar.top(), ar.bottom()));
            return true;
        } catch (IllegalArgumentException e){
            return false;
        }

    }

    public boolean straight()
    {
        return left_top == right_bottom;
    }

    public boolean empty()
    {
        return left_top.x == left_top.y && right_bottom.x == right_bottom.y && straight();
    }

    public Point get_left_top()
    {
        return left_top;
    }

    public Point get_right_bottom()
    {
        return right_bottom;
    }

    public Range<Float> get_horizontal()
    {
        return new Range<Float>( left(), right() );
    }

    public Range<Float> get_vertical()
    {
        return new Range<Float>(top(), bottom());
    }

    public void shift(Point v)
    {
        left_top.offset(v.x, v.y);
        right_bottom.offset(v.x, v.y);
    }

}
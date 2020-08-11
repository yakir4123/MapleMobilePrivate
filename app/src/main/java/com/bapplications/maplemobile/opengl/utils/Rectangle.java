package com.bapplications.maplemobile.opengl.utils;

import android.util.Range;

import com.bapplications.maplemobile.pkgnx.NXNode;
import com.bapplications.maplemobile.pkgnx.nodes.NXPointNode;

public class Rectangle{

    private Point left_top;
    private Point right_bottom;
    public Rectangle(NXPointNode sourceLeftTop, NXPointNode sourceRightBottom) {
        left_top = new Point(sourceLeftTop.getPoint());
        right_bottom = new Point(sourceRightBottom.getPoint());
    }
    public Rectangle(NXNode source){
        left_top = new Point(((NXPointNode)source.getChild("lt")).getPoint());
        right_bottom = new Point(((NXPointNode)source.getChild("rb")).getPoint());
    }

    public Rectangle(Point leftTop, Point rightBottom) {
        left_top = new Point(leftTop);
        right_bottom = new Point(rightBottom);
    }
    public Rectangle(int left, int right, int top, int bottom){
        left_top = new Point(left, top);
        right_bottom = new Point(right, bottom);
    }
    public Rectangle() {}

    public int width()
    {
        return Math.abs(left() - right());
    }

    public int height() 
    {
        return Math.abs(top() - bottom());
    }

    public int left() {
        return left_top.x;
    }

    public int top() 
    {
        return left_top.y;
    }

    public int right() 
    {
        return right_bottom.x;
    }

    public int bottom()
    {
        return right_bottom.y;
    }

    public boolean contains(Point p)
    {
        return !straight() &&
                        p.x >= left() && p.x <= right() &&
                        p.y >= top() && p.y <= bottom();
    }

    public boolean overlaps(Rectangle ar)
    {
        try {
            get_horizontal().intersect(new Range<Integer>(ar.left(), ar.right()));
            get_vertical().intersect(new Range<Integer>(ar.top(), ar.bottom()));
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

    public Range<Integer> get_horizontal()
    {
        return new Range<Integer>( left(), right() );
    }

    public Range<Integer> get_vertical()
    {
        return new Range<Integer>(top(), bottom());
    }

    public void shift(Point v)
    {
        left_top.offset(v.x, v.y);
        right_bottom.offset(v.x, v.y);
    }

}
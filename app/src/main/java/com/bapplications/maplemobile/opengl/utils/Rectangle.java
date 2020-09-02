package com.bapplications.maplemobile.opengl.utils;

import com.bapplications.maplemobile.constatns.Configuration;

import com.bapplications.maplemobile.pkgnx.NXNode;
import com.bapplications.maplemobile.pkgnx.nodes.NXPointNode;

public class Rectangle {

    private Point left_top;
    private Point right_bottom;

    public Rectangle(NXPointNode sourceLeftTop, NXPointNode sourceRightBottom) {
        left_top = new Point(sourceLeftTop.getPoint());
        right_bottom = new Point(sourceRightBottom.getPoint());

        left_top.flipY();
        right_bottom.flipY();

    }

    public Rectangle(NXNode source){
        left_top = new Point(source.getChild("lt"));
        right_bottom = new Point(source.getChild("rb"));
        left_top.flipY();
        right_bottom.flipY();
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

    public Rectangle(Rectangle rect) {
        left_top = new Point(rect.left_top);
        right_bottom = new Point(rect.right_bottom);
    }

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
            return get_horizontal().intersect(new Range<Float>(ar.left(), ar.right()))
                    && get_vertical().intersect(new Range<Float>(ar.top(), ar.bottom()));
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

    public Point get_right_top()
    {
        return new Point(right_bottom.x, left_top.y);
    }

    public Point get_left_bottom()
    {
        return new Point(left_top.x, right_bottom.y);
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

    public void draw(Point pos) {
        DrawArgument args = new DrawArgument(pos);
        DrawableCircle[] points = new DrawableCircle[4];
        points[0] = DrawableCircle.createCircle(get_left_top(), Color.GREEN);
        points[1] = DrawableCircle.createCircle(get_right_top(), Color.GREEN);
        points[2] = DrawableCircle.createCircle(get_left_bottom(), Color.GREEN);
        points[3] = DrawableCircle.createCircle(get_right_bottom(), Color.GREEN);
        for(DrawableCircle p: points) {
            p.draw(args);
        }
    }

}
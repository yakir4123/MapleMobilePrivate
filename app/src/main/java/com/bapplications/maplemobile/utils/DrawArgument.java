package com.bapplications.maplemobile.utils;


public class DrawArgument {

    Point pos;
    Point center;
    float opacity;
    float xscale;
    float yscale;
    float angle;

    public DrawArgument() { this(0, 0); }
    public DrawArgument(int x, int y) { this(new Point(x, y)); }
    public DrawArgument(Point position) { this(position, position, 1.0f, 1.0f, 1.0f, 0.0f); }
    public DrawArgument(Point position, boolean flip, float opacity) { this(position, position, flip ? -1.0f : 1.0f, 1.0f, opacity); }
    public DrawArgument(Point position, float opacity) { this(position, false, opacity); }
    public DrawArgument(Point position, float xscale, float yscale) { this(position, position, xscale, yscale, 1.0f); }
    public DrawArgument(Point position, Point stretch) { this(position, position, 1.0f, 1.0f, 1.0f, 0.0f); }
    public DrawArgument(Point position, boolean flip) { this(position, flip, 1.0f); }
    public DrawArgument(float angle, Point position, float opacity) { this(angle, position, false, opacity); }
    public DrawArgument(Point position, boolean flip, Point center) { this(position, center, flip ? -1.0f : 1.0f, 1.0f, 1.0f); }
    public DrawArgument(Point position, Point center, float xscale, float yscale, float opacity) { this(position, center, xscale, yscale, opacity, 0.0f); }
    public DrawArgument(boolean flip) { this(flip ? -1.0f : 1.0f, 1.0f, 1.0f); }
    public DrawArgument(float xscale, float yscale, float opacity) { this(new Point(0, 0), xscale, yscale, opacity); }
    public DrawArgument(Point position, float xscale, float yscale, float opacity) { this(position, position, xscale, yscale, opacity); }
    public DrawArgument(float angle, Point position, boolean flip, float opacity) { this(position, position, flip ? -1.0f : 1.0f, 1.0f, opacity, angle); }
    public DrawArgument(Point position, Point center, float xscale, float yscale, float opacity, float angle) {
        this.pos = position;
        this.center = center;
        this.xscale = xscale;
        this.yscale = yscale;
        this.opacity = opacity;
        this.angle = angle;
    }

    public float getAngle() {
        return angle;
    }

    public Point getPos() {
        return pos;
    }

    public Point getCenter() {
        return center;
    }

    public DrawArgument plus(Point pos) {
        return new DrawArgument(this.pos.plus(pos), center, xscale, yscale, opacity, angle);
    }

    public DrawArgument offsetPosition(Point pos) {
        return offsetPosition(pos.x, pos.y);
    }

    public DrawArgument offsetPosition(float x, float y) {
        this.pos.offset(x, y);
        return this;
    }

    public DrawArgument minusPosition(Point pos) {
        this.pos.offset(-pos.x, -pos.y);
        return this;
    }

    public DrawArgument plus(DrawArgument o) {
        return new DrawArgument(this.pos.plus(o.pos),
                this.center.plus(o.center),
                xscale * o.xscale,
                yscale * o.yscale,
                angle + o.angle);
    }

    public byte getDirection() {
        return (byte) (xscale > 0 ? 1 : -1);
    }

    public void setDirection(boolean lookLeft) {
        xscale = lookLeft ? xscale : -xscale;
    }

    public float getXScale() {
        return xscale;
    }

}

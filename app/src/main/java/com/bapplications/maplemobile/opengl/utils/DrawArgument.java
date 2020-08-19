package com.bapplications.maplemobile.opengl.utils;


public class DrawArgument {
    Point pos;
    Point center;
    Point stretch;
    float xscale;
    float yscale;
    float angle;
    Color color;

    public DrawArgument() { this(0, 0); }
    public DrawArgument(int x, int y) { this(new Point(x, y)); }
    public DrawArgument(Point position) { this(position, 1.0f); }
    public DrawArgument(Point position, boolean flip, float opacity) { this(position, position, flip ? -1.0f : 1.0f, 1.0f, opacity); }
    public DrawArgument(Point position, float opacity) { this(position, false, opacity); }
    public DrawArgument(Point position, float xscale, float yscale) { this(position, position, xscale, yscale, 1.0f); }
    public DrawArgument(Point position, Point stretch) { this(position, position, stretch, 1.0f, 1.0f, 1.0f, 0.0f); }
    public DrawArgument(Point position, boolean flip) { this(position, flip, 1.0f); }
    public DrawArgument(float angle, Point position, float opacity) { this(angle, position, false, opacity); }
    public DrawArgument(Point position, Color color) { this(position, position, new Point(0, 0), 1.0f, 1.0f, color, 0.0f); }
    public DrawArgument(Point position, boolean flip, Point center) { this(position, center, flip ? -1.0f : 1.0f, 1.0f, 1.0f); }
    public DrawArgument(Point position, Point center, float xscale, float yscale, float opacity) { this(position, center, new Point(0, 0), xscale, yscale, opacity, 0.0f); }
    public DrawArgument(boolean flip) { this(flip ? -1.0f : 1.0f, 1.0f, 1.0f); }
    public DrawArgument(float xscale, float yscale, float opacity) { this(new Point(0, 0), xscale, yscale, opacity); }
    public DrawArgument(Point position, float xscale, float yscale, float opacity) { this(position, position, xscale, yscale, opacity); }
    public DrawArgument(float angle, Point position, boolean flip, float opacity) { this(position, position, new Point(0, 0), flip ? -1.0f : 1.0f, 1.0f, opacity, angle); }
    public DrawArgument(Point position, Point center, Point stretch, float xscale, float yscale, float opacity, float angle) {
        this.pos = position;
        this.center = center;
        this.stretch = stretch;
        this.xscale = xscale;
        this.yscale = yscale;
        this.color = new Color(1.0f, 1.0f, 1.0f, opacity);
        this.angle = angle;
    }

    public DrawArgument(Point position, Point center, Point stretch, float xscale, float yscale, Color color, float angle) {
        this.pos = position;
        this.center = center;
        this.stretch = stretch;
        this.xscale = xscale;
        this.yscale = yscale;
        this.color = color;
        this.angle = angle;
    }


    public Color getColor() {
        return color;
    }

    public float getAngle() {
        return angle;
    }

    public Point getPos() {
        return pos;
    }

    public DrawArgument plus(Point pos) {
        return new DrawArgument(this.pos.plus(pos), center, stretch, xscale, yscale, color, angle);
    }

    public DrawArgument plus(DrawArgument o) {
        return new DrawArgument(this.pos.plus(o.pos),
                this.center.plus(o.center),
                stretch.plus(stretch),
                xscale * o.xscale,
                yscale * o.yscale,
                color.mul(o.color),
                angle + o.angle);
    }
}

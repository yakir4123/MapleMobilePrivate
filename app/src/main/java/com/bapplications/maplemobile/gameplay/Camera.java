package com.bapplications.maplemobile.gameplay;

import android.app.Activity;
import android.util.Range;
import android.widget.TextView;

import com.bapplications.maplemobile.opengl.utils.Point;


public class Camera {

    private Point pos;

    // View limits.
    Range<Short> hbounds;
    Range<Short> vbounds;
    private TextView textView;
    private Activity activity;

    public Camera()
    {
        pos = new Point();
    }

    void set_view(Range<Short> mapwalls, Range<Short> mapborders)
    {
        hbounds = new Range<Short>((short)(-mapwalls.getUpper()),
                (short)(-mapwalls.getLower()));
        vbounds = new Range<Short>((short)(-mapborders.getUpper()),
                (short)(-mapborders.getLower()));
    }

    public Point position(float alpha)
    {
        return pos;
    }


    public void offsetPosition(float dx, float dy) {
        pos.x += dx;
        pos.y += dy;
        textView.setText(pos.toString());
    }

    public void setPosition(Point p) {
        pos.x = p.x;
        pos.y = p.y;
        setText();
    }

    public Point position() {
        return position(1f);
    }


    // todo remove it after i have portals, If i decide to use this view I need to add viewModel
    public void setTextView(TextView textView, Activity activity) {
        this.textView = textView;
        this.activity = activity;
    }

    private void setText() {
        activity.runOnUiThread(() -> {
            textView.setText(pos.toString());
        });
    }
}

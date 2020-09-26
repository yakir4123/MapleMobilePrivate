package com.bapplications.maplemobile.gameplay;

import android.app.Activity;

import com.bapplications.maplemobile.opengl.GLState;
import com.bapplications.maplemobile.opengl.utils.Range;
import android.widget.TextView;

import com.bapplications.maplemobile.constatns.Loaded;
import com.bapplications.maplemobile.opengl.utils.Point;

import static com.bapplications.maplemobile.constatns.Configuration.OFFSETX;


public class Camera {

    private Point pos;
    private static final int CAMERA_X_OFFSET = 120;
    private static final int CAMERA_Y_OFFSET = 30;
    // View limits.
    Range<Short> hbounds;
    Range<Short> vbounds;
    private TextView textView;
    private Activity activity;

    public Camera()
    {
        pos = new Point();
    }

    void setView(Range<Short> mapwalls, Range<Short> mapborders)
    {
        hbounds = new Range<>((short)(mapwalls.getLower()),
                (short)(mapwalls.getUpper()));
        vbounds = new Range<>((short)(-mapborders.getUpper()),
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

    public void update(Point position) {
        int HWidth = (int) (Loaded.SCREEN_WIDTH / 2 / GLState.SCALE);
        int HHeight = Loaded.SCREEN_HEIGHT/8;


        pos.x = -position.x;
        pos.y = -(position.y + HHeight);
        if (pos.x > -(hbounds.getLower() + HWidth - OFFSETX)){
            pos.x = -(hbounds.getLower() + HWidth - OFFSETX);
        }

        if (pos.x < -(hbounds.getUpper() - HWidth + OFFSETX)){
            pos.x = -(hbounds.getUpper() - HWidth + OFFSETX);
        }
//        else if (position.x > hbounds.getUpper() - HWidth + 4 * CAMERA_X_OFFSET){
//            pos.x = -(hbounds.getUpper() - HWidth + 4 * CAMERA_X_OFFSET);
//        }

        setText();
    }
}

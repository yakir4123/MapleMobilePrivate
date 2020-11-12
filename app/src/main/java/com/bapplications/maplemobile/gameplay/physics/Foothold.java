package com.bapplications.maplemobile.gameplay.physics;

import com.bapplications.maplemobile.utils.Color;
import com.bapplications.maplemobile.pkgnx.NXNode;
import com.bapplications.maplemobile.utils.Range;
import com.bapplications.maplemobile.utils.Point;
import com.bapplications.maplemobile.utils.DrawableCircle;
import com.bapplications.maplemobile.constants.Configuration;
import com.bapplications.maplemobile.utils.DrawArgument;

public class Foothold {
    private DrawableCircle c1;
    private DrawableCircle c2;
    private short m_prev;
    private short m_next;
    private byte m_layer;
    private final short m_id;
    private Range m_vertical;
    private Range m_horizontal;


    public Foothold(NXNode src, int id, int layer){
        m_prev = src.getChild("prev").get(0L).shortValue();
        m_next = src.getChild("next").get(0L).shortValue();
        m_horizontal = new Range(src.getChild("x1").get(0L).shortValue(),
                src.getChild("x2").get(0L).shortValue());

        try {
            m_vertical = new Range(
                    -src.getChild("y1").get(0L).shortValue(),
                    -src.getChild("y2").get(0L).shortValue()
            );
        } catch (IllegalArgumentException e){
            m_vertical = new Range(
                    -src.getChild("y2").get(0L).shortValue(),
                    -src.getChild("y1").get(0L).shortValue()
            );
        }
        m_id = (short) id;
        m_layer = (byte) layer;

        if(Configuration.SHOW_FH){
            c1 = DrawableCircle.createCircle(new Point(m_horizontal.getLower(), m_vertical.getLower()), Color.RED);
            c2 = DrawableCircle.createCircle(new Point(m_horizontal.getUpper(), m_vertical.getUpper()), Color.RED);
        }
    }

    public Foothold() {
        m_id = 0;
        m_layer = 0;
        m_next = 0;
        m_prev = 0;
        m_horizontal = new Range(0, 0);
        m_vertical = new Range(0, 0);
    }


    public float l() {
        return m_horizontal.getLower();
    }

    public float r() {
        return m_horizontal.getUpper();
    }

    public float t() {
        return m_vertical.getLower();
    }
    
    public float b() {
        return m_vertical.getUpper();
    }

    public boolean isWall() {
        return m_id != 0 && m_horizontal.getUpper() == m_horizontal.getLower();
    }

    public short next() {
        return m_next;
    }

    public short prev() {
        return m_prev;
    }

    public float groundBelow(float x) {
        return isFloor() ? y1() : slope() * (x - x1()) + y1();
    }

    public float slope() {
        return isWall() ? 0.0f : (vdelta()) / hdelta();
    }

    private float hdelta() {
        return x2() - x1();
    }

    private float vdelta() {
        return y2() - y1();
    }

    public float y1() {
        return m_vertical.getLower();
    }

    public float y2() {
        return m_vertical.getUpper();
    }

    public float x1() {
        return m_horizontal.getLower();
    }

    public float x2() {
        return m_horizontal.getUpper();
    }

    private boolean isFloor() {
        return m_id != 0 && m_vertical.getUpper() == m_vertical.getLower();
    }

    public short id() {
        return m_id;
    }

    public byte getLayer() {
        return m_layer;
    }

    public boolean isBlocking(Range vertical) {
        return m_vertical.intersect(vertical) && isWall();
    }

    public void draw(Point viewpos){
        if(c1 == null || c2 == null)
            return;
        c1.draw(new DrawArgument(viewpos));
        c2.draw(new DrawArgument(viewpos));
    }
}

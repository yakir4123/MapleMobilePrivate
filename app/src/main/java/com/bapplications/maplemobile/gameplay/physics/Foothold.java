package com.bapplications.maplemobile.gameplay.physics;

import android.util.Range;

import com.bapplications.maplemobile.constatns.Configuration;
import com.bapplications.maplemobile.opengl.utils.DrawArgument;
import com.bapplications.maplemobile.opengl.utils.RedCircle;
import com.bapplications.maplemobile.opengl.utils.Point;
import com.bapplications.maplemobile.pkgnx.NXNode;

public class Foothold {
    private RedCircle c1;
    private RedCircle c2;
    private Range<Short> m_horizontal;
    private Range<Short> m_vertical;
    private final short m_id;
    private short m_prev;
    private short m_next;
    private byte m_layer;


    public Foothold(NXNode src, int id, int layer){
        m_prev = ((Long)(src.getChild("prev").get())).shortValue();
        m_next = ((Long)(src.getChild("next").get())).shortValue();
        m_horizontal = new Range<Short>(((Long)src.getChild("x1").get()).shortValue(),
                ((Long)src.getChild("x2").get()).shortValue());

        try {
            m_vertical = new Range<Short>(
                    (short) -((Long) src.getChild("y1").get()).shortValue(),
                    (short) -((Long) src.getChild("y2").get()).shortValue()
            );
        } catch (IllegalArgumentException e){
            m_vertical = new Range<Short>(
                    (short) -((Long) src.getChild("y2").get()).shortValue(),
                    (short) -((Long) src.getChild("y1").get()).shortValue()
            );
        }
        m_id = (short) id;
        m_layer = (byte) layer;

        if(Configuration.DEBUG){
            c1 = new RedCircle(new Point(m_horizontal.getLower(), m_vertical.getLower()));
            c2 = new RedCircle(new Point(m_horizontal.getUpper(), m_vertical.getUpper()));
        }
    }

    public Foothold() {
        m_id = 0;
        m_layer = 0;
        m_next = 0;
        m_prev = 0;
        m_horizontal = new Range<>((short)0, (short)0);
        m_vertical = new Range<>((short)0, (short)0);
    }


    public short l() {
        return m_horizontal.getLower();
    }

    public short r() {
        return m_horizontal.getUpper();
    }

    public short t() {
        return m_vertical.getLower();
    }
    
    public short b() {
        return m_vertical.getUpper();
    }

    public boolean is_wall() {
        return m_id != 0 && m_horizontal.getUpper().equals(m_horizontal.getLower());
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
        return is_wall() ? 0.0f : (vdelta()) / hdelta();
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
        return m_id != 0 && m_vertical.getUpper().equals(m_vertical.getLower());
    }

    public short id() {
        return m_id;
    }

    public byte getLayer() {
        return m_layer;
    }

    public boolean isBlocking(Range<Short> vertical) {
        try{
            m_vertical.intersect(vertical);
            return is_wall();
        } catch (IllegalArgumentException e){}
        return false;
    }

    public void draw(Point viewpos){
        if(c1 == null || c2 == null)
            return;
        c1.draw(new DrawArgument(viewpos));
        c2.draw(new DrawArgument(viewpos));
    }
}

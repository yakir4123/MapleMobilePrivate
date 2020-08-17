package com.bapplications.maplemobile.game.physics;

import android.util.Log;
import android.util.Range;

import com.bapplications.maplemobile.pkgnx.NXNode;

public class Foothold {
    private Range<Short> m_horizontal;
    private Range<Short> m_vertical;
    private final short m_id;

//    private final short m_prev;
//    private final short m_next;

    public Foothold(NXNode src, int id, int layer){
//        m_prev = src["prev"];
//        m_next = src["next"];
        m_horizontal = new Range<Short>(((Long)src.getChild("x1").get()).shortValue(),
                ((Long)src.getChild("x2").get()).shortValue());
        //todo :: maybe need to change to -y_ and switch between them
        try {
            m_vertical = new Range<Short>(((Long) src.getChild("y1").get()).shortValue(),
                    ((Long) src.getChild("y2").get()).shortValue());
        }catch (IllegalArgumentException e){
            m_vertical = new Range<Short>(((Long) src.getChild("y2").get()).shortValue(),
                    ((Long) src.getChild("y1").get()).shortValue());
        }
        m_id = (short) id;
//        m_layer = ly;
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
}

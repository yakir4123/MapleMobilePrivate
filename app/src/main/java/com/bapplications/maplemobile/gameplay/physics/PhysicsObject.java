package com.bapplications.maplemobile.gameplay.physics;

import com.bapplications.maplemobile.gameplay.map.MovingObject;
import com.bapplications.maplemobile.opengl.utils.Point;

public class PhysicsObject extends MovingObject {

    public byte fhlayer;
    public Type type;
    public short fhid;
    public boolean onground = true;
    public float fhslope;
    public boolean enablejd = false;
    public float groundbelow = 0;
    public float vacc;
    public float hacc;
    public float vforce;
    public float hforce;
    private int flags;

    // Determines which physics engine to use
    enum Type
    {
        NORMAL,
        ICE,
        SWIMMING,
        FLYING,
        FIXATED
    };

    enum Flag
    {
        ERROR0,
        NOGRAVITY,      // 1
        TURNATEDGES,    // 2
        ERROR3,
        CHECKBELOW,     // 4

    };


    public PhysicsObject() {
        super();
        type = Type.NORMAL;
        fhlayer = 0;
    }

    public boolean isFlagSet(Flag f) {
        return (flags & f.ordinal()) != 0;
    }

    public void clearFlag(Flag f) {
        flags &= ~f.ordinal();
    }

    public void limitX(float d) {
        x.set(d);
        hspeed = 0;
    }

    public boolean isFlagNotSet(Flag f) {
        return !isFlagSet(f);
    }

    public float nextX() {
        return x.plus(hspeed).get();
    }

    public float nextY() {
        return y.plus(vspeed).get();
    }

    public void limitY(float d) {
        y.set(d);
        vspeed = 0;
    }
    public Point get_position() {
        return new Point(getX(), getY());
    }

    private short getX() {
        return (short)Math.round(x.get());
    }

    private short getY() {
        return (short)Math.round(y.get());
    }

    public float crntX() 
    {
        return x.get();
    }

    public float crntY() 
    {
        return y.get();
    }
}

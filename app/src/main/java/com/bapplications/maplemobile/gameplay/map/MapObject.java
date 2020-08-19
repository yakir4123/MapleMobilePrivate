package com.bapplications.maplemobile.gameplay.map;

import com.bapplications.maplemobile.gameplay.physics.Physics;
import com.bapplications.maplemobile.gameplay.physics.PhysicsObject;
import com.bapplications.maplemobile.opengl.utils.Point;

public class MapObject {

    protected PhysicsObject phobj;
    protected int oid;
    protected boolean active;


    protected MapObject(int id){
        this(id, new Point());
    }
    protected MapObject(int id, Point pos)
    {
        oid = id;
        set_position(pos);
        active = true;
    }

    byte update(Physics physics)
    {
        physics.moveObject(phobj);

        return phobj.fhlayer;
    }

    void set_position(float x, float y)
    {
        phobj.set_x((int) x);
        phobj.set_y((int) y);
    }

    void set_position(Point position)
    {
        float x = position.x;
        float y = position.y;
        set_position(x, y);
    }

    void makeactive()
    {
        active = true;
    }

    void deactivate()
    {
        active = false;
    }

    boolean is_active()
    {
        return active;
    }

    byte get_layer()
    {
        return phobj.fhlayer;
    }

    int get_oid()
    {
        return oid;
    }

    Point get_position()
    {
        return phobj.get_position();
    }
}

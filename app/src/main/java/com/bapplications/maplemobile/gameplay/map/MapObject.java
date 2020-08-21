package com.bapplications.maplemobile.gameplay.map;

import com.bapplications.maplemobile.gameplay.physics.Physics;
import com.bapplications.maplemobile.gameplay.physics.PhysicsObject;
import com.bapplications.maplemobile.opengl.utils.Point;

public class MapObject {

    protected int oid;
    protected boolean active;
    protected PhysicsObject phobj;


    protected MapObject(int id){
        this(id, new Point());
    }
    protected MapObject(int id, Point pos)
    {
        oid = id;
        phobj = new PhysicsObject();
        setPosition(pos);
        active = true;
    }

    byte update(Physics physics)
    {
        physics.moveObject(phobj);

        return phobj.fhlayer;
    }

    public void setPosition(float x, float y)
    {
        phobj.set_x((int) x);
        phobj.set_y((int) y);
    }

    void setPosition(Point position)
    {
        setPosition(position.x, position.y);
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

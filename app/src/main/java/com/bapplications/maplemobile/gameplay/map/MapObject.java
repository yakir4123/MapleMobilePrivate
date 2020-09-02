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

    public byte update(Physics physics, int deltatime)
    {
        physics.moveObject(phobj);

        return phobj.fhlayer;
    }

    public void setPosition(float x, float y)
    {
        phobj.set_x((int) x);
        phobj.set_y((int) y);
    }

    protected void setPosition(Point position)
    {
        setPosition(position.x, position.y);
    }

    protected void makeActive()
    {
        active = true;
    }

    protected void deActivate()
    {
        active = false;
    }

    byte getLayer()
    {
        return phobj.fhlayer;
    }

    int getOid()
    {
        return oid;
    }

    protected Point getPosition()
    {
        return phobj.getPosition();
    }

    public PhysicsObject getPhobj() {
        return phobj;
    }

    public boolean isActive() {
        return active;
    }

    public void draw(Point view, float alpha) {

    }
}

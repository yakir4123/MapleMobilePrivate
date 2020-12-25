package com.bapplications.maplemobile.gameplay.physics;

import com.bapplications.maplemobile.constatns.Configuration;
import com.bapplications.maplemobile.utils.Point;
import com.bapplications.maplemobile.pkgnx.NXNode;

public class Physics {
    
    
	public static final double GRAVFORCE = 0.35;
	public static final double SWIMGRAVFORCE = 0.03;
	public static final double FRICTION = 0.2;
	public static final double SLOPEFACTOR = 0.1;
	public static final double GROUNDSLIP = 3.0;
	public static final double FLYFRICTION = 0.05;
	public static final double SWIMFRICTION = 0.08;
	
    private FootholdTree footholdtree;
    
    public Physics(NXNode fhnode) {
        footholdtree = new FootholdTree(fhnode);
    }

    public FootholdTree getFHT() {
        return footholdtree;
    }

    public void moveObject(PhysicsObject phobj) {

        // Determine which platform the object is currently on
        footholdtree.updateFH(phobj);

        // Use the appropriate physics for the terrain the object is on
        switch (phobj.type)
        {
            case NORMAL:
                moveNormal(phobj);
                footholdtree.limitMovement(phobj);
                break;
            case FLYING:
//                move_flying(phobj);
//                fht.limit_movement(phobj);
//                break;
            case SWIMMING:
//                move_swimming(phobj);
//                fht.limit_movement(phobj);
//                break;
            case FIXATED:
            default:
                break;
        }

        // Move the object forward
        phobj.move();

    }


    private void moveNormal(PhysicsObject phobj)
    {
        phobj.vacc = 0.0f;
        phobj.hacc = 0.0f;

        if (phobj.onground)
        {
            phobj.vacc += phobj.vforce;
            phobj.hacc += phobj.hforce;

            if (phobj.hacc == 0.0 && phobj.hspeed < 0.1 && phobj.hspeed > -0.1)
            {
                phobj.hspeed = 0.0f;
            }
            else
            {
                double inertia = phobj.hspeed / GROUNDSLIP;
                double slopef = phobj.fhslope;

                if (slopef > 0.5)
                    slopef = 0.5;
                else if (slopef < -0.5)
                    slopef = -0.5;

                phobj.hacc -= (FRICTION + SLOPEFACTOR * (1.0 + slopef * -inertia)) * inertia;
            }
        }
        else if (phobj.isFlagNotSet(PhysicsObject.Flag.NOGRAVITY))
        {
            phobj.vacc += GRAVFORCE;
        }

        phobj.hforce = 0.0f;
        phobj.vforce = 0.0f;

        phobj.hspeed += phobj.hacc;
        phobj.vspeed += phobj.vacc;
    }

    public Point getYBelow(Point position) {
        float ground = footholdtree.getYBelow(position);
        return new Point(position.x, ground + 1);
    }

    public void draw(Point viewpos) {
        if(Configuration.SHOW_FH){
            footholdtree.draw(viewpos);
        }
    }
}

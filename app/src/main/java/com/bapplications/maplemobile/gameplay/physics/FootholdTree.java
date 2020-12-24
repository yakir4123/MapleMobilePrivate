package com.bapplications.maplemobile.gameplay.physics;

import com.bapplications.maplemobile.utils.Range;

import com.bapplications.maplemobile.utils.Point;
import com.bapplications.maplemobile.pkgnx.NXNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FootholdTree {

    Foothold nullfh;
    private Range walls;
    private Range borders;
    private HashMap<Short, List<Short>> footholdsbyx;
    private HashMap<Short, Foothold> footholds;

    public FootholdTree(NXNode fhnode) {

        float topb = 30000;
        float leftw = 30000;
        float botb = -30000;
        float rightw = -30000;

        nullfh = new Foothold();
        footholds = new HashMap<>();
        footholdsbyx = new HashMap<>();
        for (NXNode basef : fhnode)
        {
            int layer;

            try
            {
                layer = Integer.parseInt(basef.getName());
            }
            catch (Exception ex)
            {
                continue;
            }

            for (NXNode midf : basef)
            {
                for (NXNode lastf : midf)
                {
                    short id;

                    try
                    {
                        id = (short) Integer.parseInt(lastf.getName());
                    }
                    catch (Exception ex)
                    {
                        continue;
                    }

					Foothold foothold = new Foothold(lastf, id, layer);
                    footholds.put(id, foothold);

                    if (foothold.getLeft() < leftw)
                        leftw = foothold.getLeft();

                    if (foothold.getRight() > rightw)
                        rightw = foothold.getRight();

                    if (foothold.getBottom() > botb)
                        botb = foothold.getBottom();

                    if (foothold.getTop() < topb)
                        topb = foothold.getTop();

                    if (foothold.isWall())
                        continue;

                    float start = foothold.getLeft();
                    float end = foothold.getRight();

                    for (short i = (short) start; i <= end; i++) {
                        if(!footholdsbyx.containsKey(i)){
                            footholdsbyx.put(i, new ArrayList<>());
                        }
                        footholdsbyx.get(i).add(id);
                    }
                }
            }
        }

        walls = new Range( leftw + 25,rightw - 25 );
        borders = new Range( topb - 300, botb + 100 );
    }

    public Range getBorders() {
        return borders;
    }

    public Range getWalls() {
        return walls;
    }

    public void updateFH(PhysicsObject phobj) {
        if (phobj.type == PhysicsObject.Type.FIXATED && phobj.fhid > 0)
            return;

		Foothold curfh = getFH(phobj.fhid);
        boolean checkslope = false;

        float x = phobj.crntX();
        float y = phobj.crntY();

        if (phobj.onground)
        {
            if (Math.floor(x) > curfh.getRight())
                phobj.fhid = curfh.next();
			else if (Math.ceil(x) < curfh.getLeft())
                phobj.fhid = curfh.prev();

            if (phobj.fhid == 0)
                phobj.fhid = getFHidBelow(x, y);
            else
                checkslope = true;
        }
        else
        {
            phobj.fhid = getFHidBelow(x, y);
        }

		Foothold nextfh = getFH(phobj.fhid);
        phobj.fhslope = nextfh.slope();

        float ground = nextfh.groundBelow(x);

        if (phobj.vspeed == 0.0 && checkslope)
        {
            double vdelta = Math.abs(phobj.fhslope);

            if (phobj.fhslope != 0.0)
                vdelta *= Math.abs(ground - y);

            if (curfh.slope() != 0.0 || nextfh.slope() != 0.0)
            {
                if ((phobj.hspeed > 0.0 && vdelta <= phobj.hspeed)
                        || (phobj.hspeed < 0.0 && vdelta >= phobj.hspeed))
                    phobj.y.set(ground);
            }
        }

//        phobj.onground = (phobj.y.get() - ground) < 2*phobj.vspeed;
        phobj.onground = phobj.y.get() == ground;

        if (phobj.enablejd || phobj.isFlagSet(PhysicsObject.Flag.CHECKBELOW))
        {
            short belowid = getFHidBelow(x, nextfh.groundBelow(x) - 1.0f);

            if (belowid > 0)
            {
                double nextground = getFH(belowid).groundBelow(x);
                phobj.enablejd = (nextground - ground) < 600.0; // to check if i can jump down
                phobj.groundbelow = ground - 1.0f;
            }
            else
            {
                phobj.enablejd = false;
            }

            phobj.clearFlag(PhysicsObject.Flag.CHECKBELOW);
        }

        if (phobj.fhlayer == 0 || phobj.onground)
            phobj.fhlayer = nextfh.getLayer();

        if (phobj.fhid == 0)
        {
            phobj.fhid = curfh.id();
            phobj.limitX(curfh.getLeft());
        }
    }

    private short getFHidBelow(float fx, float fy) {
        short ret = 0;
        double comp = borders.getLower();

        for(Short id: footholdsbyx.get((short)fx)) {
			Foothold fh = footholds.get(id);
            double ycomp = fh.groundBelow(fx);

            if (comp <= ycomp && ycomp <= fy)
            {
                comp = ycomp;
                ret = fh.id();
            }
        }

        return ret;
    }

    private Foothold getFH(short fhid) {
        Foothold fh = footholds.get(fhid);
        if (fh == null) {
            return nullfh;
        }
        return fh;

    }

    public void limitMovement(PhysicsObject phobj) {
        if (phobj.hmobile())
        {
            float crnt_x = phobj.crntX();
            double next_x = phobj.nextX();

            boolean left = phobj.hspeed < 0.0f;
            float wall = getWall(phobj.fhid, left, phobj.nextY());
            boolean collision = left ? crnt_x >= wall && next_x <= wall : crnt_x <= wall && next_x >= wall;

            if (!collision && phobj.isFlagSet(PhysicsObject.Flag.TURN_AT_EDGES))
            {
                wall = getEdge(phobj.fhid, left);
                collision = left ? crnt_x >= wall && next_x <= wall : crnt_x <= wall && next_x >= wall;
            }

            if (collision)
            {
                phobj.limitX(wall);
                phobj.clearFlag(PhysicsObject.Flag.TURN_AT_EDGES);
            }
        }

        if (phobj.vmobile())
        {
            double crnt_y = phobj.crntY();
            double next_y = phobj.nextY();

            Range ground;
            try {
                ground = new Range(
                        getFH(phobj.fhid).groundBelow(phobj.nextX()),
                        getFH(phobj.fhid).groundBelow(phobj.crntX())
                );
            } catch (IllegalArgumentException e) {
                ground = new Range(
                        getFH(phobj.fhid).groundBelow(phobj.crntX()),
                        getFH(phobj.fhid).groundBelow(phobj.nextX())
                        );
            }
            boolean collision = crnt_y >= ground.getUpper()
                    && next_y <= ground.getUpper();

            if (collision)
            {
                phobj.limitY(ground.getUpper());

                limitMovement(phobj);
            }
            else
            {
                if (next_y < borders.getLower())
                    phobj.limitY(borders.getLower());
                else if (next_y > borders.getUpper())
                    phobj.limitY(borders.getUpper());
            }
        }

    }

    private float getEdge(short curid, boolean left) {
		Foothold fh = getFH(curid);

        if (left)
        {
            short previd = fh.prev();

            if (previd == 0)
                return fh.getLeft();

			Foothold prev = getFH(previd);
            short prev_previd = prev.prev();

            if (prev_previd == 0)
                return prev.getLeft();

            return walls.getLower();
        }
        else
        {
            short nextid = fh.next();

            if (nextid == 0)
                return fh.getRight();

			Foothold next = getFH(nextid);
            short next_nextid = next.next();

            if (next_nextid == 0)
                return next.getRight();

            return walls.getUpper();
        }
    }

    private float getWall(short curid, boolean left, float fy) {
        short shorty = (short) (fy);
        Range vertical = new Range(shorty + 1, shorty + 50);
		Foothold cur = getFH(curid);

        if (left) {
			Foothold prev = getFH(cur.prev());

            if (prev.isBlocking(vertical))
                return cur.getLeft();

			Foothold prev_prev = getFH(prev.prev());

            if (prev_prev.isBlocking(vertical))
                return prev.getLeft();

            return walls.getLower();
        } else {
			Foothold next = getFH(cur.next());

            if (next.isBlocking(vertical))
                return cur.getRight();

			Foothold next_next = getFH(next.next());

            if (next_next.isBlocking(vertical))
                return next.getRight();

            return walls.getUpper();
        }
    }

    public float getYBelow(Point position) {
        short fhid = getFHidBelow(position.x, position.y);
        if (fhid != 0)
        {
			Foothold fh = getFH(fhid);

            return (short) fh.groundBelow(position.x);
        }
		else
        {
            return borders.getLower();
        }
    }

    public void draw(Point viewpos) {
        for(Foothold fh: footholds.values()){
            fh.draw(viewpos);
        }
    }
}

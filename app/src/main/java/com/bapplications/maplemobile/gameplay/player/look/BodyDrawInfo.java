package com.bapplications.maplemobile.gameplay.player.look;

import com.bapplications.maplemobile.constatns.Loaded;
import com.bapplications.maplemobile.gameplay.player.Stance;
import com.bapplications.maplemobile.utils.Point;
import com.bapplications.maplemobile.pkgnx.NXNode;

import java.util.HashMap;
import java.util.List;

public class BodyDrawInfo {
    private HashMap<Byte, Point>[] arm_positions = new HashMap[Stance.Id.values().length];
    private HashMap<Byte, Short>[] stance_delays = new HashMap[Stance.Id.values().length];
    private HashMap<Byte, Point>[] body_positions = new HashMap[Stance.Id.values().length];
    private HashMap<Byte, Point>[] hand_positions = new HashMap[Stance.Id.values().length];
    private HashMap<Byte, Point>[] head_positions = new HashMap[Stance.Id.values().length];
    private HashMap<Byte, Point>[] hair_positions = new HashMap[Stance.Id.values().length];
    private HashMap<Byte, Point>[] face_positions = new HashMap[Stance.Id.values().length];

    private HashMap<String, List<Byte>> attack_delays = new HashMap<>();
    private HashMap<String, HashMap<Byte, BodyAction>> body_actions = new HashMap<>();


    public Point getHandPosition(Stance.Id stance, byte frame) {
        Point p = hand_positions[stance.ordinal()].get(frame);
        if (p == null)
            return new Point();
        return p;
    }

    public Point getArmPosition(Stance.Id stance, byte frame) {
        Point p = arm_positions[stance.ordinal()].get(frame);
        if (p == null)
            return new Point();
        return p;
    }

    public Point getBodyPosition(Stance.Id stance, byte frame) {
        Point p = body_positions[stance.ordinal()].get(frame);
        if (p == null)
            return new Point();
        return p;
    }

    public Point getHeadPosition(Stance.Id stance, byte frame) {
        Point p = head_positions[stance.ordinal()].get(frame);
        if (p == null)
            return new Point();
        return head_positions[stance.ordinal()].get(frame);
    }

    public Point getFacePos(Stance.Id stance, byte frame) {
        Point p = face_positions[stance.ordinal()].get(frame);
        if (p == null)
            return new Point();
        return face_positions[stance.ordinal()].get(frame);
    }

    public Point getHairPos(Stance.Id stance, byte frame) {
        Point p = hair_positions[stance.ordinal()].get(frame);
        if (p == null)
            return new Point();
        return hair_positions[stance.ordinal()].get(frame);
    }

    public void init() {
        NXNode bodynode = Loaded.getFile(Loaded.WzFileName.CHARACTER).getRoot().getChild("00002000.img");
        NXNode headnode = Loaded.getFile(Loaded.WzFileName.CHARACTER).getRoot().getChild("00012000.img");

        for (NXNode stancenode : bodynode)
        {
            String ststr = stancenode.getName();

            short attackdelay = 0;

            for (byte frame = 0; stancenode.isChildExist(frame); ++frame)
            {
                NXNode framenode = stancenode.getChild(frame);
                boolean isaction = framenode.isChildExist("action");

                if (isaction)
                {
//                    BodyAction action = framenode;
//                    body_actions[ststr][frame] = action;
//
//                    if (action.isattackframe())
//                        attack_delays[ststr].push_back(attackdelay);
//
//                    attackdelay += action.get_delay();
                } else {
                    Stance.Id stance = Stance.valueOf(ststr);
                    short delay = framenode.getChild("delay").get(0L).shortValue();
                    if (delay <= 0)
                        delay = 100;

                    if(stance_delays[stance.ordinal()] == null){
                        stance_delays[stance.ordinal()] = new HashMap<>();
                    }
                    stance_delays[stance.ordinal()].put(frame, delay);

                    HashMap<Body.Layer, HashMap<String, Point>> bodyshiftmap = new HashMap<>();

                    for (NXNode partnode : framenode)
                    {
                        String part = partnode.getName();

                        if (!part.equals("delay") && !part.equals("face"))
                        {
                            String zstr = (String) partnode.getChild("z").get("");
                            Body.Layer z = Body.layerByName.get(zstr);

                            for (NXNode mapnode : partnode.getChild("map")) {
                                if(bodyshiftmap.get(z) == null)
                                    bodyshiftmap.put(z, new HashMap<>());

                                bodyshiftmap.get(z).put(mapnode.getName(), new Point(mapnode));
                            }
                        }
                    }

                    NXNode headmap = headnode.getChild(ststr).getChild(frame)
                                .getChild("head").getChild("map");
                    if(!headmap.isNotExist()) {
                        for (NXNode mapnode : headmap) {
                            if (bodyshiftmap.get(Body.Layer.HEAD) == null)
                                bodyshiftmap.put(Body.Layer.HEAD, new HashMap<>());

                            bodyshiftmap.get(Body.Layer.HEAD).put(mapnode.getName(), new Point(mapnode));
                        }
                    }

                    if(body_positions[stance.ordinal()] == null){
                        arm_positions[stance.ordinal()] = new HashMap<>();
                        body_positions[stance.ordinal()] = new HashMap<>();
                        hand_positions[stance.ordinal()] = new HashMap<>();
                        face_positions[stance.ordinal()] = new HashMap<>();
                        hair_positions[stance.ordinal()] = new HashMap<>();
                        head_positions[stance.ordinal()] = new HashMap<>();

                    }
                    Point navel = bodyshiftmap.get(Body.Layer.BODY).get("navel");
                    navel = navel == null ? new Point(): navel;
                    body_positions[stance.ordinal()].put(frame, navel);

                    try {
                        arm_positions[stance.ordinal()].put(frame, bodyshiftmap.containsKey(Body.Layer.ARM) ?
                            (bodyshiftmap.get(Body.Layer.ARM).get("hand")
                                    .minus(bodyshiftmap.get(Body.Layer.ARM).get("navel"))
                                    .plus(bodyshiftmap.get(Body.Layer.BODY).get("navel"))) :
                            (bodyshiftmap.get(Body.Layer.ARM_OVER_HAIR).get("hand").minus(bodyshiftmap.get(Body.Layer.ARM_OVER_HAIR).get("navel")).plus(bodyshiftmap.get(Body.Layer.BODY).get("navel"))));
                    } catch (NullPointerException e){}
                    try {
                        hand_positions[stance.ordinal()].put(frame, bodyshiftmap.get(Body.Layer.HAND_BELOW_WEAPON).get("handMove"));
                    } catch (NullPointerException e){}
                    try {
                        head_positions[stance.ordinal()].put(frame, bodyshiftmap.get(Body.Layer.BODY).get("neck").minus(bodyshiftmap.get(Body.Layer.HEAD).get("neck")));
                    } catch (NullPointerException e){}
                    try {
                        face_positions[stance.ordinal()].put(frame, head_positions[stance.ordinal()].get(frame)
                                .plus(bodyshiftmap.get(Body.Layer.HEAD).get("brow"))
                                .mul(new Point(1, -1)));

//                        face_positions[stance.ordinal()].put(frame, head_positions[stance.ordinal()].get(frame)
//                                .plus(bodyshiftmap.get(Body.Layer.HEAD).get("brow")));
//                                .mul(new Point(1, -1)));
                    } catch (NullPointerException e) {}
                    try {
                        hair_positions[stance.ordinal()].put(frame, bodyshiftmap.get(Body.Layer.HEAD).get("brow").minus(bodyshiftmap.get(Body.Layer.HEAD).get("neck")).plus(bodyshiftmap.get(Body.Layer.BODY).get("neck")));
                    } catch (NullPointerException e) {}
                }
            }
        }
    }

    public short getDelay(Stance.Id stance, Byte frame) {
        Short delay = stance_delays[stance.ordinal()].get(frame);
        if (delay == null)
            return 100;
        return delay;
    }

    public byte nextFrame(Stance.Id stance, byte frame) {
        if (stance_delays[stance.ordinal()].containsKey((byte)(1 + frame)))
            return (byte) (frame + 1);
        else
            return 0;
    }
}

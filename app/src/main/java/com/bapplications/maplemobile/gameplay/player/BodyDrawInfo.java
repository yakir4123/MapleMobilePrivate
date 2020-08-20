package com.bapplications.maplemobile.gameplay.player;

import com.bapplications.maplemobile.constatns.Loaded;
import com.bapplications.maplemobile.opengl.utils.Point;
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
    public static boolean bodyDrawInfoInitialized = false;


    public Point getHandPosition(Stance.Id stance, byte frame) {
        return hand_positions[stance.ordinal()].get(frame);
    }

    public Point getBodyPosition(Stance.Id stance, byte frame) {
        return body_positions[stance.ordinal()].get(frame);
    }

    public Point getHeadPosition(Stance.Id stance, byte frame) {
        return head_positions[stance.ordinal()].get(frame);
    }

    public void init() {
        NXNode bodynode = Loaded.getFile("Character").getRoot().getChild("00002000.img");
        NXNode headnode = Loaded.getFile("Character").getRoot().getChild("00012000.img");

        for (NXNode stancenode : bodynode)
        {
            String ststr = stancenode.getName();

            short attackdelay = 0;

            for (byte frame = 0; stancenode.getChild(frame) != null; ++frame)
            {
                NXNode framenode = stancenode.getChild(frame);
                boolean isaction = false;
                try {
                    isaction = framenode.getChild("action").get() instanceof String;
                } catch (NullPointerException ignored){ }

                if (isaction)
                {
//                    BodyAction action = framenode;
//                    body_actions[ststr][frame] = action;
//
//                    if (action.isattackframe())
//                        attack_delays[ststr].push_back(attackdelay);
//
//                    attackdelay += action.get_delay();
                }
                else
                {
                    Stance.Id stance = Stance.valueOf(ststr);
                    short delay = 0;
                    try {
                        delay = ((Long) (framenode.getChild("delay").get())).shortValue();
                    } catch (NullPointerException e) { }
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
                            String zstr = (String) partnode.getChild("z").get();
                            Body.Layer z = Body.layerByName.get(zstr);

                            for (NXNode mapnode : partnode.getChild("map")) {
                                if(bodyshiftmap.get(z) == null)
                                    bodyshiftmap.put(z, new HashMap<>());
                                bodyshiftmap.get(z).put(mapnode.getName(), new Point(mapnode));
                            }
                        }
                    }

                    NXNode headmap = null;
                    try {
                        headmap = headnode.getChild(ststr).getChild(frame)
                                .getChild("head").getChild("map");
                    } catch (NullPointerException e) {}

                    if(headmap != null) {
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
                    body_positions[stance.ordinal()].put(frame, navel == null ? new Point(): navel);

                    try {
                        arm_positions[stance.ordinal()].put(frame, bodyshiftmap.containsKey(Body.Layer.ARM) ?
                            (bodyshiftmap.get(Body.Layer.ARM).get("hand").minus(bodyshiftmap.get(Body.Layer.ARM).get("navel")).plus(bodyshiftmap.get(Body.Layer.BODY).get("navel"))) :
                            (bodyshiftmap.get(Body.Layer.ARM_OVER_HAIR).get("hand").minus(bodyshiftmap.get(Body.Layer.ARM_OVER_HAIR).get("navel")).plus(bodyshiftmap.get(Body.Layer.BODY).get("navel"))));
                    } catch (NullPointerException e){}
                    try {
                        hand_positions[stance.ordinal()].put(frame, bodyshiftmap.get(Body.Layer.HAND_BELOW_WEAPON).get("handMove"));
                    } catch (NullPointerException e){}
                    try {
                        head_positions[stance.ordinal()].put(frame, bodyshiftmap.get(Body.Layer.BODY).get("neck").minus(bodyshiftmap.get(Body.Layer.HEAD).get("neck")));
                    } catch (NullPointerException e){}
                    try {
                        face_positions[stance.ordinal()].put(frame, bodyshiftmap.get(Body.Layer.BODY).get("neck").minus(bodyshiftmap.get(Body.Layer.HEAD).get("neck")).plus(bodyshiftmap.get(Body.Layer.HEAD).get("brow")));
                    } catch (NullPointerException e) {}
                    try {
                        hair_positions[stance.ordinal()].put(frame, bodyshiftmap.get(Body.Layer.HEAD).get("brow").minus(bodyshiftmap.get(Body.Layer.HEAD).get("neck")).plus(bodyshiftmap.get(Body.Layer.BODY).get("neck")));
                    } catch (NullPointerException e) {}
                }
            }
        }
        bodyDrawInfoInitialized = true;
    }
}

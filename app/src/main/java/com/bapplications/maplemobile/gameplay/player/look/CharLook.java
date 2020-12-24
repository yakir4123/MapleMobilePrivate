package com.bapplications.maplemobile.gameplay.player.look;

import com.bapplications.maplemobile.gameplay.player.CharEntry;
import com.bapplications.maplemobile.gameplay.player.inventory.EquipSlot;
import com.bapplications.maplemobile.gameplay.player.Stance;
import com.bapplications.maplemobile.utils.DrawArgument;
import com.bapplications.maplemobile.utils.Nominal;
import com.bapplications.maplemobile.utils.Point;
import com.bapplications.maplemobile.utils.TimedBool;

import java.util.HashMap;
import java.util.Map;

public class CharLook {
    private Body body;
    private Face face;
    private Hair hair;
    private int exptime;
    private byte actframe;
    private short stelapsed;
    private String actionstr;
    private short expelapsed;
    private CharEquips equips;
    private BodyAction action;
    private Nominal<Byte> stframe;
    private TimedBool expcooldown;
    private Nominal<Byte> expframe;
    private Nominal<Stance.Id> stance;
    private boolean lookLeft = true;
    private static BodyDrawInfo drawInfo;
    private Nominal<Expression> expression;
    private TimedBool alerted = new TimedBool();
    private static Map<Integer, Face> faceTypes;
    private static Map<Integer, Hair> hairStyles;
    private static HashMap<Integer, Body> bodyTypes;

    public static void init() {
        drawInfo = new BodyDrawInfo();
        drawInfo.init();
        bodyTypes = new HashMap<>();
        faceTypes = new HashMap<>();
        hairStyles = new HashMap<>();
    }

    public CharLook(CharEntry.LookEntry entry) {
        stance = new Nominal<>();
        stframe = new Nominal<>();
        equips = new CharEquips();
        expframe = new Nominal<>();
        expression = new Nominal<>();
        expcooldown = new TimedBool();
        reset();

        setBody(entry.skin);
        setHair(entry.hairid);
        setFace(entry.faceid);

        for (Integer equip : entry.equips.values())
            addEquip(equip);

    }

    private void reset() {
        lookLeft = true;

        action = null;
        actionstr = "";
        actframe = 0;

        stframe.set((byte) 0);
        stelapsed = 0;

        setExpression(Expression.DEFAULT);
        expframe.set((byte) 0);
        expelapsed = 0;
    }


    private void setFace(int faceid) {
        if (!faceTypes.containsKey(faceid)){
            faceTypes.put(faceid, new Face(faceid));
        }
        face = faceTypes.get(faceid);
    }

    private void setBody(int skin_id) {
        if (!bodyTypes.containsKey(skin_id)){
            bodyTypes.put(skin_id, new Body(skin_id, drawInfo));
        }
        body = bodyTypes.get(skin_id);
    }

    public void setHair(int hair_id){
        if (!hairStyles.containsKey(hair_id)){
            hairStyles.put(hair_id, new Hair(hair_id, drawInfo));
        }
        hair = hairStyles.get(hair_id);
    }

    public void setExpression(Expression newexpression) {
        if (expression.get() != newexpression && !expcooldown.isTrue())
        {
            expression.set(newexpression);
            expframe.set((byte) 0);

            expelapsed = 0;
            exptime = 0;
//            expcooldown.set_for(5000);
        }
    }

    public void setStance(Stance.Id newstance) {
        if (action != null)
            return;

//        Stance adjstance = equips.adjust_stance(newstance);
//
//        if (stance != adjstance)
//        {
//            stance.set(adjstance);
            stance.set(newstance);
            stframe.set((byte) 0);
            stelapsed = 0;
//        }
    }

    public void addEquip(Integer itemid) {
        equips.addEquip(itemid, drawInfo);
//        updatetwohanded();
    }

    public void removeEquip(Integer itemid) {
        equips.removeEquip(itemid, drawInfo);
        //        updatetwohanded();
    }

    public void draw(DrawArgument args, float alpha) {
        if(body == null || hair == null || face == null)
            return;

        Point acmove = new Point();

//        if (action != null)
//            acmove = action.get_move();

        DrawArgument relargs = new DrawArgument( acmove, !lookLeft);
        Stance.Id interstance = stance.get(alpha);
        byte interframe = stframe.get(alpha);

        Expression interexpression = expression.get(alpha);
        byte interexpframe = expframe.get(alpha);

        switch (interstance)
        {
            case STAND1:
            case STAND2:
            {
                if (alerted.isTrue())
                    interstance = Stance.Id.ALERT;

                break;
            }
        }
        draw(relargs.plus(args), interstance, interexpression, interframe, interexpframe);
    }
    
    private void draw(DrawArgument args,
                      Stance.Id interstance,
                      Expression interexpression,
                      byte interframe,
                      byte interexpframe) {

        Point faceshift = drawInfo.getFacePos(interstance, interframe);
        face.shift(faceshift);

        if(interstance == Stance.Id.LADDER || interstance == Stance.Id.ROPE){
            climbingDraw(args, interstance, interframe);
            return;
        }
        hair.draw(interstance, Hair.Layer.BELOWBODY, interframe, args);
        equips.draw(EquipSlot.Id.CAPE, interstance, Clothing.Layer.CAPE, interframe, args);
        equips.draw(EquipSlot.Id.SHIELD, interstance, Clothing.Layer.SHIELD_BELOW_BODY, interframe, args);
        equips.draw(EquipSlot.Id.WEAPON, interstance, Clothing.Layer.WEAPON_BELOW_BODY, interframe, args);
        equips.draw(EquipSlot.Id.HAT, interstance, Clothing.Layer.CAP_BELOW_BODY, interframe, args);
        body.draw(interstance, Body.Layer.BODY, interframe, args);
        equips.draw(EquipSlot.Id.GLOVES, interstance, Clothing.Layer.WRIST_OVER_BODY, interframe, args);
        equips.draw(EquipSlot.Id.GLOVES, interstance, Clothing.Layer.GLOVE_OVER_BODY, interframe, args);
        equips.draw(EquipSlot.Id.SHOES, interstance, Clothing.Layer.SHOES, interframe, args);
        body.draw(interstance, Body.Layer.ARM_BELOW_HEAD, interframe, args);

        if (equips.hasOverall())
        {
            equips.draw(EquipSlot.Id.TOP, interstance, Clothing.Layer.MAIL, interframe, args);
        }
        else
        {
            equips.draw(EquipSlot.Id.BOTTOM, interstance, Clothing.Layer.PANTS, interframe, args);
            equips.draw(EquipSlot.Id.TOP, interstance, Clothing.Layer.TOP, interframe, args);
        }

        body.draw(interstance, Body.Layer.ARM_BELOW_HEAD_OVER_MAIL, interframe, args);
        equips.draw(EquipSlot.Id.SHIELD, interstance, Clothing.Layer.SHIELD_OVER_HAIR, interframe, args);
        equips.draw(EquipSlot.Id.EARRINGS, interstance, Clothing.Layer.EARRINGS, interframe, args);
        body.draw(interstance, Body.Layer.HEAD, interframe, args);
        hair.draw(interstance, Hair.Layer.SHADE, interframe, args);
        hair.draw(interstance, Hair.Layer.DEFAULT, interframe, args);
        face.draw(interexpression, interexpframe, args);
//        equips.draw(EquipSlot.Id.FACE, interstance, Clothing.Layer.FACEACC, 0, args);
        equips.draw(EquipSlot.Id.EYEACC, interstance, Clothing.Layer.EYEACC, interframe, args);
        equips.draw(EquipSlot.Id.SHIELD, interstance, Clothing.Layer.SHIELD, interframe, args);

        switch (equips.getCapType())
        {
            case NONE:
                hair.draw(interstance, Hair.Layer.OVERHEAD, interframe, args);
                break;
            case HEADBAND:
                equips.draw(EquipSlot.Id.HAT, interstance, Clothing.Layer.CAP, interframe, args);
                hair.draw(interstance, Hair.Layer.DEFAULT, interframe, args);
                hair.draw(interstance, Hair.Layer.OVERHEAD, interframe, args);
                equips.draw(EquipSlot.Id.HAT, interstance, Clothing.Layer.CAP_OVER_HAIR, interframe, args);
                break;
            case HALFCOVER:
                hair.draw(interstance, Hair.Layer.DEFAULT, interframe, args);
                equips.draw(EquipSlot.Id.HAT, interstance, Clothing.Layer.CAP, interframe, args);
                break;
            case FULLCOVER:
                equips.draw(EquipSlot.Id.HAT, interstance, Clothing.Layer.CAP, interframe, args);
                break;
        }

        equips.draw(EquipSlot.Id.WEAPON, interstance, Clothing.Layer.WEAPON_BELOW_ARM, interframe, args);
        boolean twohanded = isTwoHanded(interstance);
//
        if (twohanded)
        {
            equips.draw(EquipSlot.Id.TOP, interstance, Clothing.Layer.MAILARM, interframe, args);
            body.draw(interstance, Body.Layer.ARM, interframe, args);
            equips.draw(EquipSlot.Id.WEAPON, interstance, Clothing.Layer.WEAPON, interframe, args);
        }
        else
        {
            equips.draw(EquipSlot.Id.WEAPON, interstance, Clothing.Layer.WEAPON, interframe, args);
            body.draw(interstance, Body.Layer.ARM, interframe, args);
            equips.draw(EquipSlot.Id.TOP, interstance, Clothing.Layer.MAILARM, interframe, args);
        }

        equips.draw(EquipSlot.Id.GLOVES, interstance, Clothing.Layer.WRIST, interframe, args);
        equips.draw(EquipSlot.Id.GLOVES, interstance, Clothing.Layer.GLOVE, interframe, args);

        body.draw(interstance, Body.Layer.HAND_BELOW_WEAPON, interframe, args);

        body.draw(interstance, Body.Layer.ARM_OVER_HAIR, interframe, args);
        body.draw(interstance, Body.Layer.ARM_OVER_HAIR_BELOW_WEAPON, interframe, args);
        equips.draw(EquipSlot.Id.WEAPON, interstance, Clothing.Layer.WEAPON_OVER_HAND, interframe, args);
        equips.draw(EquipSlot.Id.WEAPON, interstance, Clothing.Layer.WEAPON_OVER_BODY, interframe, args);
        body.draw(interstance, Body.Layer.HAND_OVER_HAIR, interframe, args);
        body.draw(interstance, Body.Layer.HAND_OVER_WEAPON, interframe, args);

        equips.draw(EquipSlot.Id.GLOVES, interstance, Clothing.Layer.WRIST_OVER_HAIR, interframe, args);
        equips.draw(EquipSlot.Id.GLOVES, interstance, Clothing.Layer.GLOVE_OVER_HAIR, interframe, args);
        equips.draw(EquipSlot.Id.WEAPON, interstance, Clothing.Layer.WEAPON_OVER_GLOVE, interframe, args);
    }

    private boolean isTwoHanded(Stance.Id st) {
        switch (st)
        {
            case STAND1:
            case WALK1:
                return false;
            case STAND2:
            case WALK2:
                return true;
            default:
                return equips.isTwoHanded();
        }
    }

    private void climbingDraw(DrawArgument args,
                              Stance.Id interstance,
                              byte interframe) {
        body.draw(interstance, Body.Layer.BODY, interframe, args);
        equips.draw(EquipSlot.Id.GLOVES, interstance, Clothing.Layer.GLOVE, interframe, args);
        equips.draw(EquipSlot.Id.SHOES, interstance, Clothing.Layer.SHOES, interframe, args);
        equips.draw(EquipSlot.Id.BOTTOM, interstance, Clothing.Layer.PANTS, interframe, args);
        equips.draw(EquipSlot.Id.TOP, interstance, Clothing.Layer.TOP, interframe, args);
        equips.draw(EquipSlot.Id.TOP, interstance, Clothing.Layer.MAIL, interframe, args);
        equips.draw(EquipSlot.Id.CAPE, interstance, Clothing.Layer.CAPE, interframe, args);
        body.draw(interstance, Body.Layer.HEAD, interframe, args);
        equips.draw(EquipSlot.Id.EARRINGS, interstance, Clothing.Layer.EARRINGS, interframe, args);

        switch (equips.getCapType())
        {
            case NONE:
                hair.draw(interstance, Hair.Layer.BACK, interframe, args);
                break;
            case HEADBAND:
                equips.draw(EquipSlot.Id.HAT, interstance, Clothing.Layer.CAP, interframe, args);
                hair.draw(interstance, Hair.Layer.BACK, interframe, args);
                break;
            case HALFCOVER:
                hair.draw(interstance, Hair.Layer.BELOWCAP, interframe, args);
                equips.draw(EquipSlot.Id.HAT, interstance, Clothing.Layer.CAP, interframe, args);
                break;
            case FULLCOVER:
                equips.draw(EquipSlot.Id.HAT, interstance, Clothing.Layer.CAP, interframe, args);
                break;
        }

        equips.draw(EquipSlot.Id.SHIELD, interstance, Clothing.Layer.BACKSHIELD, interframe, args);
        equips.draw(EquipSlot.Id.WEAPON, interstance, Clothing.Layer.BACKWEAPON, interframe, args);

    }

    public boolean update(short timestep) {
        if (timestep == 0)
        {
            stance.normalize();
            stframe.normalize();
            expression.normalize();
            expframe.normalize();
            return false;
        }

        alerted.update(timestep);

        boolean aniend = false;
        if (action == null)
        {
            short delay = getDelay(stance.get(), stframe.get());
            short delta = (short) (delay - stelapsed);

            if (timestep >= delta)
            {
                stelapsed = (short) (timestep - delta);
                byte nextframe = getNextFrame(stance.get(), stframe.get());
                float threshold = (float)(delta) / timestep;
                stframe.next(nextframe, threshold);

                if (stframe.get() == 0)
                    aniend = true;
            }
            else
            {
                stance.normalize();
                stframe.normalize();

                stelapsed += timestep;
            }
        }
        else
        {
//            short delay = action.getDelay();
//            short delta = (short) (delay - stelapsed);
//
//            if (timestep >= delta)
//            {
//                stelapsed = (short) (timestep - delta);
//                actframe = drawInfo.nextActionFrame(actionstr, actframe);
//
//                if (actframe > 0)
//                {
//                    action = drawInfo.getAction(actionstr, actframe);
//
//                    float threshold = (float)(delta) / timestep;
//                    stance.next(action.getStance(), threshold);
//                    stframe.next(action.getFrame(), threshold);
//                }
//                else
//                {
//                    aniend = true;
//                    action = null;
//                    actionstr = "";
//                    setStance(Stance.Id.STAND1);
//                }
//            }
//            else
//            {
//                stance.normalize();
//                stframe.normalize();
//                stelapsed += timestep;
//            }
        }


        short expdelay = face.getDelay(expression.get(), expframe.get());
        short expdelta = (short) (expdelay - expelapsed);

        exptime += timestep;
        if (timestep >= expdelta)
        {
            expelapsed = (short) (timestep - expdelta);
            byte nextexpframe = face.nextFrame(expression.get(), expframe.get());
            float fcthreshold = (float)(expdelta) / timestep;
            expframe.next(nextexpframe, fcthreshold);

            if (expframe.get() == 0 && exptime > Expression.TIME)
            {
                if (expression.get() == Expression.DEFAULT)
                    expression.next(Expression.BLINK, fcthreshold);
				else
                    expression.next(Expression.DEFAULT, fcthreshold);
            }
        }
        else
        {
            expression.normalize();
            expframe.normalize();
            expelapsed += timestep;
        }
        return aniend;
    }

    private byte getNextFrame(Stance.Id stance, Byte frame) {
        return drawInfo.nextFrame(stance, frame);
    }

    private short getDelay(Stance.Id stance, Byte frame) {
        return drawInfo.getDelay(stance, frame);
    }

    public void setDirection(boolean flipped) {
        face.setDirection(lookLeft);
        lookLeft = flipped;
    }

    public void setAlerted(int millis) {
        alerted.setFor(millis);
    }

}

package com.bapplications.maplemobile.gameplay.player;

import com.bapplications.maplemobile.opengl.utils.DrawArgument;
import com.bapplications.maplemobile.opengl.utils.Linear;
import com.bapplications.maplemobile.opengl.utils.Nominal;
import com.bapplications.maplemobile.opengl.utils.Point;
import com.bapplications.maplemobile.opengl.utils.TimedBool;

import java.util.HashMap;
import java.util.function.BinaryOperator;
import java.util.regex.Matcher;

public class CharLook {
    private Body body;
    private boolean flip;
    private byte actframe;
    private short stelapsed;
    private String actionstr;
    private short expelapsed;
    private BodyAction action;
    private Nominal<Byte> stframe;
    private TimedBool expcooldown;
    private static BodyDrawInfo drawInfo;
    private Nominal<Stance.Id> stance;
    private Nominal<Byte> expframe;
    private HashMap<Byte, Body> bodyTypes;
    private Nominal<Expression> expression;

    public static void init() {
        drawInfo = new BodyDrawInfo();
        drawInfo.init();
    }

    public CharLook(CharEntry.LookEntry entry) {
        stance = new Nominal<>();
        stframe = new Nominal<>();
        expframe = new Nominal<>();
        bodyTypes = new HashMap<>();
        expression = new Nominal<>();
        expcooldown = new TimedBool();

        reset();


        setBody(entry.skin);
//        set_hair(entry.hairid);
//        set_face(entry.faceid);
//
//        for (auto& equip : entry.equips)
//            add_equip(equip.second);

    }

    private void reset() {
        flip = true;

        action = null;
        actionstr = "";
        actframe = 0;

        setStance(Stance.Id.STAND1);
        stframe.set((byte) 0);
        stelapsed = 0;

        setExpression(Expression.DEFAULT);
        expframe.set((byte) 0);
        expelapsed = 0;
    }

    private void setBody(byte skin_id) {
        if (!bodyTypes.containsKey(skin_id)){
            bodyTypes.put(skin_id, new Body(skin_id, drawInfo));
        }
        body = bodyTypes.get(skin_id);
    }

    private void setExpression(Expression newexpression) {
        if (expression.get() != newexpression && !expcooldown.isTrue())
        {
            expression.set(newexpression);
            expframe.set((byte) 0);

            expelapsed = 0;
            expcooldown.set_for(5000);
        }
    }

    private void setStance(Stance.Id newstance) {
        if (action != null || newstance == Stance.Id.NONE)
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

    public void draw(DrawArgument args, float alpha) {
//        if (!body || !hair || !face)
//            return;

        if(body == null)
            return;

        Point acmove = new Point();

//        if (action != null)
//            acmove = action.get_move();

        DrawArgument relargs = new DrawArgument( acmove, flip );
        Stance.Id interstance = stance.get(alpha);
        Expression interexpression = expression.get(alpha);
        byte interframe = stframe.get(alpha);
        byte interexpframe = expframe.get(alpha);

        switch (interstance)
        {
            case STAND1:
            case STAND2:
            {
//                if (alerted)
//                    interstance = Stance.Id.Id.ALERT;

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
//        body.draw(interstance, Body.Layer.BODY, interframe, args);
//        body.draw(interstance, Body.Layer.ARM_BELOW_HEAD, interframe, args);
//        body.draw(interstance, Body.Layer.ARM_BELOW_HEAD_OVER_MAIL, interframe, args);
        body.draw(interstance, Body.Layer.HEAD, interframe, args);

//        body.draw(interstance, Body.Layer.HAND_BELOW_WEAPON, interframe, args);
//        body.draw(interstance, Body.Layer.ARM_OVER_HAIR, interframe, args);
//        body.draw(interstance, Body.Layer.ARM_OVER_HAIR_BELOW_WEAPON, interframe, args);
//        body.draw(interstance, Body.Layer.HAND_OVER_HAIR, interframe, args);
//        body.draw(interstance, Body.Layer.HAND_OVER_WEAPON, interframe, args);

    }
}

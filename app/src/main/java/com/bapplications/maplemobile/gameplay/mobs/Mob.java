package com.bapplications.maplemobile.gameplay.mobs;

import com.bapplications.maplemobile.StaticUtils;
import com.bapplications.maplemobile.constatns.Loaded;
import com.bapplications.maplemobile.gameplay.audio.Sound;
import com.bapplications.maplemobile.gameplay.map.MapObject;
import com.bapplications.maplemobile.gameplay.physics.Physics;
import com.bapplications.maplemobile.gameplay.physics.PhysicsObject;
import com.bapplications.maplemobile.gameplay.textures.Animation;
import com.bapplications.maplemobile.opengl.utils.DrawArgument;
import com.bapplications.maplemobile.opengl.utils.Linear;
import com.bapplications.maplemobile.opengl.utils.Point;
import com.bapplications.maplemobile.opengl.utils.Randomizer;
import com.bapplications.maplemobile.opengl.utils.TimedBool;
import com.bapplications.maplemobile.pkgnx.NXNode;

import java.util.HashMap;
import java.util.Map;

public class Mob extends MapObject {


    private int stanceLength = 400 * (int)Randomizer.nextExponential();

    enum Stance
    {
        MOVE,
        STAND,
        JUMP,
        HIT,
        DIE
    };


    enum FlyDirection
    {
        STRAIGHT,
        UPWARDS,
        DOWNWARDS,
        NUM_DIRECTIONS
    };
    
    Map<Stance, Animation> animations;
    String name;
    Sound hitsound;
    Sound diesound;
    short level;
    float speed;
    float flyspeed;
    short watk;
    short matk;
    short wdef;
    short mdef;
    short accuracy;
    short avoid;
    short knockback;
    boolean undead;
    boolean touchdamage;
    boolean noflip;
    boolean notattack;
    boolean canmove;
    boolean canjump;
    boolean canfly;

//    EffectLayer effects;
//    Text namelabel;
//    MobHpBar hpbar;

    TimedBool showhp;


//    List<Movement> movements;
    short counter;

    int id;
    byte effect;
    byte team;
    boolean dying;
    boolean dead;
    boolean control;
    boolean aggro;
    Stance stance;
    boolean flip;
    FlyDirection flydirection;
    float walkforce;
    byte hppercent;
    boolean fading;
    boolean fadein;
    Linear opacity;

    public Mob(int oi, int mid, byte mode, byte st, short fh, boolean newspawn, byte tm, Point position)
    {
        super(oi);
        opacity = new Linear();
        animations = new HashMap<>();

        String strid = StaticUtils.extendId(mid, 7);
        NXNode src = Loaded.getFile("Mob").getRoot().getChild(strid + ".img");

        NXNode info = src.getChild("info");

        level = info.getChild("level").get(0L).shortValue();
        watk = info.getChild("PADamage").get(0L).shortValue();
        matk = info.getChild("MADamage").get(0L).shortValue();
        wdef = info.getChild("PDDamage").get(0L).shortValue();
        mdef = info.getChild("MDDamage").get(0L).shortValue();
        accuracy = info.getChild("acc").get(0L).shortValue();
        avoid = info.getChild("eva").get(0L).shortValue();
        knockback = info.getChild("pushed").get(0L).shortValue();
        speed = info.getChild("speed").get(0L).floatValue();
        flyspeed = info.getChild("flySpeed").get(0L).floatValue();
        touchdamage = info.getChild("bodyAttack").get(0L) > 0;
        undead = info.getChild("undead").get(0L) > 0;
        noflip = info.getChild("noFlip").get(0L) >  0;
        notattack = info.getChild("notAttack").get(0L) > 0;
        canjump = src.isChildExist("jump");
        canfly = src.isChildExist("fly");
        canmove = src.isChildExist("move") || canfly;

        if (canfly)
        {
            putAnimation(Stance.STAND, src.getChild("fly"));
            putAnimation(Stance.MOVE, src.getChild("fly"));
        }
        else
        {
            putAnimation(Stance.STAND, src.getChild("stand"));
            putAnimation(Stance.MOVE, src.getChild("move"));
        }

        putAnimation(Stance.JUMP, src.getChild("jump"));
        putAnimation(Stance.HIT, src.getChild("hit1"));
        putAnimation(Stance.DIE, src.getChild("die1"));

        name = Loaded.getFile("String").getRoot().getChild("Mob.img").getChild(mid).getChild("name").get("");

        NXNode sndsrc = Loaded.getFile("Sound").getRoot().getChild("Mob.img").getChild(strid);

        hitsound = new Sound(sndsrc.getChild("Damage"));
        diesound = new Sound(sndsrc.getChild("Die"));

        speed += 100;
        speed *= 0.001f;

        flyspeed += 100;
        flyspeed *= 0.0005f;

        if (canfly)
            phobj.type = PhysicsObject.Type.FLYING;

        id = mid;
        team = tm;
        setPosition(position);
        setControl(mode);
        phobj.fhid = fh;
        phobj.setFlag(PhysicsObject.Flag.TURN_AT_EDGES);

        hppercent = 0;
        dying = false;
        dead = false;
        fading = false;
        setStance(st);
        flydirection = FlyDirection.STRAIGHT;
        counter = 0;

//        namelabel = Text(Text.Font.A13M, Text.Alignment.CENTER, Color.Name.WHITE, Text.Background.NAMETAG, name);

        if (newspawn)
        {
            fadein = true;
            opacity.set(0.0f);
        }
        else
        {
            fadein = false;
            opacity.set(1.0f);
        }

//        if (control && stance == Stance.STAND)
        nextMove();
        fixAnimatins();
    }

    private void putAnimation(Stance stance, NXNode src) {
        if(src == null || src.isNull()){
            return;
        }
        animations.put(stance, new Animation(src));
    }

    private void fixAnimatins() {
        for(Animation anim: animations.values()) {
            anim.shiftHead();
        }
    }

    public void draw(Point view, float alpha)
    {
        Point absp = phobj.getAbsolute(view, alpha);
        Point headpos = getHeadPosition(absp);

        if (!dead)
        {
            float interopc = opacity.get(alpha);
            animations.get(stance).draw(new DrawArgument(absp, flip && !noflip, interopc), alpha);
        }
    }


    @Override
    public byte update(Physics physics, int deltatime)
    {
        if (!active)
            return phobj.fhlayer;

        boolean aniend = animations.get(stance).update(deltatime);

        if (aniend && stance == Stance.DIE)
            dead = true;

        if (fading)
        {
            opacity.setMinus(0.025f);

            if (opacity.last() < 0.025f)
            {
                opacity.set(0.0f);
                fading = false;
                dead = true;
            }
        }
        else if (fadein)
        {
            opacity.setPlus(0.025f);

            if (opacity.last() > 0.975f)
            {
                opacity.set(1.0f);
                fadein = false;
            }
        }

        if (dead)
        {
            deActivate();

            return -1;
        }

//        effects.update();
//        showhp.update();

        if (!dying)
        {
            if (!canfly)
            {
                if (phobj.isFlagNotSet(PhysicsObject.Flag.TURN_AT_EDGES))
                {
                    flip = !flip;
                    phobj.setFlag(PhysicsObject.Flag.TURN_AT_EDGES);

                    if (stance == Stance.HIT)
                        setStance(Stance.STAND);
                }
            }

            switch (stance)
            {
                case MOVE:
                    if (canfly)
                    {
                        phobj.hforce = flip ? flyspeed : -flyspeed;

                        switch (flydirection)
                        {
                            case UPWARDS:
                                phobj.vforce = flyspeed;
                                break;
                            case DOWNWARDS:
                                phobj.vforce = -flyspeed;
                                break;
                        }
                    }
                    else
                    {
                        phobj.hforce = flip ? speed : -speed;
                    }

                    break;
                case HIT:
                    if (canmove)
                    {
                        double KBFORCE = phobj.onground ? 0.2 : 0.1;
                        phobj.hforce = (float) (flip ? -KBFORCE : KBFORCE);
                    }

                    break;
                case JUMP:
                    phobj.vforce = 5.0f;
                    break;
            }

            physics.moveObject(phobj);

//            if (control)
//            {
                counter++;

                boolean next;

                switch (stance)
                {
                    case HIT:
                        next = counter > 60;
                        break;
                    case JUMP:
                        next = phobj.onground;
                        break;
                    default:
                        next = aniend && counter > stanceLength;
                        break;
                }

                if (next)
                {
                    nextMove();
//                    updateMovement();
                }
//            }
        }
        else
        {
            phobj.normalize();
            physics.getFHT().updateFH(phobj);
        }

        return phobj.fhlayer;
    }

    private void nextMove() {
        if (canmove)
        {
            switch (stance)
            {
                case HIT:
                case STAND:
                    setStance(Stance.MOVE);
                    flip = Randomizer.nextBoolean();
                    break;
                case MOVE:
                case JUMP:
                    if (canjump && phobj.onground && Randomizer.below(0.25f))
                    {
                        setStance(Stance.JUMP);
                    }
                    else
                    {
                        switch (Randomizer.nextInt(3))
                        {
                            case 0:
                                setStance(Stance.STAND);
                                break;
                            case 1:
                                setStance(Stance.MOVE);
                                flip = false;
                                break;
                            case 2:
                                setStance(Stance.MOVE);
                                flip = true;
                                break;
                        }
                    }

                    break;
            }

            if (stance == Stance.MOVE && canfly)
                flydirection = Randomizer.nextEnum(FlyDirection.class);
        }
        else
        {
            setStance(Stance.STAND);
        }
        stanceLength = 400 * (int)Randomizer.nextExponential();
        counter = 0;
    }

    private Point getHeadPosition(Point pos) {
        Point head = new Point(animations.get(stance).getHead());

        pos.offsetThisX((flip && !noflip) ? -head.x : head.x);
        pos.offsetThisY(head.y);

        return pos;
    }

    private void setStance(byte stancebyte) {
        flip = (stancebyte % 2) == 0;

        if (!flip)
            stancebyte -= 1;

        if (stancebyte < Stance.MOVE.ordinal())
            stancebyte = (byte) Stance.MOVE.ordinal();

        setStance(Stance.values()[stancebyte]);
    }


    void setStance(Stance newstance)
    {
        if (stance != newstance)
        {
            stance = newstance;

            animations.get(stance).reset();
        }
    }

    public void setControl(byte mode) {
        control = mode > 0;
        aggro = mode == 2;
    }

    private void setFlip(boolean lookLeft) {
        flip = lookLeft;
    }
}

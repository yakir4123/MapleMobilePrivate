package com.bapplications.maplemobile.gameplay.mobs;

import com.bapplications.maplemobile.StaticUtils;
import com.bapplications.maplemobile.constatns.Configuration;
import com.bapplications.maplemobile.constatns.Loaded;
import com.bapplications.maplemobile.gameplay.Collider;
import com.bapplications.maplemobile.gameplay.audio.Sound;
import com.bapplications.maplemobile.gameplay.map.MapObject;
import com.bapplications.maplemobile.gameplay.physics.Physics;
import com.bapplications.maplemobile.gameplay.physics.PhysicsObject;
import com.bapplications.maplemobile.gameplay.textures.Animation;
import com.bapplications.maplemobile.opengl.utils.Color;
import com.bapplications.maplemobile.opengl.utils.DrawArgument;
import com.bapplications.maplemobile.opengl.utils.DrawableCircle;
import com.bapplications.maplemobile.opengl.utils.Linear;
import com.bapplications.maplemobile.opengl.utils.Point;
import com.bapplications.maplemobile.opengl.utils.Randomizer;
import com.bapplications.maplemobile.opengl.utils.Rectangle;
import com.bapplications.maplemobile.opengl.utils.TimedBool;
import com.bapplications.maplemobile.pkgnx.NXNode;

import java.util.HashMap;
import java.util.Map;

public class Mob extends MapObject implements Collider {

    enum Stance
    {
        MOVE,
        STAND,
        JUMP,
        HIT,
        DIE;
    };

    private int stanceLength = 400 * (int)Randomizer.nextExponential();

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
    boolean lookLeft;
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
        NXNode src = Loaded.getFile(Loaded.WzFileName.MOB).getRoot().getChild(strid + ".img");
        NXNode linkedNodes;

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

        if(info.isChildExist("link")){
            linkedNodes = Loaded.getFile(Loaded.WzFileName.MOB).getRoot().getChild(info.getChild("link").get("") + ".img");

        } else {
            linkedNodes = src;
        }

        canfly = linkedNodes.isChildExist("fly");
        canmove = linkedNodes.isChildExist("move") || canfly;

        if (canfly)
        {
            putAnimation(Stance.STAND, linkedNodes.getChild("fly"));
            putAnimation(Stance.MOVE, linkedNodes.getChild("fly"));
        }
        else
        {
            putAnimation(Stance.STAND, linkedNodes.getChild("stand"));
            putAnimation(Stance.MOVE, linkedNodes.getChild("move"));
        }

        putAnimation(Stance.JUMP, linkedNodes.getChild("jump"));
        putAnimation(Stance.HIT, linkedNodes.getChild("hit1"));
        putAnimation(Stance.DIE, linkedNodes.getChild("die1"));

        name = Loaded.getFile(Loaded.WzFileName.STRING).getRoot().getChild("Mob.img").getChild(mid).getChild("name").get("");

        NXNode sndsrc = Loaded.getFile(Loaded.WzFileName.SOUND).getRoot().getChild("Mob.img").getChild(strid);

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
        if(src == null || src.isNotExist()){
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
//        Point absp = getPosition();
        Point headpos = getHeadPosition(absp);
        if (!dead)
        {
            float interopc = opacity.get(alpha);

            DrawArgument dargs= new DrawArgument(absp, lookLeft && !noflip, interopc);
            animations.get(stance).draw(dargs, alpha);
            if (Configuration.SHOW_MOBS_RECT) {
                getCollider().draw(view);

                DrawableCircle origin = DrawableCircle.createCircle(getPosition(), Color.GREEN);
                origin.draw(dargs);

                origin = DrawableCircle.createCircle(headpos, Color.BLUE);
                origin.draw(dargs);

                origin = DrawableCircle.createCircle(absp, Color.RED);
                origin.draw(dargs);
            }
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
                    lookLeft = !lookLeft;
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
                        phobj.hforce = lookLeft ? flyspeed : -flyspeed;

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
                        phobj.hforce = lookLeft ? speed : -speed;
                    }

                    break;
                case HIT:
                    if (canmove)
                    {
                        double KBFORCE = phobj.onground ? 0.2 : 0.1;
                        phobj.hforce = (float) (lookLeft ? -KBFORCE : KBFORCE);
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
                    lookLeft = Randomizer.nextBoolean();
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
                                lookLeft = false;
                                break;
                            case 2:
                                setStance(Stance.MOVE);
                                lookLeft = true;
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

        pos.offsetThisX((lookLeft && !noflip) ? -head.x : head.x);
        pos.offsetThisY(head.y);

        return pos;
    }

    private void setStance(byte stancebyte) {
        lookLeft = (stancebyte % 2) == 0;

        if (!lookLeft)
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

    public boolean isAlive() {
        return active && !dying;
    }

    @Override
    public Rectangle getCollider() {
        Rectangle bounds = new Rectangle(animations.get(stance).getBounds());
        if (lookLeft) {
            bounds.setLeft(-bounds.left());
            bounds.setRight(-bounds.right());
        }
        bounds.shift(getPosition());
        
        return bounds;
    }

    public boolean isInRange(Collider collider)
    {
        if (!active)
            return false;


        return collider.getCollider().overlaps(getCollider());
    }

    public Attack.MobAttack createTouchAttack() {
        if (!touchdamage)
            return new Attack.MobAttack();

        int minattack = (int)(watk * 0.8f);
        int maxattack = watk;
        int attack = Randomizer.nextInt(minattack, maxattack);

        return new Attack.MobAttack(attack, getPosition(), id, oid);
    }
}

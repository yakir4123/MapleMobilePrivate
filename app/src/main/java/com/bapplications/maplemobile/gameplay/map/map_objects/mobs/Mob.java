package com.bapplications.maplemobile.gameplay.map.map_objects.mobs;

import com.bapplications.maplemobile.constatns.Configuration;
import com.bapplications.maplemobile.gameplay.components.ColliderComponent;
import com.bapplications.maplemobile.gameplay.map.MapObject;
import com.bapplications.maplemobile.gameplay.model_pools.MobModel;
import com.bapplications.maplemobile.gameplay.physics.Physics;
import com.bapplications.maplemobile.gameplay.physics.PhysicsObject;
import com.bapplications.maplemobile.gameplay.textures.Animation;
import com.bapplications.maplemobile.gameplay.model_pools.AnimationModel;
import com.bapplications.maplemobile.utils.Color;
import com.bapplications.maplemobile.utils.DrawArgument;
import com.bapplications.maplemobile.utils.DrawableCircle;
import com.bapplications.maplemobile.utils.Linear;
import com.bapplications.maplemobile.utils.Point;
import com.bapplications.maplemobile.utils.Randomizer;
import com.bapplications.maplemobile.utils.Rectangle;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

public class Mob extends MapObject implements ColliderComponent {

    public enum Stance
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
    
    MobModel mobModel;
    Map<Stance, Animation> animations = new EnumMap<>(Mob.Stance.class);


    int id;
    byte effect;
    byte team;
    boolean dying;
    boolean dead;
    boolean control;
    boolean aggro;
    Stance stance;
    boolean lookLeft;
    short stanceCounter;
    FlyDirection flydirection;
    float walkforce;
    byte hppercent;
    boolean fading;
    boolean fadein;
    Linear opacity;

    public Mob(int oi, MobModel mobModel, byte mode, byte st, short fh, boolean newspawn, byte tm, Point position)
    {
        super(oi);
        this.mobModel = mobModel;
        for(Stance stance: Stance.values()) {
            AnimationModel aniModel = mobModel.getAnimations().get(stance);
            if (aniModel != null) {
                animations.put(stance, new Animation(aniModel));
            }
        }

        opacity = new Linear();
        id = mobModel.getId();
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
        stanceCounter = 0;

        if (mobModel.getCanfly()) phobj.type = PhysicsObject.Type.FLYING;

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
    }

    public void draw(Point view, float alpha)
    {
        Point absp = phobj.getAbsolute(view, alpha);
        Point headpos = getHeadPosition(absp);
        if (!dead)
        {
            float interopc = opacity.get(alpha);

            DrawArgument dargs = new DrawArgument(absp, lookLeft && !mobModel.getNoflip(), interopc);
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
            if (!mobModel.getCanfly())
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
                    if (mobModel.getCanfly())
                    {
                        phobj.hforce = lookLeft ? mobModel.getFlyspeed() : -mobModel.getFlyspeed();

                        switch (flydirection)
                        {
                            case UPWARDS:
                                phobj.vforce = mobModel.getFlyspeed();
                                break;
                            case DOWNWARDS:
                                phobj.vforce = -mobModel.getFlyspeed();
                                break;
                        }
                    }
                    else
                    {
                        phobj.hforce = lookLeft ? mobModel.getSpeed() : -mobModel.getSpeed();
                    }

                    break;
                case HIT:
                    if (mobModel.getCanmove())
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
                stanceCounter++;

                boolean next;

                switch (stance)
                {
                    case HIT:
                        next = stanceCounter > 60;
                        break;
                    case JUMP:
                        next = phobj.onground;
                        break;
                    default:
                        next = aniend && stanceCounter > stanceLength;
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
        if (mobModel.getCanmove())
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
                    if (mobModel.getCanjump() && phobj.onground && Randomizer.below(0.25f))
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

            if (stance == Stance.MOVE && mobModel.getCanfly())
                flydirection = Randomizer.nextEnum(FlyDirection.class);
        }
        else
        {
            setStance(Stance.STAND);
        }
        stanceLength = 400 * (int)Randomizer.nextExponential();
        stanceCounter = 0;
    }

    private Point getHeadPosition(Point pos) {
        Point head = new Point(Objects.requireNonNull(animations.get(stance)).getHead());

        pos.offsetThisX((lookLeft && !mobModel.getNoflip()) ? -head.x : head.x);
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

            animations.forEach((stance, animation) -> animation.reset());
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
        Rectangle bounds = Objects.requireNonNull(animations.get(stance)).getBounds();
        if (lookLeft) {
            bounds.setLeft(-bounds.left());
            bounds.setRight(-bounds.right());
        }
        bounds.shift(getPosition());
        
        return bounds;
    }

    public boolean isInRange(ColliderComponent collider)
    {
        if (!active)
            return false;


        return collider.getCollider().overlaps(getCollider());
    }

    public Attack.MobAttack createTouchAttack() {
        if (!mobModel.getTouchdamage())
            return new Attack.MobAttack();

        int minattack = (int)(mobModel.getWatk() * 0.8f);
        int maxattack = mobModel.getWatk();
        int attack = Randomizer.nextInt(minattack, maxattack);

        return new Attack.MobAttack(attack, getPosition(), id, oid);
    }
}

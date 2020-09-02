package com.bapplications.maplemobile.gameplay.player;

import com.bapplications.maplemobile.constatns.Configuration;
import com.bapplications.maplemobile.gameplay.Collider;
import com.bapplications.maplemobile.gameplay.audio.Sound;
import com.bapplications.maplemobile.gameplay.map.Ladder;
import com.bapplications.maplemobile.gameplay.map.Layer;
import com.bapplications.maplemobile.gameplay.mobs.Attack;
import com.bapplications.maplemobile.gameplay.physics.Physics;
import com.bapplications.maplemobile.gameplay.player.state.PlayerClimbState;
import com.bapplications.maplemobile.gameplay.player.state.PlayerFallState;
import com.bapplications.maplemobile.gameplay.player.state.PlayerProneState;
import com.bapplications.maplemobile.gameplay.player.state.PlayerStandState;
import com.bapplications.maplemobile.gameplay.player.state.PlayerState;
import com.bapplications.maplemobile.gameplay.player.state.PlayerWalkState;
import com.bapplications.maplemobile.opengl.utils.Point;
import com.bapplications.maplemobile.opengl.utils.Rectangle;
import com.bapplications.maplemobile.opengl.utils.TimedBool;
import com.bapplications.maplemobile.views.KeyAction;
import com.bapplications.maplemobile.views.UIControllers;

import java.util.Arrays;
import java.util.Collection;
import java.util.TreeSet;

public class Player extends Char implements Collider {

    private Ladder ladder;
    private boolean attacking;
    private TimedBool climb_cooldown;
    private boolean underwater;
    private final UIControllers controllers;
    private TreeSet<Expression> myExpressions;


    public Player(CharEntry entry, UIControllers controllers) {
        super(entry.id, new CharLook(entry.look), entry.stats.name);
        this.controllers = controllers;
        attacking = false;
        underwater = false;

        setState(State.STAND);
        myExpressions = new TreeSet<>();
        myExpressions.addAll(Arrays.asList(Expression.values()));
        climb_cooldown = new TimedBool();
    }

    @Override
    public void setState(State state) {
        if (!attacking)
        {
            super.setState(state);

			PlayerState pst = getState(state);

            if (pst != null)
                pst.initialize(this);
        }
    }


    public void draw(Layer layer, Point viewpos, float alpha)
    {
        if (layer == getLayer())
            super.draw(viewpos, alpha);
        if (Configuration.SHOW_PLAYER_RECT) {
            Rectangle player_rect = getCollider();
            player_rect.draw(viewpos);
        }
    }


    public void respawn(Point pos, boolean underwater) {
        setPosition(pos.x, pos.y);
        this.underwater = underwater;
        attacking = false;
        ladder = null;
//        nullstate.update_state(*this);
    }

    public byte update(Physics physics, int deltatime) {

		PlayerState pst = getState(state);

        if (pst != null)
        {
            pst.update(this);
            physics.moveObject(phobj);

            boolean aniend = super.update(physics, getStanceSpeed(), deltatime);

            if (aniend && attacking)
            {
                attacking = false;
//                nullstate.update_state(this);
            }
            else
            {
                pst.updateState(this);
            }
        }
//
//        byte stancebyte = facing_right ? state : state + 1;
//        Movement newmove(phobj, stancebyte);
//        boolean needupdate = lastmove.hasmoved(newmove);
//
//        if (needupdate)
//        {
//            MovePlayerPacket(newmove).dispatch();
//            lastmove = newmove;
//        }
//
        climb_cooldown.update(deltatime);

        return (byte) getLayer().ordinal();
    }

    private float getStanceSpeed() {

//        if (attacking)
//            return get_real_attackspeed();

        switch (state)
        {
            case WALK:
                return (float)(Math.abs(phobj.hspeed));
            case LADDER:
            case ROPE:
                return (float)(Math.abs(phobj.vspeed));
            default:
                return 1.0f;
        }
    }

    private static PlayerProneState lying = new PlayerProneState();
    private static PlayerWalkState walking = new PlayerWalkState();
    private static PlayerFallState falling = new PlayerFallState();
    private static PlayerStandState standing = new PlayerStandState();
    private static PlayerClimbState climbing = new PlayerClimbState();
    //    private static PlayerSitState sitting = new PlayerStandState();
//    private static PlayerFlyState flying = new PlayerStandState();
    private PlayerState getState(State state) {

        switch (state)
        {
            case STAND:
                return standing;
            case WALK:
                return walking;
            case FALL:
                return falling;
            case PRONE:
                return lying;
            case LADDER:
            case ROPE:
                return climbing;
//            case Char.State.SIT:
//                return sitting;
//            case Char.State.SWIM:
//                return flying;
            default:
                return null;
        }

    }

    public boolean isAttacking() {
        return attacking;
    }

    public boolean isPressed(KeyAction key) {
        return controllers.isPressed(key);
    }

    public boolean hasWalkInput() {
        return controllers.isPressed(KeyAction.LEFT_ARROW_KEY)
                || controllers.isPressed(KeyAction.RIGHT_ARROW_KEY);

    }

    public float getWalkForce() {
        return 0.05f + 0.32f;// * (float)(stats.get_total(EquipStat::Id::SPEED)) / 100;
    }

    public float getJumpForce() {
        return 1.0f + 6f;// * (float)(stats.get_total(EquipStat::Id::JUMP)) / 100;
    }

    public float getClimbForce() {
        return 1.5f;//static_cast<float>(stats.get_total(EquipStat::Id::SPEED)) / 100;
    }

    public void setDirection(boolean lookLeft) {
        if (!attacking)
            super.setDirection(lookLeft);
    }

    public void sendAction(KeyAction key) {
		PlayerState pst = getState(state);

        if (pst != null)
            pst.sendAction(this, key);
    }

    public void playJumpSound() {
        (new Sound(Sound.Name.JUMP)).play();
    }

    public Point getPosition() {
        return getPhobj().getPosition();
    }

    public Collection getExpressions() {
        return myExpressions;
    }

    public Ladder getLadder() {
        return ladder;
    }
    
    public void setLadder(Ladder ldr) {
        ladder = ldr;

        if (ladder != null)
        {
            phobj.set_x(ldr.getX());

            phobj.hspeed = 0.0f;
            phobj.vspeed = 0.0f;
            phobj.fhlayer = 7;

            setState(ldr.isLadder() ? Char.State.LADDER : Char.State.ROPE);
        }
    }

    public void setClimbCooldown() {
        climb_cooldown.setFor(1000);
    }


    public boolean canClimb()
    {
        return !climb_cooldown.isTrue();
    }

    public Attack.MobAttackResult damage(Attack.MobAttack attack) {
        int damage = 1; //stats.calculate_damage(attack.watk);
        showDamage(damage);

        boolean fromleft = attack.origin.x > phobj.getX();

//        boolean missed = damage <= 0;
//        boolean immovable = ladder != null || state == Char.State.DIED;
//        boolean knockback = !missed && !immovable;

//        if (knockback && randomizer.above(stats.get_stance()))
//        {
            phobj.hspeed = (float) (fromleft ? -1.5 : 1.5);
            phobj.vforce -= 3.5;
//        }

        byte direction = (byte) (fromleft ? 0 : 1);

        return new Attack.MobAttackResult(attack, damage, direction);
    }

    @Override
    public Rectangle getCollider() {
        return new Rectangle(
                phobj.getLastX() - 10,
                phobj.getX() + 10,
                phobj.getLastY(),
                phobj.getY() + 50);
    }
}

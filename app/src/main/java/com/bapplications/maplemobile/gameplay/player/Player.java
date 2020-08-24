package com.bapplications.maplemobile.gameplay.player;

import com.bapplications.maplemobile.gameplay.audio.Sound;
import com.bapplications.maplemobile.gameplay.map.Layer;
import com.bapplications.maplemobile.gameplay.physics.Physics;
import com.bapplications.maplemobile.gameplay.player.state.PlayerFallState;
import com.bapplications.maplemobile.gameplay.player.state.PlayerProneState;
import com.bapplications.maplemobile.gameplay.player.state.PlayerStandState;
import com.bapplications.maplemobile.gameplay.player.state.PlayerState;
import com.bapplications.maplemobile.gameplay.player.state.PlayerWalkState;
import com.bapplications.maplemobile.opengl.utils.Point;
import com.bapplications.maplemobile.views.KeyAction;
import com.bapplications.maplemobile.views.UIControllers;

public class Player extends Char {

    private boolean attacking;
    private final boolean underwater;
    private final UIControllers controllers;


    public Player(CharEntry entry, UIControllers controllers) {
        super(entry.id, new CharLook(entry.look), entry.stats.name);

        this.controllers = controllers;
        attacking = false;
        underwater = false;

        setState(State.STAND);
//        setDirection(true);
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
    }


    public void respawn(Point pos, boolean underwater) {
        setPosition(pos.x, pos.y);
//        this.underwater = underwater;
//        keysdown.clear();
//        attacking = false;
//        ladder = nullptr;
//        nullstate.update_state(*this);
    }

    public int update(Physics physics, int deltatime) {

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
//        climb_cooldown.update();

        return getLayer().ordinal();
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

    private static PlayerStandState standing = new PlayerStandState();
    private static PlayerWalkState walking = new PlayerWalkState();
    private static PlayerFallState falling = new PlayerFallState();
    private static PlayerProneState lying = new PlayerProneState();
//    private static PlayerClimbState climbing = new PlayerStandState();
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
//            case Char.State.LADDER:
//            case Char.State.ROPE:
//                return climbing;
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
        return 0.05f + 0.3f;// * (float)(stats.get_total(EquipStat::Id::SPEED)) / 100;
    }

    public float getJumpForce() {
        return 1.0f + 6f;// * (float)(stats.get_total(EquipStat::Id::JUMP)) / 100;
    }

    public void setDirection(boolean flipped) {
        if (!attacking)
            super.setDirection(flipped);
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
}

package com.bapplications.maplemobile.gameplay.player;

import com.bapplications.maplemobile.gameplay.map.Layer;
import com.bapplications.maplemobile.gameplay.physics.Physics;
import com.bapplications.maplemobile.gameplay.player.state.PlayerFallState;
import com.bapplications.maplemobile.gameplay.player.state.PlayerStandState;
import com.bapplications.maplemobile.gameplay.player.state.PlayerState;
import com.bapplications.maplemobile.opengl.utils.Point;

public class Player extends Char {

    private boolean attacking;
    private final boolean underwater;

    public Player(CharEntry entry) {
        super(entry.id, new CharLook(entry.look), entry.stats.name);

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
//    private static PlayerWalkState walking = new PlayerStandState();
    private static PlayerFallState falling = new PlayerFallState();
//    private static PlayerProneState lying = new PlayerStandState();
//    private static PlayerClimbState climbing = new PlayerStandState();
//    private static PlayerSitState sitting = new PlayerStandState();
//    private static PlayerFlyState flying = new PlayerStandState();
    private PlayerState getState(State state) {

        switch (state)
        {
            case STAND:
                return standing;
//            case Char.State.WALK:
//                return walking;
            case FALL:
                return falling;
//            case Char.State.PRONE:
//                return lying;
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
}

package com.bapplications.maplemobile.gameplay.player.look;

import com.bapplications.maplemobile.gameplay.audio.Sound;
import com.bapplications.maplemobile.gameplay.map.Ladder;
import com.bapplications.maplemobile.gameplay.player.state.PlayerClimbState;
import com.bapplications.maplemobile.gameplay.player.state.PlayerFallState;
import com.bapplications.maplemobile.gameplay.player.state.PlayerProneState;
import com.bapplications.maplemobile.gameplay.player.state.PlayerStandState;
import com.bapplications.maplemobile.gameplay.player.state.PlayerState;
import com.bapplications.maplemobile.gameplay.player.state.PlayerWalkState;
import com.bapplications.maplemobile.input.InputAction;
import com.bapplications.maplemobile.utils.Point;
import com.bapplications.maplemobile.utils.TimedBool;
import com.bapplications.maplemobile.gameplay.map.Layer;
import com.bapplications.maplemobile.utils.DrawArgument;
import com.bapplications.maplemobile.gameplay.player.Stance;
import com.bapplications.maplemobile.gameplay.map.MapObject;
import com.bapplications.maplemobile.gameplay.physics.Physics;

import java.util.ArrayList;

public abstract class Char extends MapObject {

    protected State state;
    protected Ladder ladder;
    protected boolean attacking;
    private final CharLook look;
    protected TimedBool climb_cooldown;
    private final CharLook look_preview;
    private TimedBool invincible = new TimedBool();

    private ArrayList<InputAction> pressedButton = new ArrayList<>();

    protected boolean lookLeft = true;


    protected Char(int o, CharLook lk, String name) {
        super(o);
        look = lk;
        look_preview = lk;
        climb_cooldown = new TimedBool();
//        namelabel(Text(Text::Font::A13M, Text::Alignment::CENTER, Color::Name::
//        WHITE, Text::Background::NAMETAG, name));
    }

    public static void init() {
        CharLook.init();
    }

    public void draw(Point viewpos, float alpha) {
        Point absp = phobj.getAbsolute(viewpos, alpha);

        look.draw(new DrawArgument(absp), alpha);

//        afterimage.draw(look.get_frame(), DrawArgument(absp, facing_right), alpha);

//        if (ironbody)
//        {
//            float ibalpha = ironbody.alpha();
//            float scale = 1.0f + ibalpha;
//            float opacity = 1.0f - ibalpha;
//
//            look.draw(DrawArgument(absp, scale, scale, opacity), alpha);
//        }
//
//        for (auto& pet : pets)
//        if (pet.get_itemid())
//            pet.draw(viewx, viewy, alpha);
//
//        // If ever changing code for namelabel confirm placements with map 10000
//        namelabel.draw(absp + Point<int16_t>(0, -4));
//        chatballoon.draw(absp - Point<int16_t>(0, 85));
//
//        effects.drawabove(absp, alpha);
//
//        for (auto& number : damagenumbers)
//            number.draw(viewx, viewy, alpha);

    }



    public byte update(Physics physics, int deltaTime) {

        PlayerState pst = getState(state);

        if (pst != null) {
            pst.update(this);
            physics.moveObject(phobj);

            short stancespeed = 0;
            if (getStanceSpeed() >= 1.0f / deltaTime)
                stancespeed = (short)(deltaTime * getStanceSpeed());
            invincible.update(deltaTime);
            boolean aniend = look.update(stancespeed);

            if (aniend && attacking) {
                attacking = false;
            } else {
                pst.updateState(this);
            }
        }
        climb_cooldown.update(deltaTime);

        return (byte) getLayer().ordinal();
    }

    public abstract float getStanceSpeed();

    public void setState(State state) {
            this.state = state;

        Stance.Id stance = Stance.byState(state);
        look.setStance(stance);
        PlayerState pst = getState(state);

        if (pst != null)
            pst.initialize(this);
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
    public Ladder getLadder() {
        return ladder;
    }

    public void setLadder(Ladder ldr) {
        ladder = ldr;

        if (ladder != null) {
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


    public void setLookLeft(boolean lookLeft) {
        this.lookLeft = lookLeft;
        look.setDirection(lookLeft);
    }

    public boolean getLookLeft() {
        return lookLeft;
    }


    public CharLook getLook() {
        return look;
    }

    public void setExpression(Expression expression) {
        getLook().setExpression(expression);
    }

    // Player states which determine animation and state
    // Values are used in movement packets (Add one if facing left)
    public enum State
    {
        WALK (Stance.Id.WALK1),
        STAND (Stance.Id.STAND1),
        FALL (Stance.Id.JUMP),
        ALERT (Stance.Id.ALERT),
        PRONE (Stance.Id.PRONE),
        SWIM (Stance.Id.FLY),
        LADDER (Stance.Id.LADDER),
        ROPE (Stance.Id.ROPE),
        DIED (Stance.Id.DEAD),
        SIT (Stance.Id.SIT);

        private final Stance.Id val;

        State(Stance.Id val) {
            this.val = val;
        }
        public Stance.Id getStance() { return val;}
    }

    public boolean update(Physics physics, float speed, int deltaTime){
        short stancespeed = 0;

        if (speed >= 1.0f / deltaTime)
            stancespeed = (short)(deltaTime * speed);
        invincible.update(deltaTime);
        return look.update(stancespeed);
    }

    public boolean isInvincible()
    {
        return invincible.isTrue();
    }

    public Layer getLayer() {
        return Layer.byValue(isClimbing() ? 7 : phobj.fhlayer);
    }

    public boolean isClimbing() {
        return state == State.LADDER || state == State.ROPE;
    }

    protected void showDamage(int damage) {
        short start_y = (short) (phobj.getY() - 60);
        short x = (short) (phobj.getX() - 10);


        look.setAlerted(5000);
        invincible.setFor(2000);
    }


    private static PlayerProneState lying = new PlayerProneState();
    private static PlayerWalkState walking = new PlayerWalkState();
    private static PlayerFallState falling = new PlayerFallState();
    private static PlayerStandState standing = new PlayerStandState();
    private static PlayerClimbState climbing = new PlayerClimbState();
//    private static PlayerSitState sitting = new PlayerStandState();
//    private static PlayerFlyState flying = new PlayerStandState();
    protected PlayerState getState(State state) {

        switch (state) {
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

    public void playJumpSound() {
        (new Sound(Sound.Name.JUMP)).play();
    }

    public Point getPosition() {
        return getPhobj().getPosition();
    }

    public boolean isAttacking() {
        return attacking;
    }

    public boolean hasWalkInput() {
        return isPressed(InputAction.LEFT_ARROW_KEY) || isPressed(InputAction.RIGHT_ARROW_KEY);
    }

    public boolean clickedButton(InputAction key) {
        PlayerState pst = getState(state);

        if (pst == null) {
            return false;
        }

        if(pressedButton.contains(key)) {
            return true;
        }

        if (key.getType() == InputAction.Type.CONTINUES_CLICK){
            pressedButton.add(key);
        }
        return pst.sendAction(this, key);
    }

    public boolean releasedButtons(InputAction key) {
        if (!pressedButton.contains(key)) {
            return false;
        }
        pressedButton.remove(key);
        return true;
    }

    public boolean isPressed(InputAction key) {
        return pressedButton.contains(key);
    }
}

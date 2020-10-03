package com.bapplications.maplemobile.gameplay.player;

import com.bapplications.maplemobile.constatns.Configuration;
import com.bapplications.maplemobile.gameplay.Collider;
import com.bapplications.maplemobile.gameplay.audio.Sound;
import com.bapplications.maplemobile.gameplay.map.Ladder;
import com.bapplications.maplemobile.gameplay.map.Layer;
import com.bapplications.maplemobile.gameplay.mobs.Attack;
import com.bapplications.maplemobile.gameplay.physics.Physics;
import com.bapplications.maplemobile.gameplay.player.inventory.Inventory;
import com.bapplications.maplemobile.gameplay.player.inventory.InventoryType;
import com.bapplications.maplemobile.gameplay.player.state.PlayerClimbState;
import com.bapplications.maplemobile.gameplay.player.state.PlayerFallState;
import com.bapplications.maplemobile.gameplay.player.state.PlayerProneState;
import com.bapplications.maplemobile.gameplay.player.state.PlayerStandState;
import com.bapplications.maplemobile.gameplay.player.state.PlayerState;
import com.bapplications.maplemobile.gameplay.player.state.PlayerWalkState;
import com.bapplications.maplemobile.opengl.utils.Color;
import com.bapplications.maplemobile.opengl.utils.DrawArgument;
import com.bapplications.maplemobile.opengl.utils.DrawableCircle;
import com.bapplications.maplemobile.opengl.utils.Point;
import com.bapplications.maplemobile.opengl.utils.Rectangle;
import com.bapplications.maplemobile.opengl.utils.TimedBool;
import com.bapplications.maplemobile.views.KeyAction;
import com.bapplications.maplemobile.views.GameActivityUIManager;

import java.util.Arrays;
import java.util.Collection;
import java.util.TreeSet;

public class Player extends Char implements Collider {

    private Ladder ladder;
    private PlayerStats stats;
    private boolean attacking;
    private boolean underwater;
    private Inventory inventory;
    private TimedBool climb_cooldown;
    private final GameActivityUIManager controllers;
    private TreeSet<Expression> myExpressions;


    public Player(CharEntry entry, GameActivityUIManager controllers) {
        super(entry.id, new CharLook(entry.look), entry.stats.name);
        attacking = false;
        underwater = false;
        this.controllers = controllers;

        loadStats();
        setState(State.STAND);
        inventory = new Inventory();
        addItem();

        myExpressions = new TreeSet<>();
        myExpressions.addAll(Arrays.asList(Expression.values()));
        climb_cooldown = new TimedBool();

        controllers.getViewModel().init(this);
    }

    private void loadStats() {
        stats = new PlayerStats();
        stats.setStat(PlayerStats.Id.MAX_HP, (short) 100);
        stats.setStat(PlayerStats.Id.MAX_MP, (short) 50);
        stats.setStat(PlayerStats.Id.HP, (short) 32);
        stats.setStat(PlayerStats.Id.MP, (short) 42);
        stats.setStat(PlayerStats.Id.EXP, (short) 10);
    }

    @Override
    public void setState(State state) {
        if (!attacking) {
            super.setState(state);

            PlayerState pst = getState(state);

            if (pst != null)
                pst.initialize(this);
        }
    }


    public void draw(Layer layer, Point viewpos, float alpha) {
        if (layer == getLayer())
            super.draw(viewpos, alpha);
        if (Configuration.SHOW_PLAYER_RECT) {
            Rectangle player_rect = getCollider();
            player_rect.draw(viewpos);

            DrawableCircle origin = DrawableCircle.createCircle(getPosition(), Color.GREEN);
            origin.draw(new DrawArgument(viewpos));
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

        if (pst != null) {
            pst.update(this);
            physics.moveObject(phobj);

            boolean aniend = super.update(physics, getStanceSpeed(), deltatime);

            if (aniend && attacking) {
                attacking = false;
//                nullstate.update_state(this);
            } else {
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

        switch (state) {
            case WALK:
                return Math.abs(phobj.hspeed);
            case LADDER:
            case ROPE:
                return Math.abs(phobj.vspeed);
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

    public short getStat(PlayerStats.Id id) {
        return stats.getStat(id);
    }

    public PlayerStats setStat(PlayerStats.Id id, short val) {
        return stats.setStat(id, val);
    }

    public PlayerStats addStat(PlayerStats.Id id, short val) {
        stats.addStat(id, val);
        switch (id){
            case MAX_MP:
                controllers.getViewModel().setHp(getStat(PlayerStats.Id.MAX_MP));
                break;
            case MAX_HP:
                controllers.getViewModel().setHp(getStat(PlayerStats.Id.MAX_HP));
                break;
            case HP:
                controllers.getViewModel().setHp(getStat(PlayerStats.Id.HP));
                break;
            case MP:
                controllers.getViewModel().setMp(getStat(PlayerStats.Id.MP));
                break;
            case EXP:
                controllers.getViewModel().setExp(getStat(PlayerStats.Id.EXP));
                break;
            case LEVEL:
//                controllers.getViewModel().setLevel(getStat(PlayerStats.Id.LEVEL));
                break;
        }
        return stats;
    }

    // todo change signature
    public void addItem(){
        inventory.addItem(InventoryType.Id.EQUIP, (short)0, 1050045,false, -1L, (short)0, null, (short)0);
        inventory.addItem(InventoryType.Id.EQUIP, (short)1, 1002017,false, -1L, (short)0, null, (short)0);
        inventory.addItem(InventoryType.Id.EQUIP, (short)2, 1092045,false, -1L, (short)0, null, (short)0);
    }

    public void playJumpSound() {
        (new Sound(Sound.Name.JUMP)).play();
    }

    public Point getPosition() {
        return getPhobj().getPosition();
    }

    public Collection<Expression> getExpressions() {
        return myExpressions;
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


    public boolean canClimb() {
        return !climb_cooldown.isTrue();
    }

    public Attack.MobAttackResult damage(Attack.MobAttack attack) {
        int damage = 1; //stats.calculate_damage(attack.watk);
        addStat(PlayerStats.Id.HP, (short) -damage);
        showDamage(damage);
        boolean fromleft = attack.origin.x > phobj.getX();

        boolean missed = damage <= 0;
        boolean immovable = ladder != null || state == Char.State.DIED;
        boolean knockback = !missed && !immovable;

        if (knockback /*&& Randomizer.above(stats.getStance())*/)
        {
            phobj.hspeed = (float) (fromleft ? -1.5 : 1.5);
            phobj.vforce -= 3.5;
        }

        byte direction = (byte) (fromleft ? 0 : 1);

        return new Attack.MobAttackResult(attack, damage, direction);
    }

    @Override
    public Rectangle getCollider() {
        int left, right, bottom, top;
        if (state == State.PRONE) {
            left = 10;
            right = -50;
            bottom = 0;
            top = 30;
        } else {
            left = -15;
            right = 12;
            bottom = 0;
            top = 55;
        }
        if (!lookLeft) {
            left *= -1;
            right *= -1;
        }
        return new Rectangle(
            phobj.getLastX() + left,
            phobj.getX() + right,
            phobj.getLastY() + bottom,
            phobj.getY() + top);
    }

    public Inventory getInventory() {
        return inventory;
    }
}

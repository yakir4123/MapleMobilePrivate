package com.bapplications.maplemobile.gameplay.player;

import com.bapplications.maplemobile.gameplay.GameMap;
import com.bapplications.maplemobile.gameplay.physics.Physics;
import com.bapplications.maplemobile.input.EventsQueue;
import com.bapplications.maplemobile.gameplay.map.Layer;
import com.bapplications.maplemobile.input.events.Event;
import com.bapplications.maplemobile.input.events.EventType;
import com.bapplications.maplemobile.constatns.Configuration;
import com.bapplications.maplemobile.gameplay.player.look.Char;
import com.bapplications.maplemobile.input.events.DropItemEvent;
import com.bapplications.maplemobile.input.events.EventListener;
import com.bapplications.maplemobile.gameplay.player.look.CharLook;
import com.bapplications.maplemobile.input.events.PlayerStateUpdateEvent;
import com.bapplications.maplemobile.input.events.PressButtonEvent;
import com.bapplications.maplemobile.gameplay.player.inventory.Item;
import com.bapplications.maplemobile.gameplay.player.inventory.Slot;
import com.bapplications.maplemobile.gameplay.player.inventory.Equip;
import com.bapplications.maplemobile.gameplay.player.look.Expression;
import com.bapplications.maplemobile.input.events.ExpressionButtonEvent;
import com.bapplications.maplemobile.gameplay.player.inventory.Inventory;
import com.bapplications.maplemobile.gameplay.map.map_objects.mobs.Attack;
import com.bapplications.maplemobile.gameplay.components.ColliderComponent;
import com.bapplications.maplemobile.gameplay.player.inventory.InventoryType;

import com.bapplications.maplemobile.ui.view_models.GameActivityViewModel;
import com.bapplications.maplemobile.utils.Color;
import com.bapplications.maplemobile.utils.Point;
import com.bapplications.maplemobile.utils.Rectangle;
import com.bapplications.maplemobile.input.InputAction;
import com.bapplications.maplemobile.utils.DrawArgument;
import com.bapplications.maplemobile.utils.DrawableCircle;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.TreeSet;
import java.util.Collection;

import androidx.lifecycle.LiveData;


public class Player extends Char implements ColliderComponent , EventListener {

    private GameMap map;
    private PlayerStatsViewModel stats;
    private Inventory inventory;
    private TreeSet<Expression> myExpressions;
    private short lastUpdate = 0;

    public Player(CharEntry entry) {
        super(entry.id, new CharLook(entry.look), entry.stats.name);
        attacking = false;
        underwater = false;

        setState(State.STAND);
        inventory = new Inventory();
        addItem();

        myExpressions = new TreeSet<>();
        myExpressions.addAll(Arrays.asList(Expression.values()));

        EventsQueue.Companion.getInstance().registerListener(EventType.PressButton, this);
        EventsQueue.Companion.getInstance().registerListener(EventType.ExpressionButton, this);
    }

    public void setStats(PlayerStatsViewModel stats) {
        this.stats = stats;
        stats.setStat(PlayerStatsViewModel.Id.MAX_HP, (short) 100);
        stats.setStat(PlayerStatsViewModel.Id.MAX_MP, (short) 50);
        stats.setStat(PlayerStatsViewModel.Id.HP, (short) 32);
        stats.setStat(PlayerStatsViewModel.Id.MP, (short) 42);
        stats.setStat(PlayerStatsViewModel.Id.EXP, (short) 113);
        stats.setStat(PlayerStatsViewModel.Id.LEVEL, (short) 12);
        stats.setStat(PlayerStatsViewModel.Id.JOB, (short) 220);
        stats.getName().postValue("LapLap");
    }

    @Override
    public void setState(State state) {
        if (!attacking) {
            super.setState(state);
        }
    }

    @Override
    public byte update(Physics physics, int deltaTime) {
        byte res = super.update(physics, deltaTime);
        lastUpdate += deltaTime;
        if(lastUpdate > Configuration.UPDATE_DIFF_TIME) {
            lastUpdate -= Configuration.UPDATE_DIFF_TIME;
            EventsQueue.Companion.getInstance().enqueue(new PlayerStateUpdateEvent(0, getState(), getPosition()));
        }
        return res;
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

    public float getStanceSpeed() {

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

    public void setLookLeft(boolean lookLeft) {
        if (!attacking)
            super.setLookLeft(lookLeft);
    }

    public PlayerStatsViewModel getStats() {
        return stats;
    }

    public LiveData<Short> getStat(PlayerStatsViewModel.Id id) {
        return stats.getStat(id);
    }

    public PlayerStatsViewModel addStat(PlayerStatsViewModel.Id id, short val) {
        return stats.addStat(id, val);
    }

    public boolean changeEquip(Slot to)
    {
        boolean changed = inventory.equipItem((Equip) to.getItem());
        if(changed) {
            getLook().addEquip(to.getItemId());
            inventory.getInventory(InventoryType.Id.EQUIP).popItem(to.getSlotId());
        }
        return changed;
    }

    public boolean dropItem(Slot slot) {
        InventoryType.Id invType = slot.getInventoryType();
        // it actually just like slot
        Slot dropped = inventory.getInventory(invType).popItem(slot.getSlotId());
        if(dropped.isEmpty()) {
            return false;
        }
        EventsQueue.Companion.getInstance()
                .enqueue(new DropItemEvent(dropped.getItemId(), getPosition(),
                                        0, invType.ordinal(), slot.getSlotId(), map.getMapId()));

        return true;
    }

    // todo change signature
    public void addItem(){
        inventory.addItem(new Equip(1002357, -1L,  null, (short)0, (byte)7, (byte)0), (short)1);
        inventory.addItem(new Equip(1050045, -1L,  null, (short)0, (byte)7, (byte)0), (short)1);
        inventory.addItem(new Equip(1002017, -1L,  null, (short)0, (byte)7, (byte)0), (short)1);
        inventory.addItem(new Equip(1092045, -1L,  null, (short)0, (byte)7, (byte)0), (short)1);
        inventory.addItem(new Equip(1050087, -1L,  null, (short)0, (byte)7, (byte)0), (short)1);
        inventory.addItem(new Equip(1002575, -1L,  null, (short)0, (byte)7, (byte)0), (short)1);
        inventory.addItem(new Equip(1002356, -1L,  null, (short)0, (byte)7, (byte)0), (short)1);
        inventory.addItem(new Equip(1050040, -1L,  null, (short)0, (byte)7, (byte)0), (short)1);
        inventory.addItem(new Item(2000000, -1L,  null, (short)0), (short)76);
        inventory.addItem(new Item(2000001, -1L,  null, (short)0), (short)50);
        inventory.addItem(new Item(2000002, -1L,  null, (short)0), (short)100);
        inventory.addItem(new Item(2002000, -1L,  null, (short)0), (short)100);
        inventory.addItem(new Item(2070006, -1L,  null, (short)0), (short)800);
        inventory.addItem(new Item(4020006, -1L,  null, (short)0), (short)19);
        inventory.addItem(new Item(3010072, -1L,  null, (short)0), (short)1);
        inventory.addItem(new Item(3010106, -1L,  null, (short)0), (short)1);
        inventory.addItem(new Item(5021011, -1L,  null, (short)0), (short)1);
        inventory.addItem(new Item(5030000, -1L,  null, (short)0), (short)1);
        inventory.addItem(new Item(5000000, -1L,  null, (short)0), (short)1);
        inventory.addItem(new Item(5000028, -1L,  null, (short)0), (short)1);
    }

    public Collection<Expression> getExpressions() {
        return myExpressions;
    }

    public boolean canClimb() {
        return !climb_cooldown.isTrue();
    }

    public Attack.MobAttackResult damage(Attack.MobAttack attack) {
        int damage = 1; //stats.calculate_damage(attack.watk);
        addStat(PlayerStatsViewModel.Id.HP, (short) -damage);
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

    public void setMap(GameMap map) {
        this.map = map;
    }

    public GameMap getMap() {
        return map;
    }

    @Override
    public void onEventReceive(@NotNull Event event) {
        switch(event.getType()) {
            case PressButton: {
                PressButtonEvent _event = (PressButtonEvent) event;
                if(_event.getCharid() == 0) {
                    if (_event.getPressed()) {
                        clickedButton(InputAction.byKey(_event.getButtonPressed()));
                    } else {
                        releasedButtons(InputAction.byKey(_event.getButtonPressed()));
                    }
                }
                break;
            }
            case ExpressionButton: {
                ExpressionButtonEvent _event = (ExpressionButtonEvent) event;
                if(_event.getCharid() == 0) {
                    setExpression(_event.getExpression());
                }
            }

        }
    }
}

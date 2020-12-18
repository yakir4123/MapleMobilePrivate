package com.bapplications.maplemobile.gameplay.map;

import com.bapplications.maplemobile.gameplay.components.ColliderComponent;
import com.bapplications.maplemobile.gameplay.textures.Animation;
import com.bapplications.maplemobile.utils.DrawArgument;
import com.bapplications.maplemobile.utils.Point;
import com.bapplications.maplemobile.utils.Rectangle;

public class Portal implements ColliderComponent {

    private final Type type;
    private boolean touched;
    private final String name;
    private final Point position;
    private final Rectangle colider;
    private final WarpInfo warpInfo;
    private final Point spawnPosition;
    private final Animation animation;

    public Portal(Animation animation, Type type, String name, boolean intramap, Point position, int targetMapid, String target_name) {
        this.type = type;
        this.name = name;
        this.touched = false;
        this.position = position;
        this.animation = animation;
        this.spawnPosition = position.plus(new Point(0 , 90));
        this.warpInfo =  new WarpInfo(targetMapid, intramap, target_name, name);
        Point lt = position.plus(new Point(-25, 100));
        Point rb = position.plus(new Point(25, -25));

        this.colider = new Rectangle(lt, rb);
    }


    public void update(Point playerpos)
    {
        touched = getCollider().contains(playerpos);
    }

    public void draw(Point viewpos, float inter)
    {
        if (animation == null || (type == Type.HIDDEN && !touched))
            return;

        animation.draw(new DrawArgument(position.plus(viewpos)), inter);
    }

    public String getName()
    {
        return name;
    }

    public Type getType()
    {
        return type;
    }

    public Point getPosition()
    {
        return position;
    }


    public Point getSpawnPosition() {
        return spawnPosition;
    }

    public WarpInfo getWarpInfo()
    {
        return warpInfo;
    }

    @Override
    public Rectangle getCollider() {
        return colider;
    }

    public enum Type
    {
        SPAWN,
        INVISIBLE,
        REGULAR,
        TOUCH,
        TYPE4,
        TYPE5,
        WARP,
        SCRIPTED,
        SCRIPTED_INVISIBLE,
        SCRIPTED_TOUCH,
        HIDDEN,
        SCRIPTED_HIDDEN,
        SPRING1,
        SPRING2,
        TYPE14
    };

    public static Type typeById(int id) {
        return Type.values()[id];
    }

    public static class WarpInfo {
        public final int mapid;
        public final String name;
        public final String toname;
        public final boolean valid;
        public final boolean intramap;

        public WarpInfo(int mapid, boolean intramap, String target_name, String name) {
            valid = mapid < 999999999;
            this.mapid = mapid;
            this.intramap = intramap;
            this.toname = target_name;
            this.name = name;
        }

        public WarpInfo() {
            this(999999999, false, "", "");
        }
    }
}

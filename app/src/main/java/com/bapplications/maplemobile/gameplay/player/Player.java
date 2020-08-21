package com.bapplications.maplemobile.gameplay.player;

import com.bapplications.maplemobile.gameplay.map.Layer;
import com.bapplications.maplemobile.gameplay.physics.Physics;
import com.bapplications.maplemobile.opengl.utils.Point;

public class Player extends Char {

    public Player(CharEntry entry) {
        super(entry.id, new CharLook(entry.look), entry.stats.name);
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

    public void update(Physics physics, int deltatime) {

    }
}

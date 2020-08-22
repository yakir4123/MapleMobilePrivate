package com.bapplications.maplemobile.gameplay.player;

import android.util.Log;

import com.bapplications.maplemobile.gameplay.map.Layer;
import com.bapplications.maplemobile.gameplay.map.MapObject;
import com.bapplications.maplemobile.gameplay.physics.Physics;
import com.bapplications.maplemobile.opengl.utils.DrawArgument;
import com.bapplications.maplemobile.opengl.utils.Point;

public class Char extends MapObject {

    private final CharLook look;
    private final CharLook look_preview;
    protected State state;

    public static void init() {
        CharLook.init();
    }
    protected void draw(Point viewpos, float alpha) {
        Point absp = phobj.getAbsolute(viewpos, alpha);
//        Log.d("pos::", ""+viewpos.plus(absp));

//        effects.drawbelow(absp, alpha);

//        Color color;

//        if (invincible)
//        {
//            float phi = invincible.alpha() * 30;
//            float rgb = 0.9f - 0.5f * std::abs(std::sinf(phi));
//
//            color = Color(rgb, rgb, rgb, 1.0f);
//        }
//        else
//        {
//            color = Color::Code::CWHITE;
//        }

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

    protected void setState(State state) {
        this.state = state;

        Stance.Id stance = Stance.byState(state);
        look.setStance(stance);

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

    protected Char(int o, CharLook lk, String name) {
        super(o);
        look = lk;
        look_preview = lk;
//        namelabel(Text(Text::Font::A13M, Text::Alignment::CENTER, Color::Name::
//        WHITE, Text::Background::NAMETAG, name));
    }

    public boolean update(Physics physics, float speed, int deltaTime){
        short stancespeed = 0;

        if (speed >= 1.0f / deltaTime)
            stancespeed = (short)(deltaTime * speed);
        return look.update(stancespeed);
    }

    public Layer getLayer() {
        return Layer.byValue(isClimbing() ? 7 : phobj.fhlayer);
    }

    private boolean isClimbing() {
        return state == State.LADDER || state == State.ROPE;
    }

}

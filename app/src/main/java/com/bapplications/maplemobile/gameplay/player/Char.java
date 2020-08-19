package com.bapplications.maplemobile.gameplay.player;

import com.bapplications.maplemobile.gameplay.map.Layer;
import com.bapplications.maplemobile.gameplay.map.MapObject;
import com.bapplications.maplemobile.opengl.utils.Color;
import com.bapplications.maplemobile.opengl.utils.DrawArgument;
import com.bapplications.maplemobile.opengl.utils.Point;

public class Char extends MapObject {

    private final CharLook look;
    private final CharLook look_preview;
    private State state;

    public static void init() {
        CharLook.init();
    }
    protected void draw(Point viewpos, float alpha) {
        Point absp = phobj.getAbsolute(viewpos, alpha);

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

    // Player states which determine animation and state
    // Values are used in movement packets (Add one if facing left)
    public enum State
    {
        WALK (2),
        STAND (4),
        FALL (6),
        ALERT (8),
        PRONE (10),
        SWIM (12),
        LADDER (14),
        ROPE (16),
        DIED (18),
        SIT (20);

        private final byte val;

        State(int val) {
            this.val = (byte) val;
        }
    }

    protected Char(int o, CharLook lk, String name) {
        super(o);
        look = lk;
        look_preview = lk;
//        namelabel(Text(Text::Font::A13M, Text::Alignment::CENTER, Color::Name::
//        WHITE, Text::Background::NAMETAG, name));
    }


    public Layer getLayer() {
        return Layer.byValue(isClimbing() ? 7 : phobj.fhlayer);
    }

    private boolean isClimbing() {
        return state == State.LADDER || state == State.ROPE;
    }

}

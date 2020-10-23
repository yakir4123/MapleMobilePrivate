package com.bapplications.maplemobile.gameplay.player.look;

import com.bapplications.maplemobile.R;

public enum Expression {
    DEFAULT (0),
    BLINK (0),
    HIT (0),
    SMILE (R.drawable.face_smile),
    TROUBLED (R.drawable.face_troubled),
    CRY (R.drawable.face_cry),
    ANGRY (R.drawable.face_angry),
    BEWILDERED (R.drawable.face_bewildered),
    STUNNED (R.drawable.face_stunned),
    BLAZE (R.drawable.face_blaze),
    BOWING (R.drawable.face_bowing),
    CHEERS (R.drawable.face_cheers),
    CHU (R.drawable.face_chu),
    DAM (R.drawable.face_dam),
    DESPAIR (R.drawable.face_despair),
    GLITTER (R.drawable.face_glitter),
    HOT (R.drawable.face_hot),
    HUM (R.drawable.face_hum),
    LOVE (R.drawable.face_love),
    OOPS (R.drawable.face_oops),
    PAIN (R.drawable.face_pain),
    SHINE (R.drawable.face_shine),
    VOMIT (R.drawable.face_vomit),
    WINK (R.drawable.face_wink);

    public static final int TIME = 2500;
    int resourceId;
    Expression(int resourceId){
        this.resourceId = resourceId;
    }

    public int getResource() {
        return resourceId;
    }
}

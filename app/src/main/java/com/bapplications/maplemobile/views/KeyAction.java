package com.bapplications.maplemobile.views;

public enum KeyAction {
    LEFT_ARROW_KEY (Type.CONTINUES_CLICK),
    RIGHT_ARROW_KEY (Type.CONTINUES_CLICK),
    UP_ARROW_KEY (Type.CONTINUES_CLICK),
    DOWN_ARROW_KEY (Type.CONTINUES_CLICK),
    JUMP_KEY (Type.SINGLE_CLICK);

    private final Type type;

    KeyAction(Type type){
        this.type = type;
    }

    public Type getType(){
        return type;
    }

    public enum  Type {
        SINGLE_CLICK,
        CONTINUES_CLICK,
        LONG_CLICK
    }
}

package com.bapplications.maplemobile.input;

public class InputAction {
    public static InputAction UP_ARROW_KEY = new InputAction(Type.CONTINUES_CLICK);
    public static InputAction LEFT_ARROW_KEY = new InputAction(Type.CONTINUES_CLICK);
    public static InputAction DOWN_ARROW_KEY = new InputAction(Type.CONTINUES_CLICK);
    public static InputAction RIGHT_ARROW_KEY = new InputAction(Type.CONTINUES_CLICK);
    public static InputAction JUMP_KEY = new InputAction(Type.SINGLE_CLICK);

    private final Type type;

    InputAction(Type type){
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

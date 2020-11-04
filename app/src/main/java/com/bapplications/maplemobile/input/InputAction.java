package com.bapplications.maplemobile.input;


import org.jetbrains.annotations.Nullable;

public class InputAction {

    public final Key key;

    public enum Key {
        UP,
        DOWN,
        LEFT,
        RIGHT,
        JUMP,
        EXPRESSION;
    }

    public static InputAction UP_ARROW_KEY = new InputAction(Key.UP, Type.CONTINUES_CLICK);
    public static InputAction LEFT_ARROW_KEY = new InputAction(Key.LEFT, Type.CONTINUES_CLICK);
    public static InputAction DOWN_ARROW_KEY = new InputAction(Key.DOWN, Type.CONTINUES_CLICK);
    public static InputAction RIGHT_ARROW_KEY = new InputAction(Key.RIGHT, Type.CONTINUES_CLICK);
    public static InputAction JUMP_KEY = new InputAction(Key.JUMP, Type.SINGLE_CLICK);

    public static InputAction byKey(Key key) {
        switch (key){
            case UP:
                return UP_ARROW_KEY;
            case DOWN:
                return DOWN_ARROW_KEY;
            case LEFT:
                return LEFT_ARROW_KEY;
            case RIGHT:
                return RIGHT_ARROW_KEY;
            case JUMP:
                return JUMP_KEY;
        }
        return null;
    }

    private final Type type;

    InputAction(Key key, Type type){
        this.key = key;
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

package com.bapplications.maplemobile.gameplay.map;

public enum Layer {
    ZERO,
    ONE,
    TWO,
    THREE,
    FOUR,
    FIVE,
    SIX,
    SEVEN;

    private static Layer[] map = new Layer[Layer.values().length];
    static {
        for (Layer layer: Layer.values()){
            map[layer.ordinal()] = layer;
        }
    }
    public static Layer byValue(int ord) {
        return map[ord];
    }


}

package com.bapplications.maplemobile.constatns;

import com.bapplications.maplemobile.pkgnx.EagerNXFile;
import com.bapplications.maplemobile.pkgnx.LazyNXFile;
import com.bapplications.maplemobile.pkgnx.NXFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Loaded {

    private static Map<String, NXFile> files = new HashMap<>();

    public static int SCREEN_WIDTH = -1;
    public static int SCREEN_HEIGHT = -1;
    public static float SCREEN_RATIO = -1;


    public static void loadFile(String key, String path) throws IOException {
        files.put(key, new LazyNXFile(path));
    }

    public static NXFile getFile(String key) {
        return files.get(key);
    }
}

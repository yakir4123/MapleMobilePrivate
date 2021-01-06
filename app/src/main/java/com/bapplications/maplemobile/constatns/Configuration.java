package com.bapplications.maplemobile.constatns;

public class Configuration {

    public static final boolean SHOW_FH = false;
    public static final boolean SHOW_MOBS_RECT = false;
    public static final boolean SHOW_DROPS_RECT = false;
    public static final boolean SHOW_PLAYER_RECT = false;

    public static final String HOST = "10.147.20.164"; //"192.168.193.164";
    public static final String FILES_HOST = "https://gitlab.com/nilnil47/MapleMobileAssets/-/raw/master/";
    public static final int PORT = 50051;
    public static final int UPDATE_DIFF_TIME = 1000;

    //    public static final int START_MAP = 105090900; // balrog map
    public static final int START_MAP = 100000000;//100000000; // henesys 104020000
    public static final int OFFSETX = 0;
    public static final byte MS_PER_UPDATE = 16;
    public static final byte MIN_DIST_UPDATE = 30;

    public static String WZ_DIRECTORY = "";
    public static String CACHE_DIRECTORY = "";
    public static int INVENTORY_MAX_SLOTS = 72;
}

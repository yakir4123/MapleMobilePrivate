package com.bapplications.maplemobile;

public class StaticUtils {


    public static float lerp(float first, float second, float alpha)
    {
        return alpha <= 0.0f ? first
                : alpha >= 1.0f ? second
                : first == second ? first
                : ((1.0f - alpha) * first + alpha * second);
    }

    public static String extendId(int id, int length) {
        StringBuilder strid = new StringBuilder("" + id);
        for(int i = 0; strid.length() < length; i++)
            strid.insert(0, '0');
        return strid.toString();
    }

    public static int orDefault(String number, int def) {
        try
        {
            return Integer.parseInt(number);
        }
        catch (NumberFormatException ex)
        {
            return def;
        }
    }
}

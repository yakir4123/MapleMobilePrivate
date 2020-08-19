package com.bapplications.maplemobile.opengl.utils;

public class TimedBool {

    private long last;
    private long delay;
    private boolean value;
    
    public TimedBool()
    {
        value = false;
        delay = 0;
        last = 0;
    }

    public boolean isTrue()
    {
        return value;
    }

    public void set_for(long millis)
    {
        last = millis;
        delay = millis;
        value = true;
    }

    public void update(int deltatime)
    {
        if (value)
        {
            if (deltatime >= delay)
            {
                value = false;
                delay = 0;
            }
            else
            {
                delay -= deltatime;
            }
        }
    }

    public void set (boolean b)
    {
        value = b;
        delay = 0;
        last = 0;
    }

    public boolean equals (boolean b)
    {
        return value == b;
    }

    public boolean notEquals (boolean b)
    {
        return value != b;
    }

    public float alpha()
    {
        return 1.0f - (float)(delay) / last;
    }
}

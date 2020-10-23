package com.bapplications.maplemobile.utils;

import java.util.function.BinaryOperator;

public class Nominal<T> {
    private BinaryOperator<T> plusOP;
    T now;
    T before;
    float threshold;

    public Nominal(){
        plusOP = new BinaryOperator<T>() {
            public Object apply(Object t1, Object t2) {
                return ((Number)t1).doubleValue() + ((Number)t2).doubleValue();
            }
        };
    }

    public boolean equals(Object value){
        return now == value;
    }

    public void set(T value) {
        now = value;
        before = value;
    }

    public T get() {
        return now;
    }

    public T get(float alpha)
    {
        return alpha >= threshold ? now : before;
    }

    public Object plus(T value) {
        Number res = (Number) plusOP.apply(now, value);
        if (now instanceof Byte)
            return res.byteValue();
        if (now instanceof Short)
            return res.shortValue();
        if (now instanceof Integer)
            return res.intValue();
        if (now instanceof Long)
            return res.longValue();
        if (now instanceof Float)
            return res.byteValue();
        return res.doubleValue();
    }

    public void next(T value, float thrs)
    {
        before = now;
        now = value;
        threshold = thrs;
    }

    public void normalize() {
        before = now;
    }
}

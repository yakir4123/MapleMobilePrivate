package com.bapplications.maplemobile.utils;

import com.bapplications.maplemobile.utils.StaticUtils;

public class Linear {

    private float now;
    private float before;

    public Linear(){
        now = 0;
        before = 0;
    }

    public void set(float value){
        before = now;
        now = value;
    }

    public float get() {
        return now;
    }

    public float get(float alpha)
    {
        return StaticUtils.lerp(before, now, alpha);
    }

//    public Linear plus(float value){
//        before = now;
//        now += value;
//        return this;
//    }

    public float plus(float value){
        return now  + value;
    }


    public void setPlus(float value){
        before = now;
        now += value;
    }

    public void setMinus(float value){
        setPlus(-value);
    }

    public float last() {
        return before;
    }

    public boolean normalized() {
        return before == now;
    }

//    public Linear minus(float value) {
//        before = now;
//        now -= value;
//        return this;
//    }

    public float minus(float value) {
        return now - value;
    }

    public void normalize() {
        before = now;
    }
}

package com.bapplications.maplemobile.utils;

public class BoolPair<T> {


    T onFalse;
    T onTrue;

    public void setOnFalse(T v){
        onFalse = v;
    }

    public void setOnTrue(T v){
        onTrue = v;
    }

    public T get(boolean bool){
        if(bool)
            return onTrue;
        return onFalse;
    }
}

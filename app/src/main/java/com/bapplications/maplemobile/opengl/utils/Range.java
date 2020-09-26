package com.bapplications.maplemobile.opengl.utils;

public class Range<T extends Comparable<T>> {

    private T upper;
    private T lower;

    public Range(T first, T second) {
        if (first.compareTo(second) <= 0) {
            lower = first;
            upper = second;
        } else {
            lower = second;
            upper = first;
        }
    }

    public T getLower() {
        return lower;
    }

    public T getUpper() {
        return upper;
    }

    public boolean intersect(Range<T> v) {
        return contains(v.lower) || contains(v.upper) || v.contains(lower) || v.contains(upper);
    }

    public boolean contains(T v) {
        return v.compareTo(lower) >= 0 && v.compareTo(upper) < 0;
    }

    public boolean isDot() {
        return getLower().equals(getUpper());
    }
}

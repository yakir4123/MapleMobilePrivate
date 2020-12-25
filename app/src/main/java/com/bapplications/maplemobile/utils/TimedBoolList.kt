package com.bapplications.maplemobile.utils

class TimedBoolList<T> : HashMap<T, TimedBool>() {

    fun update(deltaTime: Int) {
        values.forEach{it.update(deltaTime)}
        values.removeIf{!it.isTrue}
    }
}
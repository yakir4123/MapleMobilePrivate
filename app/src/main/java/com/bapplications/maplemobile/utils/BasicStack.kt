package com.bapplications.maplemobile.utils

import java.lang.IndexOutOfBoundsException
import kotlin.collections.ArrayList

/**
 *  Stack and arraylist functionality seems a bit heavy for what I needed
 *  and I got unnecessary overhead so I implement a simple basic stack
 */
class BasicStack<T>(initialSize: Int = 10) {
    private val collection: ArrayList<T> = ArrayList(initialSize)
    var size: Int = 0

    fun push(element: T) {
        try {
            collection[size] = element
        } catch (e: IndexOutOfBoundsException) {
            collection.add(element)
        }
        size++
    }

    fun pop() : T {
        return collection[--size]
    }

    fun clear() {
        size = 0
    }

    fun isEmpty() = size == 0

}
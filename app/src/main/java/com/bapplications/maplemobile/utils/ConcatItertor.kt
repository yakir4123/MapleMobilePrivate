package com.bapplications.maplemobile.utils

class ConcatIterator<T>(iterator: Iterator<T>) : Iterator<T> {
    private val store = ArrayDeque<Iterator<T>>()

    constructor(collection: Collection<T>) : this(collection.iterator())

    init {
        if (iterator.hasNext())
            store.add(iterator)
    }

    override fun hasNext(): Boolean = when {
        store.isEmpty() -> false
        else -> store.first().hasNext()
    }

    override fun next(): T {
        val t = store.first().next()

        if (!store.first().hasNext())
            store.removeFirst()

        return t
    }

    operator fun plus(iterator: Iterator<T>): ConcatIterator<T> {
        if (iterator.hasNext())
            store.add(iterator)
        return this
    }

}

operator fun <T> Iterator<T>.plus(iterator: Iterator<T>): ConcatIterator<T> =
        when {
            this is ConcatIterator<T> -> this.plus(iterator)
            iterator is ConcatIterator<T> -> iterator.plus(this)
            else -> ConcatIterator(this).plus(iterator)
        }
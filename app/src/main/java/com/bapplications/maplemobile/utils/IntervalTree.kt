package com.bapplications.maplemobile.utils

import com.bapplications.maplemobile.gameplay.GameEngine
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

class IntervalTree(ranges: Collection<Range>, xrange: Range) : Collection<Range> {

    val x: Float = xrange.center
    val cutX = mutableListOf<Range>()

    val minMaxTreeRange: Range // range of the ranges of the total tree
    val minMaxNodeRange: Range // range of the ranges of this node

    var leftTree: IntervalTree?
    var rightTree: IntervalTree?

    companion object {
        @JvmStatic
        private val stack = BasicStack<IntervalTree>(25)
    }


    init {
        var minNode = Float.MAX_VALUE
        var minTree = Float.MAX_VALUE
        var maxNode = Float.MIN_VALUE
        var maxTree = Float.MIN_VALUE
        val leftTo = mutableListOf<Range>()
        val rightTo = mutableListOf<Range>()
        for(range in ranges) {
            if(range.lower < minTree) minTree = range.lower
            if(range.upper > maxTree) maxTree = range.upper
            when {
                range < x -> {
                    leftTo.add(range)
                }
                range > x -> {
                    rightTo.add(range)
                }
                else -> {
                    cutX.add(range)
                    if(range.lower < minNode) {
                        minNode = range.lower
                    }
                    if(range.upper > maxNode) {
                        maxNode = range.upper
                    }
                }
            }
        }
        rightTree = if (rightTo.size == 0) null
                    else IntervalTree(rightTo, Range(x, xrange.upper))
        leftTree = if (leftTo.size == 0) null
                    else IntervalTree(leftTo, Range(xrange.lower, x))

        shrinkTree()
        minMaxNodeRange = Range(minNode, maxNode)
        minMaxTreeRange = Range(minTree, maxTree)
    }

    private fun shrinkTree() {
        if(leftTree?.cutX?.size == 0) {
            if(leftTree?.leftTree == null)
                leftTree = leftTree?.rightTree
            else if(leftTree?.rightTree == null)
                leftTree = leftTree?.leftTree
        }
        if(rightTree?.cutX?.size == 0) {
            if(rightTree?.leftTree == null)
                rightTree = rightTree?.rightTree
            else if(rightTree?.rightTree == null)
                rightTree = rightTree?.leftTree
        }
        leftTree?.shrinkTree()
        rightTree?.shrinkTree()
    }

    /**
     * Stack implementation is twice better than the recursion implementation
     */
    fun getRanges(range: Range): List<Range> {
        val res: MutableList<Range> = ArrayList()
        // From profiling this stack initialization, pop and push is more than 1.5%
        // so I implement it using array list with less than 1%
        stack.push(this)

        while(!stack.isEmpty()){
            val node = stack.pop()
            if (range.intersect(node.minMaxNodeRange)) {
                res.addAll(node.cutX)
            }

            if(node.rightTree?.minMaxTreeRange?.intersect(range) == true) {
                node.rightTree?.let { stack.push(it) }
            }
            if(node.leftTree?.minMaxTreeRange?.intersect(range) == true) {
                node.leftTree?.let { stack.push(it) }
            }
        }
        stack.clear()
        return res
    }

    private fun isNodeEmpty(): Boolean {
        return minMaxNodeRange.lower == Float.MAX_VALUE // check one side is enough
    }

    override val size: Int
        get() = 1 + (leftTree?.size ?: 0) + (rightTree?.size ?: 0)

    override fun contains(element: Range): Boolean {
        return when {
            element < x -> {
                leftTree?.contains(element) ?: false
            }
            element > x -> {
                rightTree?.contains(element) ?: false
            }
            else -> {
                cutX.contains(element)
            }
        }
    }

    override fun containsAll(elements: Collection<Range>): Boolean {
        for(element in elements) {
            if(!contains(element))
                return false
        }
        return true
    }

    override fun isEmpty(): Boolean = leftTree == null && rightTree == null && cutX.isEmpty()

    override fun iterator(): Iterator<Range> {
        return (leftTree?.iterator() ?: listOf<Range>().iterator()) + ConcatIterator(cutX.iterator()) + (rightTree?.iterator() ?: listOf<Range>().iterator())
    }


}

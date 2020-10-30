package com.bapplications.maplemobile.utils

class TwoDIntervalTree (objs: List<Point.TwoDPolygon>, dimension: Point.TwoDPolygon) : Collection<Point.TwoDPolygon> {

    private val heightRangeToPolygon: Map<Range, Point.TwoDPolygon> = objs.map { it.height to it }.toMap()
    private val widthRangeToPolygon: Map<Range, Point.TwoDPolygon> = objs.map { it.width to it }.toMap()

    private val yIntervalTree = IntervalTree(heightRangeToPolygon.keys, dimension.height)
    private val xIntervalTree = IntervalTree(widthRangeToPolygon.keys, dimension.width)

    fun getRectangles(rect: Rectangle) : Set<Point.TwoDPolygon> {
        val intersectMap = mutableMapOf<Point.TwoDPolygon, Int>()
        val xranges = xIntervalTree.getRanges(rect.width)
        val yranges = yIntervalTree.getRanges(rect.height)

        xranges.forEach { intersectMap[widthRangeToPolygon[it]!!] = 1 }
        yranges.forEach { intersectMap[heightRangeToPolygon[it]!!] = intersectMap[heightRangeToPolygon[it]!!]?.plus(1) ?: 1}

        return intersectMap.filterValues { it == 2 }.keys
    }

    override val size: Int
        get() = widthRangeToPolygon.size

    override fun contains(element: Point.TwoDPolygon): Boolean =
            widthRangeToPolygon.values.contains(element)

    override fun containsAll(elements: Collection<Point.TwoDPolygon>): Boolean =
            widthRangeToPolygon.values.containsAll(elements)

    override fun isEmpty(): Boolean =
        widthRangeToPolygon.values.isEmpty()


    override fun iterator(): Iterator<Point.TwoDPolygon> =
            widthRangeToPolygon.values.iterator()

}
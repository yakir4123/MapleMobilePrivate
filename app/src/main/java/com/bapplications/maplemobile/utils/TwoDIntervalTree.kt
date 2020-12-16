package com.bapplications.maplemobile.utils

class TwoDIntervalTree (objs: List<Point.TwoDPolygon>, dimension: Point.TwoDPolygon) : Collection<Point.TwoDPolygon> {

    private val onWidth = dimension.width.size() > dimension.height.size()
    private val intervalTree: IntervalTree
    private val rangeToPolygon: Map<Range, Point.TwoDPolygon>


    init {

        intervalTree = if(onWidth) {
            rangeToPolygon = objs.map { it.width to it }.toMap()
            IntervalTree(rangeToPolygon.keys, dimension.width)
        } else {
            rangeToPolygon = objs.map { it.height to it }.toMap()
            IntervalTree(rangeToPolygon.keys, dimension.height)
        }
    }

    fun getRectangles(rect: Rectangle) : Collection<Point.TwoDPolygon> {
        val firstAxisRange = if(onWidth) rect.width else rect.height
        val secondAxisRange = if(onWidth) rect.height else rect.width

        val intersectRanges = intervalTree.getRanges(firstAxisRange)

        val res = ArrayList<Point.TwoDPolygon>()
        for((key, value) in rangeToPolygon) {
            if(intersectRanges.contains(key)
                    && secondAxisRange.intersect(if(onWidth) value.height else value.width)) {
                res.add(value)
            }
        }
        return res

        // the method above to get the rectangles is better by 25%! than using those list functions
//        return rangeToPolygon.filterKeys{intersectRanges.contains(it)}
//                .filterValues { secondAxisRange.intersect(if(onWidth) it.height else it.width) }
//                .values
    }

    override val size: Int
        get() = rangeToPolygon.size

    override fun contains(element: Point.TwoDPolygon): Boolean =
            rangeToPolygon.values.contains(element)

    override fun containsAll(elements: Collection<Point.TwoDPolygon>): Boolean =
            rangeToPolygon.values.containsAll(elements)

    override fun isEmpty(): Boolean =
            rangeToPolygon.values.isEmpty()


    override fun iterator(): Iterator<Point.TwoDPolygon> =
            rangeToPolygon.values.iterator()
}
package com.bapplications.maplemobile.ui.custom_views

import android.R.attr.columnWidth
import android.content.Context
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.core.view.GestureDetectorCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bapplications.maplemobile.R
import com.bapplications.maplemobile.utils.StaticUtils
import kotlin.math.max


class InventoryView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr),
    GestureDetector.OnGestureListener {

    private var colWidth : Int = 0

    init {
        if (attrs != null) {
            val attrsArray = intArrayOf(columnWidth)
            val array = context.obtainStyledAttributes(attrs, attrsArray)
            colWidth = array.getDimensionPixelSize(0, -1)
            array.recycle()
        }
        layoutManager = GridLayoutManager(getContext(), 1)
    }


    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        super.onMeasure(widthSpec, heightSpec)
        if (colWidth > 0) {
            val spacing = context?.resources?.getDimension(R.dimen.recycler_equal_spacing) ?: 0f
            val spanCount = max(1f, (measuredWidth / (colWidth + spacing / 2f))).toInt()
            (layoutManager as GridLayoutManager).spanCount = spanCount
        }
    }

    private var mDetector: GestureDetectorCompat = GestureDetectorCompat(context, this)
    private val MIN_DISTANCE = 25000
    private var onHorizonSwipe: ((isLeft: Boolean) -> Unit)? = null

    fun setOnHorizonSwipe(onHorizonSwipe: (isLeft: Boolean) -> Unit) {
        this.onHorizonSwipe = onHorizonSwipe
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (mDetector.onTouchEvent(event)) {
            true
        } else {
            super.onTouchEvent(event)
        }
    }

    override fun onFling(p0: MotionEvent?, p1: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
        try {
            val direcX = p1?.x?.minus(p0?.x!!)?.times(kotlin.math.abs(velocityX))!!
            val direcY = p1.y.minus(p0?.y!!).times(kotlin.math.abs(velocityY))
            if (kotlin.math.abs(direcX) > kotlin.math.abs(direcY) * 3 && kotlin.math.abs(direcX) > MIN_DISTANCE) {
                onHorizonSwipe?.let { it(direcX > MIN_DISTANCE) }
                return true
            }
        } catch (e: NullPointerException) {}
        return false;
    }

    override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, dx: Float, dy: Float): Boolean {
        return false
    }

    override fun onDown(p0: MotionEvent?): Boolean {
        return false
    }

    override fun onShowPress(p0: MotionEvent?) {
    }

    override fun onSingleTapUp(p0: MotionEvent?): Boolean {
        return false
    }

    override fun onLongPress(p0: MotionEvent?) {
    }


}
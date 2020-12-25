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
) : RecyclerView(context, attrs, defStyleAttr) {

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

}
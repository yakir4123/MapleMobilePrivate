package com.bapplications.maplemobile.ui.custom_views

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayout

class WrapHeightTabLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : TabLayout(context, attrs, defStyleAttr) {
    private var tabStrip: ViewGroup = getChildAt(0) as ViewGroup

    init {
        tabStrip.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, tabStrip.measuredHeight)
        // Force the height of this ViewGroup to be the same height of the tabStrip
        setMeasuredDimension(measuredWidth, tabStrip.measuredHeight)
    }
}
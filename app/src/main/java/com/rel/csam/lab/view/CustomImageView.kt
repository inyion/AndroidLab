package com.rel.csam.lab.view

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView

/**
 * Created by leechansaem on 2016. 11. 3..
 */
class CustomImageView : ImageView {
    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val width = measuredWidth
        setMeasuredDimension(width, width)

//        val d = drawable
//
//        if (d != null) {
//            val width = MeasureSpec.getSize(widthMeasureSpec)
//            val height = Math.ceil((width.toFloat() * d.intrinsicHeight.toFloat() / d.intrinsicWidth.toFloat()).toDouble()).toInt()
//            setMeasuredDimension(width, height)
//        } else {
//            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
//        }
    }
}

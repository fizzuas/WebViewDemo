package com.kydw.webviewdemo.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import androidx.core.widget.NestedScrollView

 class MyScrollView : NestedScrollView {
    private var mLastX = 0
    private var mLastY = 0

    constructor(context: Context) : super(context) {}
    constructor(
        context: Context,
        attrs: AttributeSet?
    ) : super(context, attrs) {
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        var intercepted = false
        val x = event.x.toInt()
        val y = event.y.toInt()
        Log.e("oyx", event.action.toString() + "")
        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_UP -> {
                intercepted = false
            }
            MotionEvent.ACTION_MOVE -> intercepted = isParentNeed(x, y)
            else -> {
            }
        }
        mLastX = x // 用于判断是否拦截的条件
        mLastY = y // 用于判断是否拦截的条件
        return intercepted
    }

    private fun isParentNeed(x: Int, y: Int): Boolean {
        return y > 500
    }
}


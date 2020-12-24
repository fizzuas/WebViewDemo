package com.kydw.webviewdemo.util

import android.app.Activity
import android.app.Application
import android.content.Context
import android.graphics.Point
import android.view.Display
import android.view.WindowManager

/**
 * Get Display
 *
 * @param context Context for get WindowManager
 * @return Display
 */
fun getDisplay(context: Context): Display? {
    val wm: WindowManager?
    wm = if (context is Activity) {
        context.windowManager
    } else {
        context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }
    return wm?.defaultDisplay
}

fun getScreenRealWidth(context: Context): Int {
    val display = getDisplay(context) ?: return 0
    val outSize = Point()
    display.getRealSize(outSize)
    return outSize.x
}

fun getScreenRealHeight(context: Context): Int {
    val display = getDisplay(context) ?: return 0
    val outSize = Point()
    display.getRealSize(outSize)
    return outSize.y
}

fun statueHeight(applicationContext: Application): Int {
    var height = 0
    val resourceId =
        applicationContext.resources.getIdentifier("status_bar_height", "dimen", "android")
    if (resourceId > 0) {
        height = applicationContext.resources.getDimensionPixelSize(resourceId)
    }
    return height
}

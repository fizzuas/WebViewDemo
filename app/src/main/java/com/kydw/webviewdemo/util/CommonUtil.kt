package com.kydw.webviewdemo.util

import android.content.Context
import java.io.File
import java.nio.charset.Charset



fun appendFile(text: String, destFile: String, context: Context) {
    if (PermissionUtil.hasRequiredPermissions(context)) {
        val f = File(destFile)
        if (!f.exists()) {
            f.createNewFile()
        }
        f.appendText(text, Charset.defaultCharset())
    }
}


fun appName(context: Context): String {
    return context.packageManager.getApplicationLabel(context.applicationInfo).toString()
}


/**
 * 返回当前程序版本名
 */
fun getAppVersionName(context: Context): String? {
    var versionName = ""
    // ---get the package info---
    val pm = context.packageManager
    val pi = pm.getPackageInfo(context.packageName, 0)
    versionName = pi.versionName
    return versionName
}


package com.kydw.webviewdemo

import android.app.Application
import com.tencent.bugly.crashreport.CrashReport

/**
 *@Author oyx
 *@date 2021/3/19 11:43
 *@description
 */
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        CrashReport.initCrashReport(applicationContext, "2dad152548", true)
    }
}



package com.kydw.webviewdemo.shellutil

/**
 *@Author oyx
 *@date 2020/12/11 15:35
 *@description
 */

class CMD {
    companion object {
        val REBOOT = "reboot"
        val WIFI_ON = "svc wifi enable"
        val WIFI_OFF = "svc wifi disable"
        val DATE_ON = "svc data enable"
        val DATE_OFF = "svc data disable"
        val AIRPLANE_MODE_ON = """
        settings put global airplane_mode_on 1
        am broadcast -a android.intent.action.AIRPLANE_MODE --ez state true
        """.trimIndent()
        val AIRPLANE_MODE_OFF = """
        settings put global airplane_mode_on 0
        am broadcast -a android.intent.action.AIRPLANE_MODE --ez state false
        """.trimIndent()

        val IP="ifconfig"
    }

}
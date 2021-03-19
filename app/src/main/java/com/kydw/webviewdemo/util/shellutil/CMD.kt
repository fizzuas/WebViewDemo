package com.kydw.webviewdemo.util.shellutil


class CMD {
    companion object {
        val REBOOT = "reboot"
        val WIFI_ON = "svc wifi enable"
        val WIFI_OFF = "svc wifi disable"
        val DATA_ON = "svc data enable"
        val DATA_OFF = "svc data disable"
        val AIRPLANE_MODE_ON = """
        settings put global airplane_mode_on 1
        am broadcast -a android.intent.action.AIRPLANE_MODE --ez state true
        """.trimIndent()
        val AIRPLANE_MODE_OFF = """
        settings put global airplane_mode_on 0
        am broadcast -a android.intent.action.AIRPLANE_MODE --ez state false
        """.trimIndent()

        val IP="ifconfig"

        //方便切换 WIFE 和 DATA
        val CMD_1= WIFI_ON
        val CMD_2= DATA_OFF

    }

}
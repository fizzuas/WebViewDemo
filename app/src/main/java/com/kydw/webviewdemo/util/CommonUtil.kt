package com.kydw.webviewdemo.util

import java.io.File
import java.nio.charset.Charset

/**
 *@Author oyx
 *@date 2020/12/16 14:19
 *@description
 */

fun appendFile(text: String, destFile: String) {
    val f = File(destFile)
    if (!f.exists()) {
        f.createNewFile()
    }
    f.appendText(text, Charset.defaultCharset())
}

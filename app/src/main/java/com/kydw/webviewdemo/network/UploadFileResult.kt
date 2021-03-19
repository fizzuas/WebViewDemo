package com.kydw.webviewdemo.network

data class UploadFileResult(
        val Code: Int,
        val Detail: String?,
        val Message: String?,
        val Value: UploadFileResultValue?
)

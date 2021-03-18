package com.sogou.activity.network

data class UploadFileResult(
        val Code: Int,
        val Detail: String?,
        val Message: String?,
        val Value: UploadFileResultValue?
)

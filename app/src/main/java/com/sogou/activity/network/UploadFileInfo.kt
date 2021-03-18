package com.sogou.activity.network

data class UploadFileInfo(
        val FileTypeEnum: Int? = null,
        val OriginalName: String? = null,
        val Suffix: String? = null,
        val Size: Int? = null,
        val FileAddress: String? = null,
        var Versions: Double? = 0.0,
        val Remark: String? = null
)
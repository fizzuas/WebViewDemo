package com.kydw.webviewdemo.network


import retrofit2.Call
import retrofit2.http.*


interface UpdateService {
    @GET("FindJiazhengServiceUpdate")
    fun getUpdateApk(@Query("param") param: String): Call<UploadFileResult>

}
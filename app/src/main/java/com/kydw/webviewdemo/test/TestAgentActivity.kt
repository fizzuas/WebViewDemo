package com.kydw.webviewdemo.test

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.kydw.webviewdemo.R
import com.kydw.webviewdemo.baidu_simplify.MyTag
import com.tencent.smtt.sdk.WebView

class TestAgentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        //获取本地user_agent;

        //获取本地user_agent;
        val userAgentString: String = WebView(this).settings.userAgentString
        //设置user_agent(以asyncHttprequest为例)
        //设置user_agent(以asyncHttprequest为例)
        Log.i(MyTag, "agent=$userAgentString")

    }
}
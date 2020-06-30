package com.kydw.webviewdemo

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
import com.tencent.smtt.sdk.WebChromeClient
import com.tencent.smtt.sdk.WebSettings
import com.tencent.smtt.sdk.WebView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


       webview.webChromeClient=object : WebChromeClient(){
           override fun onReceivedTitle(view: WebView?, title: String?) {
               super.onReceivedTitle(view, title)
               tv.text = title
           }

       }

//        webview.webViewClient=object : WebViewClient(){
//            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
//                view?.loadUrl(url)
//                return true
//            }
//        }


        webview.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                if (event?.action == MotionEvent.ACTION_UP) {
                    webview.requestDisallowInterceptTouchEvent(false)
                } else {
                    webview.requestDisallowInterceptTouchEvent(true)
                }
                return false
            }

        })
        val webSettings=webview.settings

        //设置true,才能让Webivew支持<meta>标签的viewport属性
        webSettings.useWideViewPort=true
        webSettings.loadWithOverviewMode=true
        //设置可以手势支持缩放
        webSettings.setSupportZoom(true)
        webSettings.builtInZoomControls=true
        //设定缩放控件隐藏
        webSettings.displayZoomControls=false
        webview.setInitialScale(100)


//        webSettings.javaScriptEnabled=true
        webview.loadUrl("http://192.168.0.111:9355/上传排行榜.xls.htm")
//        webview.loadUrl("file:///android_asset/web/index_table.html")
    }


    override fun onBackPressed() {
        if(webview.canGoBack()){
            webview.goBack()
        }else{
            super.onBackPressed()
        }

    }
}
package com.kydw.webviewdemo

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File


const val TAG: String = "oyx"

class MainActivity : AppCompatActivity() {
    val obj = InJavaScriptLocalObj()
    val baiduIndexUrl="http://www.baidu.com/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val js = "javascript:document.getElementById('name').value = '\" + 家政"
        webview.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                if (url == null || url.startsWith("http://") || url.startsWith("https://")) {
                    return false
                } else try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    view!!.context.startActivity(intent)
                    return true
                } catch (e: Exception) {
                    Log.i(TAG, "shouldOverrideUrlLoading Exception:$e")
                    return true
                }
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                Log.i(TAG, "onPageStarted")

            }

            override fun onPageFinished(view: WebView?, url: String?) {
                Log.i(TAG, "onPageFinished")



                // 在结束加载网页时会回调
//                val jsGetHtml = "window.java_obj.showSource" +
//                        "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');"
//
//                val jsSetTitleToLocal =
//                    "window.java_obj.setPageTitle" + "(document.getElementsByTagName('title')[0].innerText);"
//                view!!.loadUrl("javascript:" + jsSetTitleToLocal)


                Log.i(TAG, "url="+url)
                if(url.equals(baiduIndexUrl)){
                    //首页，提交表单
                    val jsFormInput = "document.getElementsByName('word')[0].value='家政';" +
                            "var nodes=document.getElementsByTagName('form');" +
                            "var lastNode=nodes[0].lastChild;"
                            "function time(){lastNode.click();} " +
                            "setTimeout(time,5000);"
                    view!!.loadUrl("javascript:" + jsFormInput)
                }else{
                    //Next 页
//                    val  jsNext="function nextClick(){document.getElementsByClassName('nextOnly')[0].click();}"+
//                            "setTimeout(nextClick,2000);"
//
//
//
//                    view!!.loadUrl("javascript:" + jsNext)

                }



//                view!!.loadUrl("javascript:$jsPerformClick")

//                // 获取解析<meta name="share-description" content="获取到的值">
//                view!!.loadUrl(
//                    "javascript:window.java_obj.showDescription("
//                            + "document.querySelector('meta[name=\"share-description\"]').getAttribute('content')"
//                            + ");"
//                )


//                view!!.evaluateJavascript(
//                    "javascript: + window.alert('Js injection success')",
//                    ValueCallback {
//                        Log.i(TAG, it)
//
//                    })
                super.onPageFinished(view, url)


            }

            override fun onReceivedSslError(
                view: WebView?,
                handler: SslErrorHandler?,
                error: SslError?
            ) {
                // let's ignore ssl error
                handler!!.proceed();
            }
        }

        webview.webChromeClient = WebChromeClient()

        webview.addJavascriptInterface(obj, "java_obj")
        setWebView(webview)
        webview.loadUrl("http://www.baidu.com")
    }

    @SuppressLint("ClickableViewAccessibility", "SetJavaScriptEnabled")
    private fun setWebView(wv: android.webkit.WebView) {
        wv.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                if (event?.action == MotionEvent.ACTION_UP) {
                    wv.requestDisallowInterceptTouchEvent(false)
                } else {
                    wv.requestDisallowInterceptTouchEvent(true)
                }
                return false
            }
        })
        val webSettings = wv.settings

        //设置true,才能让Webivew支持<meta>标签的viewport属性,可任意比例缩放
        webSettings.useWideViewPort = true

        webSettings.loadWithOverviewMode = true
        //设置可以手势支持缩放
        webSettings.setSupportZoom(false)

        webSettings.builtInZoomControls = true
        //设定缩放控件隐藏
        webSettings.displayZoomControls = true

//      <meta content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0" name="viewport"/>百度不支持缩放
//        wv.setInitialScale(100)

// 设置是否开启DOM存储API权限，默认false，未开启，设置为true，WebView能够使用DOM storage API
        webSettings.domStorageEnabled = true

        webSettings.javaScriptEnabled = true

        webSettings.userAgentString = "User-Agent:Android"

    }


    override fun onBackPressed() {
        if (webview.canGoBack()) {
            webview.goBack()
        } else {
            super.onBackPressed()
        }

    }
}

class InJavaScriptLocalObj {
    var title = ""

    @JavascriptInterface
    fun showSource(html: String) {
        Log.i(TAG, "====>html_showSource=$html")
        File(Environment.getExternalStorageDirectory().absolutePath + File.separator + "show.html").writeText(
            html
        )


    }


    @JavascriptInterface
    fun showDescription(str: String) {
        Log.i(TAG, "====>html_showDescription=$str")
    }

    @JavascriptInterface
    fun saveHtml(html: String, characterSet: String) {
        Log.i(TAG, "====>saveHtml characterSet$characterSet")
        File(Environment.getExternalStorageDirectory().absolutePath + File.separator + "save.html").writeText(
            html
        )
    }

    @JavascriptInterface
    fun setPageTitle(title: String) {
        Log.i(TAG, "setPageTitle"+title)
        this.title = title
    }
}
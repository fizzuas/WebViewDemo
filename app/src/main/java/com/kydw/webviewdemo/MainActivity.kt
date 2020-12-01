package com.kydw.webviewdemo

import android.annotation.SuppressLint
import android.content.Intent
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
import java.nio.charset.Charset


const val TAG: String = "oyx"

class MainActivity : AppCompatActivity() {
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


            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)

                Log.i(TAG, "onPageFinished")
                // 在结束加载网页时会回调

                val jsGetHtml = "document.execCommand('selectall');" +
                        "var txt;" +
                        "if (window.getSelection) {" +
                        "txt = window.getSelection().toString();" +
                        "} else if (window.document.getSelection) {" +
                        "txt = window.document.getSelection().toString();" +
                        "} else if (window.document.selection) {" +
                        "txt = window.document.selection.createRange().text;" +
                        "}" +
                        "var charactersets = document.characterSet;" +
                        "window.java_obj.saveHtml(txt,charactersets);"

                val jsGetHtml2 = "window.java_obj.showSource" +
                        "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');"

                var jsInject = "var script = document.createElement('script');"
                jsInject += "script.type = 'text/javascript';"
                jsInject += "window.alert('Js injection success')"


                var jsAlertInfo = "var script = document.createElement('script');"
                jsAlertInfo += "script.type = 'text/javascript';"
                jsAlertInfo += "window.alert('window.innerWidth='+window.innerWidth+'," +
                        "document.title='+document.title+" +
                        "',location.host='+location.host)"

                var jsAlertP="window.alert('"
                var jsAlertS="');"

                //要查找DOM树的某个节点，需要从document对象开始查找。最常用的查找是根据ID和Tag Name

                var jsSetInput="document.getElementById('index-kw').defaultValue='家政';"


                var  jsPerformClick="document.getElementById('index-kw').defaultValue;"


                var jsGetNextNode="document.getElementById('index-kw').n)extSibing.click("

                view!!.loadUrl("javascript:"+jsPerformClick+"js"+jsAlertS)
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
        webview.addJavascriptInterface(InJavaScriptLocalObj(), "java_obj")
        setWebView(webview)
//        webview.loadUrl("javascript:javacalljs()")
//        webview.loadUrl("javascript:javacalljswith(\"JAVA调用了JS的有参函数\")")
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
}
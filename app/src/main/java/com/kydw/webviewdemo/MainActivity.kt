package com.kydw.webviewdemo

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.os.*
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.lang.ref.WeakReference
import java.nio.charset.Charset


const val TAG: String = "oyx"

class MainActivity : AppCompatActivity() {
    val handler = MyHandler(this)

    class MyHandler(activity: MainActivity) : Handler() {
        private val mActivity: WeakReference<MainActivity> = WeakReference(activity)

        override fun handleMessage(msg: Message) {
            if (mActivity.get() == null) {
                return
            }
            val activity = mActivity.get()
            when (msg.what) {
                0 -> {
                    activity?.startGet()
                }
                else -> {
                }
            }
        }
    }

    fun startGet() {
        webview.loadUrl("http://www.baidu.com")
    }

    val obj = InJavaScriptLocalObj(this)
    val baiduIndexUrl = "http://www.baidu.com/"

    //    m.51baomu.cn
    val targetSiteKeyInfo = "baike.baidu.com"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
                Log.i(TAG, "onPageStarted = " + url)

            }

            override fun onPageFinished(view: WebView?, url: String?) {
                Log.i(TAG, "onPageFinished = " + url)

                // 在结束加载网页时会回调
//                val jsGetHtml = "window.java_obj.showSource" +
//                        "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');"
//
//                val jsSetTitleToLocal =
//                    "window.java_obj.setPageTitle" + "(document.getElementsByTagName('title')[0].innerText);"
//                view!!.loadUrl("javascript:" + jsSetTitleToLocal)


                if (url.equals(baiduIndexUrl)) {
                    Log.e(TAG, "首页点击")
                    //首页，提交表单
                    val js_form =
                        application.assets.open("js_bd_2second.js").bufferedReader().use {
                            it.readText()
                        }
                    view!!.loadUrl("javascript:$js_form")
                } else if (url!!.contains(targetSiteKeyInfo)) {
                    Log.e(TAG, "目标页加载成功=$url")
                    val js_look = application.assets.open("js_look.js").bufferedReader().use {
                        it.readText()
                    }
                    view!!.loadUrl("javascript:$js_look")
                } else if (url.contains("baidu.com")) {
                    Log.e(TAG, "下一页=$url")
                    //Next 页
                    val js_to_next =
                        application.assets.open("js_to_next.js").bufferedReader().use {
                            it.readText()
                        }
                    view!!.loadUrl("javascript:$js_to_next")
                }
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

class InJavaScriptLocalObj(val context: Context) {

    @JavascriptInterface
    fun showSource(html: String) {
        Log.i(TAG, "====>html_showSource=$html")
        File(Environment.getExternalStorageDirectory().absolutePath + File.separator + "show.html").writeText(
            html
        )
    }

    @JavascriptInterface
    fun saveLog(content: String) {
        Log.i(TAG, "saveLog")
        appendFile(
            content,
            Environment.getExternalStorageDirectory().absolutePath + File.separator + "baidu_dianji.txt"
        )
    }

    @JavascriptInterface
    fun requestFinished() {
        Log.i(TAG, "requestFinished" + (Looper.myLooper() == Looper.getMainLooper()))
        GlobalScope.launch(Dispatchers.Main) {
            Log.i(TAG, "requestFinished" + (Looper.myLooper() == Looper.getMainLooper()))
        }

    }

    @JavascriptInterface
    fun finish() {
        Log.i(TAG, "finish")
        GlobalScope.launch(Dispatchers.Main) {
            delay(5000)
            (context as MainActivity).handler.sendEmptyMessage(0)
        }
    }

}

fun appendFile(text: String, destFile: String) {
    val f = File(destFile)
    if (!f.exists()) {
        f.createNewFile()
    }
    f.appendText(text, Charset.defaultCharset())
}
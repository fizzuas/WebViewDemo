package com.kydw.webviewdemo.baidu_simplify

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri

import android.os.*
import android.util.Log
import android.view.MotionEvent
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.WindowManager
import android.webkit.JavascriptInterface
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.kydw.webviewdemo.R
import com.kydw.webviewdemo.dialog.JAlertDialog
import com.kydw.webviewdemo.util.shellutil.CMD
import com.kydw.webviewdemo.util.shellutil.ShellUtils
import com.kydw.webviewdemo.util.PermissionUtil
import com.kydw.webviewdemo.util.appendFile
import com.tencent.smtt.export.external.interfaces.SslError
import com.tencent.smtt.export.external.interfaces.SslErrorHandler
import com.tencent.smtt.sdk.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import java.io.File
import java.lang.ref.WeakReference


const val TAG: String = "oyx"

class BaiduMWebActivity : AppCompatActivity() {
    val pageCheck =
        "https://wappass.baidu.com/static/captcha/tuxing.html?&ak=248b24c134a6b4f52ee85f8b9577d4a8&backurl=https%3A%2F%2Fm.baidu.com%2Ffrom%3D844b%2Fs%3Fpn%3D10%26usm%3D3%26word%3D%25E9%2592%25A5%25E5%258C%2599%25E6%259C%25BA%26sa%3Dnp%26ms%3D1%26rqid%3D10964520929320459358%26params_ssrt%3Dsmarty&logid=9950648836190522585&signature=819bd5a304bb5a74607e492e26f461b5&timestamp=1608519201"
    lateinit var webview: WebView
    val isRoot = false
    var mCircleCount = 1
    var mCircleIndex = 1

    private var mLoadingDbDialog: JAlertDialog? = null
    private val obj = MInJavaScriptLocalObj(this)
    val baiduIndexUrl = "https://www.baidu.com/"

    //    m.51baomu.cn

    val mKeyWords =
        mutableListOf<Pair<String, String>>()
    var mRequestIndex = 0

    fun indexNext() {
        mRequestIndex++
    }

    val handler = MyHandler(this)

    class MyHandler(activity: BaiduMWebActivity) : Handler() {
        private val mActivity: WeakReference<BaiduMWebActivity> = WeakReference(activity)

        override fun handleMessage(msg: Message) {
            if (mActivity.get() == null) {
                return
            }
            val activity = mActivity.get()
            when (msg.what) {
                0 -> {
                    //次 页not  found，或者满页
                    activity?.indexNext()
                    activity?.request()
                }
                1 -> {
                    activity?.printUserAgent()
                    //单个请求单次访问结束
                    activity?.indexNext()
                    activity?.request()
                }
                else -> {
                }
            }
        }
    }

    fun printUserAgent() {
        Log.i(MyTag, "user-agent=" + webview.settings.userAgentString)
    }

    fun request() {
        webview.loadUrl(pageCheck)
    }

    private fun nextCircle() {
        //切换IP
        if (mLoadingDbDialog == null) {
            mLoadingDbDialog =
                JAlertDialog.Builder(this).setContentView(R.layout.dialog_waitting_fly)
                    .setWidth_Height_dp(300, 120).setCancelable(false)
                    .create()
        }
        mLoadingDbDialog?.show()


        GlobalScope.launch(Dispatchers.IO) {
            val result0 = ShellUtils.execCommand(CMD.IP + " rmnet_data0", isRoot)
            if (result0 != null && result0.successMsg != null) {
                val sucMsg0 = result0.successMsg!!
                Log.i(MyTag, "result0.sucMsg0=" + sucMsg0?.toString() + ", ")
                saveIP(sucMsg0)

            }


            ShellUtils.execCommand(CMD.AIRPLANE_MODE_ON, isRoot)
            delay(1000)
            ShellUtils.execCommand(CMD.AIRPLANE_MODE_OFF, isRoot)

            //关掉飞行时，4G 需要慢慢打开
            delay(10000)
            val result1 = ShellUtils.execCommand(CMD.IP + " rmnet_data0", isRoot)
            if (result1 != null && result1.successMsg != null) {
                Log.i(MyTag, "result1.sucMsg=" + result1.successMsg?.toString())
                appendFile(result1.successMsg + "\n\n",
                    getExternalFilesDir(null)!!.absolutePath + File.separator + "ip.txt",
                    this@BaiduMWebActivity)
            }
            withContext(Dispatchers.Main) {
                mLoadingDbDialog?.dismiss()
                mRequestIndex = 0
                clearCache()
                webview.loadUrl(baiduIndexUrl)
            }
        }
    }

    private fun saveIP(sucMsg0: String) {
        appendFile(sucMsg0,
            getExternalFilesDir(null)!!.absolutePath + File.separator + "ip.txt", this)
    }

    override fun onDestroy() {
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        destroyWebView()
        super.onDestroy()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        PermissionUtil.askForRequiredPermissions(this)

        webview = WebView(applicationContext)
        val lp = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
        webview.layoutParams = lp
        content.addView(webview)

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
                Log.i(TAG, "onPageStarted = $url")
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                Log.i(TAG, "onPageFinished = $url")
                if (url!!.contains("baidu.com")) {
                    Log.e(TAG, "百度搜索页面=$url")
                    if (url.contains("wappass.baidu.com/static/captcha/tuxing")) {
                        //验证码
                        Log.e(MyTag, "发现验证码界面" + url)
                        val jsSwipe =
                            application.assets.open("js_swipe_vc_by_cb.js").bufferedReader().use {
                                it.readText()
                            }
                        view!!.loadUrl("javascript:$jsSwipe")
                    } else {
                        Log.e(MyTag, "发现下一页" + url)
                    }
                }

                super.onPageFinished(view, url)
            }

            override fun onReceivedSslError(
                view: WebView?,
                handler: SslErrorHandler?,
                error: SslError?
            ) {
                // let's ignore ssl error
                handler!!.proceed()
            }
        }
        webview.webChromeClient = WebChromeClient()

        webview.addJavascriptInterface(obj, "java_obj")
        setWebView(webview)
        request()
        webview.keepScreenOn = true


    }

    override fun onResume() {
        super.onResume()
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }


    @SuppressLint("ClickableViewAccessibility", "SetJavaScriptEnabled")
    private fun setWebView(wv: WebView) {
        wv.setOnTouchListener { _, event ->
            if (event?.action == MotionEvent.ACTION_UP) {
                wv.requestDisallowInterceptTouchEvent(false)
            } else {
                wv.requestDisallowInterceptTouchEvent(true)
            }
            false
        }
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

        webSettings.userAgentString =
            "User-Agent:Android"

        //手机 QQ浏览器  百度手机浏览器首页
//        "Mozilla/5.0 (Linux; U; Android 10; zh-cn; ELS-AN00 Build/HUAWEIELS-AN00) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/77.0.3865.120 MQQBrowser/11.0 Mobile Safari/537.36 COVC/045429"

        //手机 Firefox  "User-Agent:Android"首页
//            "Mozilla/5.0 (Android 4.2; rv:19.0) Gecko/20121129 Firefox/19.0"

        //PC  Win7 Firefox  缩小版，跟百度浏览器头部相同
//            "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:61.0) Gecko/20100101 Firefox/61.0"
//
        //Android	Samsung三星手机	搜狗手机浏览器
//            "Mozilla/5.0 (Linux; U; Android 4.4.4; zh-cn; SM-G7508Q Build/KTU84P) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30 SogouMSE,SogouMobileBrowser/5.0.3"

        //Android	索尼爱立信MT15i	UC 浏览器
//            "Mozilla/5.0 (Linux; U; Android 2.3.4; zh-cn; MT15i Build/4.0.2.A.0.62) UC AppleWebKit/530+ (KHTML, like Gecko) Mobile Safari/530"
        //Android	魅族MX3	Webkit
//            "Mozilla/5.0 (Linux; U; Android 4.4.4; zh-cn; M351 Build/KTU84P) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30"
        //特殊	360spider		360安全UA
        //"360spider(http://webscan.360.cn)"
        //PC	Windows	Win10	Google浏览器 Chrome
//        "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36"
        //PC	Windows	Win10	360浏览器(无痕)
//            "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36 QIHU 360SE"
        //手机	Android	华为Mate 8	手机百度
//        "Mozilla/5.0 (Linux; Android 7.0; HUAWEI NXT-AL10 Build/HUAWEINXT-AL10) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/35.0.1916.138 Mobile Safari/537.36 T7/7.4 baiduboxapp/8.2.5 (Baidu; P1 7.0)"
//            "Mozilla/5.0 (Linux; U; Android 6.0.1; zh-cn; Redmi 4 Build/MMB29M) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/61.0.3163.128 Mobile Safari/537.36 XiaoMi/MiuiBrowser/10.8.1"
    }

    override fun onBackPressed() {
        if (webview.canGoBack()) {
            webview.goBack()
        } else {
            super.onBackPressed()
        }
    }

    private fun destroyWebView() {
        webview.loadDataWithBaseURL(null, "", "text/html", "utf-8", null)
        webview.clearHistory()
        content.removeView(webview)
        webview.removeView(webview)
        webview.destroy()
    }

    private fun clearCache() {
        webview.clearCache(true)
        webview.clearHistory()
        clearCookies(this)
    }

    @SuppressWarnings("deprecation")
    fun clearCookies(context: Context?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            CookieManager.getInstance().removeAllCookies(null)
            CookieManager.getInstance().flush()
        } else if (context != null) {
            val cookieSyncManager = CookieSyncManager.createInstance(context)
            cookieSyncManager.startSync()
            val cookieManager: CookieManager = CookieManager.getInstance()
            cookieManager.removeAllCookie()
            cookieManager.removeSessionCookie()
            cookieSyncManager.stopSync()
            cookieSyncManager.sync()
        }
    }
}

private class MInJavaScriptLocalObj(val context: Context) {
    @JavascriptInterface
    fun showSource(html: String) {
        Log.i(TAG, "====>html_showSource=$html")
        File(context.getExternalFilesDir(null)!!.absolutePath + File.separator + "show.html").writeText(
            html
        )
    }

    @JavascriptInterface
    fun saveLog(content: String) {
        Log.i(TAG,
            "saveLog" + context.getExternalFilesDir(null)!!.absolutePath + File.separator + "baidu_dianji.txt")
        if (PermissionUtil.hasRequiredPermissions(context))
            appendFile(
                content,
                context.getExternalFilesDir(null)!!.absolutePath + File.separator + "baidu_dianji.txt",
                context
            )
    }

    @JavascriptInterface
    fun requestFinished() {
        Log.i(TAG, "requestFinished" + (Looper.myLooper() == Looper.getMainLooper()))
        GlobalScope.launch(Dispatchers.Main) {
            // 40页都找不到，下一页异常
            (context as BaiduMWebActivity).handler.sendEmptyMessage(0)
        }
    }

    @JavascriptInterface
    fun finish() {
        Log.i(TAG, "finish")
        GlobalScope.launch(Dispatchers.Main) {
            // 目标网页跳转成功
            (context as BaiduMWebActivity).handler.sendEmptyMessage(1)
        }
    }


    @JavascriptInterface
    fun swipe() {
        Log.i(TAG, "swipe")
        val x0 = 240
        val y0 = 1230
        val x1 = 870
        val y1 = 1230
        Log.i(MyTag, "$x0,$y0;$x1,$y1")
        GlobalScope.launch {
            val result = ShellUtils.execSwipe(x0, y0, x1, y1, 500)
            Log.i(MyTag, result.toString())
        }
    }
}




package com.kydw.webviewdemo.baidu_sougou

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.*
import android.util.Log
import android.view.MotionEvent
import android.view.WindowManager
import android.webkit.JavascriptInterface
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.kydw.webviewdemo.*
import com.kydw.webviewdemo.adapter.Model
import com.kydw.webviewdemo.baidu_simplify.MyTag
import com.kydw.webviewdemo.baidu_simplify.TAG_CHECK
import com.kydw.webviewdemo.bean.TUrl
import com.kydw.webviewdemo.dialog.JAlertDialog
import com.kydw.webviewdemo.util.*
import com.kydw.webviewdemo.util.shellutil.CMD
import com.kydw.webviewdemo.util.shellutil.ShellUtils
import com.tencent.smtt.export.external.interfaces.SslError
import com.tencent.smtt.export.external.interfaces.SslErrorHandler
import com.tencent.smtt.sdk.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.content
import kotlinx.coroutines.*
import java.io.File
import java.lang.StringBuilder
import java.lang.ref.WeakReference
import java.util.*

const val UC_CHECK_TIME_INTERVAL = 3 * 60 * 1000L

class WebSouGouActivity : AppCompatActivity() {
    private var mPinIPPage = 0
    private var mLookTime: Int = 0
    private var mSwitchIPPages: Int = 0
    private var mSingleLoopMaxPages: Int = 0
    private var mCircleCount = 1
    var mCircleIndex = 1


    var isDealingWebError = false
    var stoped = false

    // <div class="results" data-page="2"> vrResult 节点的起始偏移index
    var mPageItemIndex = 0

    val isRoot = true
    private var mLoadingSwitchFlyDialog: JAlertDialog? = null
    private var mLoadingCheckNetDialog: JAlertDialog? = null
    private var mShowSetInfoDialog: JAlertDialog? = null

    private val obj = InJavaScriptLocalObj(this)
    val baiduIndexUrl = "https://wap.sogou.com/"

    val mKeyWords =
        mutableListOf<Pair<String, MutableList<TUrl>>>()
    var mKeyWordIndex = 0

    var mPageIndex = 0

    var mLastCircleIndex = 0
    var mLastKeyWordIndex = 0
    var mLastPageIndex = 0


    val handler = MyHandler(this)

    class MyHandler(activity: WebSouGouActivity) : Handler() {
        private val mActivity: WeakReference<WebSouGouActivity> = WeakReference(activity)

        override fun handleMessage(msg: Message) {
            if (mActivity.get() == null) {
                return
            }
            val activity = mActivity.get()
            when (msg.what) {
                MSG_PAGE_NEXT_EXCEPTION_OR_NOT_FOUNT -> {
                    //页面异常，没有"下一页"Node"
                    activity?.nextKeyWord()
                }
                MSG_TARGET_JUMP_SUC -> {
                    //一个keyword  的单个请求访问成功
                    activity?.onTargetJumpSuc()
                }
                MSG_WEB_VIEW_RECEIVE_ERROR -> {
                    ToastUtil.showShort(activity, "检测到网络异常，在处理...")
                    activity?.dealWebException()
                }
                MSG_CHECKING_WEB_UPDATE -> {
                    activity?.checkWebUpdate()
                }
                MSG_WEB_VIEW_SET_INDEX -> {
                    activity?.setItemStartIndex(msg.arg1)
                }
                MSG_PAGE_INDEX -> {
                    activity?.setPageIndex(msg.arg1)
                }
                else -> {
                }
            }
        }
    }

    private fun setItemStartIndex(index: Int) {
        Log.i(MyTag, "setItemIndex" + index)
        mPageItemIndex = index
    }

    private fun onTargetJumpSuc() {
        nextRequest()
    }

    private fun setPageIndex(pageIndex: Int) {
        Log.e(com.kydw.webviewdemo.baidu_simplify.TAG, "page=" + pageIndex)
        mPageIndex = pageIndex

        val switchIPPages =
            getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getInt(SWITCH_IP_PAGE_NUM, 0)
        val pageMax =
            getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getInt(SINGLE_LOOP_PAGE_MAX, 0)
        Log.e(com.kydw.webviewdemo.baidu_simplify.TAG,
            "mPinIPPage=" + mPinIPPage + ",switchIPPages=" + switchIPPages + "是否需要切换IP=" + (switchIPPages > 0 && (pageIndex > mPinIPPage) && (switchIPPages > 0) && (pageIndex % switchIPPages == 0) && (pageIndex != pageMax)))
        if (switchIPPages > 0 && (pageIndex > mPinIPPage) && (switchIPPages > 0) && (pageIndex % switchIPPages == 0) && (pageIndex != pageMax)) {
            mPinIPPage = pageIndex
            stopWebView()
            // 切换IP
            if (mLoadingSwitchFlyDialog == null) {
                mLoadingSwitchFlyDialog =
                    JAlertDialog.Builder(this).setContentView(R.layout.dialog_waitting_fly)
                        .setWidth_Height_dp(300, 120).setCancelable(false)
                        .create()
            }
            mLoadingSwitchFlyDialog?.show()

            GlobalScope.launch(Dispatchers.IO) {
                delay(2000)//因为上面reloadWebview可能还未加载完，立即切换到飞行模式会 webview会回调报错信息
                val result0 = ShellUtils.execCommand(CMD.IP + " rmnet_data0", isRoot)
                ShellUtils.execCommand(CMD.AIRPLANE_MODE_ON, isRoot)
                delay(2000)
                ShellUtils.execCommand(CMD.AIRPLANE_MODE_OFF, isRoot)

                //关掉飞行时，4G 需要慢慢打开
                delay(2000)

                for (i in 1..60) {
                    if (NetState.hasNetWorkConnection(this@WebSouGouActivity) && isOnline()) {
                        val result1 = ShellUtils.execCommand(CMD.IP + " rmnet_data0", isRoot)
                        break
                    } else {
                        Log.i(MyTag, "网络未建立，再等2秒,$i")
                        delay(2000)
                    }
                }
                delay(2000)
                withContext(Dispatchers.Main) {
                    mLoadingSwitchFlyDialog?.dismiss()
                    goonWebView()
                }
            }

        }


    }

    private fun goonWebView() {
        stoped = false
        webview.reload()
    }

    private fun stopWebView() {
        stoped = true
        webview.reload()
    }

    private fun checkWebUpdate() {
        if (stoped) {
            return
        }

        if (mLastCircleIndex == mCircleIndex && mLastKeyWordIndex == mKeyWordIndex && mLastPageIndex == mPageIndex) {
            ToastUtil.showShort(this, "检擦到页面卡住一分钟")
            Log.i(MyTag, "检测到 1 分钟还有同一个循环里同个关键词，同一个页面")
            goonWebView()
        } else {
            Log.i(MyTag, "2min  页面有变化")
        }

        mLastCircleIndex = mCircleIndex
        mLastKeyWordIndex = mKeyWordIndex
        mLastPageIndex = mPageIndex


    }

    private fun dealWebException() {
        if (!isDealingWebError) {
            isDealingWebError = true
            var count = 0
            GlobalScope.launch(Dispatchers.Main) {
                if (mLoadingCheckNetDialog == null) {
                    mLoadingCheckNetDialog =
                        JAlertDialog.Builder(this@WebSouGouActivity)
                            .setContentView(R.layout.dialog_waiting_net)
                            .setText(R.id.content, if (count > 100) "请检查4G卡是否有流量" else "网络已断开...")
                            .setWidth_Height_dp(300, 120).setCancelable(false)
                            .create()
                }
                mLoadingCheckNetDialog?.show()
                do {
                    count++
                    ShellUtils.execCommand(CMD.DATA_OFF, true)
                    ShellUtils.execCommand(CMD.WIFI_ON, true)
                    delay(2000)
                    if (NetState.hasNetWorkConnection(this@WebSouGouActivity) && isOnline()) {
                        webViewGoBack()
                        isDealingWebError = false
                        mLoadingCheckNetDialog?.dismiss()
                        return@launch
                    }
                } while (true)
            }
        }
    }

    private fun webViewGoBack() {
        if (webview.canGoBack()) {
            webview.goBack()
        } else {
            webview.loadUrl(baiduIndexUrl)
        }
    }


    /*
    * 同一个keyword 下一个请求
    * 目标页返回搜狗搜索页
    * */
    private fun nextRequest() {
        if (webview.canGoBack()) {
            webview.goBack()
        }
    }

    private fun nextKeyWord() {
        mPinIPPage = 0
        //切换IP
        if (mLoadingSwitchFlyDialog == null) {
            mLoadingSwitchFlyDialog =
                JAlertDialog.Builder(this).setContentView(R.layout.dialog_waitting_fly)
                    .setWidth_Height_dp(300, 120).setCancelable(false)
                    .create()
        }
        mLoadingSwitchFlyDialog?.show()

        // switchIP
        GlobalScope.launch(Dispatchers.IO) {
            val result0 = ShellUtils.execCommand(CMD.IP + " rmnet_data0", isRoot)
            if (result0?.successMsg != null) {
                val sucMsg0 = result0.successMsg!!
                Log.i(MyTag, "result0.sucMsg0=$sucMsg0, ")
                saveIP(sucMsg0)
            }

            ShellUtils.execCommand(CMD.AIRPLANE_MODE_ON, isRoot)
            delay(2000)
            ShellUtils.execCommand(CMD.AIRPLANE_MODE_OFF, isRoot)

            //关掉飞行时，4G 需要慢慢打开
            delay(2000)

            for (i in 1..60) {
                if (NetState.hasNetWorkConnection(this@WebSouGouActivity) && isOnline()) {
                    val result1 = ShellUtils.execCommand(CMD.IP + " rmnet_data0", isRoot)
                    if (result1?.successMsg != null) {
                        Log.i(MyTag, "result1.sucMsg=" + result1.successMsg?.toString())
                        appendFile(result1.successMsg + "\n\n",
                            getExternalFilesDir(null)!!.absolutePath + File.separator + "ip.txt",
                            this@WebSouGouActivity)
                    }
                    break
                } else {
                    Log.i(MyTag, "网络未建立，再等2秒,$i")
                    delay(2000)
                }
            }
            delay(2000)
            withContext(Dispatchers.Main) {
                mLoadingSwitchFlyDialog?.dismiss()
                if (mKeyWordIndex < mKeyWords.lastIndex) {
                    mKeyWordIndex++
                    webview.loadUrl(baiduIndexUrl)
                } else {
                    //一个循环结束
                    if (mCircleCount == 0) {
                        //无限循环
                        nextCircle()
                    } else {
                        if (mCircleIndex == mCircleCount) {
                            //第mCircleIndex次循环结束
                            ToastUtil.show(this@WebSouGouActivity, "循环结束")
                            finish()
                        } else {
                            //开启下一次循环
                            ToastUtil.show(this@WebSouGouActivity,
                                "开启第" + (mCircleIndex + 1) + "次循环")
                            nextCircle()
                        }
                        mCircleIndex++
                    }
                }
            }
        }
    }

    private fun nextCircle() {
        mLoadingSwitchFlyDialog?.dismiss()
        mKeyWordIndex = 0
        mKeyWords.forEach {
            it.second.forEach {
                it.isRequested = false
            }
        }
        clearCache()
        webview.loadUrl(baiduIndexUrl)
    }

    private fun saveIP(sucMsg0: String) {
        appendFile(sucMsg0,
            getExternalFilesDir(null)!!.absolutePath + File.separator + "ip.txt", this)
    }

    override fun onDestroy() {
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        destroyWebView()
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initData(intent)

        webview.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)

            }

            override fun onReceivedError(p0: WebView?, p1: Int, p2: String?, p3: String?) {
                super.onReceivedError(p0, p1, p2, p3)
                handler.sendEmptyMessage(MSG_WEB_VIEW_RECEIVE_ERROR)
                Log.e(MyTag, TAG_CHECK + "onReceivedError")
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                if (stoped) {
                    webview.loadUrl("javascript:scrollTo(0,0);")
                    return
                }
                Log.i(MyTag, "onPageFinished = $url")

//                view!!.loadUrl(
//                    "javascript:" + "var url=\"${url!!}\";" +
//                            "window.java_obj.showSource("
//                            + "document.getElementsByTagName('html')[0].innerHTML,url);"
//                )

                val keyWord = mKeyWords[mKeyWordIndex].first
                val siteInfo = mKeyWords[mKeyWordIndex].second

                if (url == baiduIndexUrl) {
                    Log.e(MyTag, "搜狗首页=" + url)
                    //首页，提交表单
                    val jsForm =
                        application.assets.open("sougou/js_index.js").bufferedReader().use {
                            it.readText()
                        }
                    Log.i(MyTag, "keyword$keyWord")
                    Log.i(MyTag, "siteInfo$siteInfo")
                    val head = "var keyword=\"$keyWord\";"
                    view!!.loadUrl("javascript:$head$jsForm")
                } else if (url!!.startsWith("https://wap.sogou.com/web/searchList.jsp")) {
                    Log.e(MyTag, "搜狗搜索后首页加载=$url")
                    //Next 页
                    val jsToNext =
                        application.assets.open("sougou/js_next_page.js").bufferedReader().use {
                            it.readText()
                        }

                    val jsList = StringBuilder()
                    jsList.append("[")
                    for (i in siteInfo.indices) {
                        jsList.append("\"${siteInfo[i].url}\",")
                    }
                    jsList.append("]")
                    val head =
                        "var targetSites=$jsList;var itemStartIndex=$mPageItemIndex;var page_max=$mSingleLoopMaxPages;var ip_page=$mSwitchIPPages;"
                    Log.e(MyTag, "jsList head=" + head)
                    view!!.loadUrl("javascript:$head$jsToNext")
                } else {
                    Log.e(MyTag, "目标页加载成功=$url")
                    siteInfo.forEach {
                        if (url.contains(it.url)) {
                            it.isRequested = true
                        }
                    }
                    val lookTime = if (mLookTime < 1000) 1000 else mLookTime
                    val head = "var look_time=$lookTime;"
                    val jsLook = application.assets.open("js_look.js").bufferedReader().use {
                        it.readText()
                    }
                    view!!.loadUrl("javascript:$head$jsLook")
                }
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
        webview.loadUrl(baiduIndexUrl)
        webview.keepScreenOn = true

        but_stop.setOnClickListener {
            stopWebView()

        }
        but_go.setOnClickListener {
            reStartWebView()
        }


        //循环检查网页
        val runnable = object : Runnable {
            override fun run() {
                handler.sendEmptyMessage(MSG_CHECKING_WEB_UPDATE)
                handler.postDelayed(this, UC_CHECK_TIME_INTERVAL)
            }
        }
        handler.postDelayed(runnable, UC_CHECK_TIME_INTERVAL)
    }

    private fun reStartWebView() {
        stoped = false
        webview.reload()
    }


    private fun initData(intent: Intent) {
        restore()

        val list = intent.getParcelableArrayExtra(KEYWORD_SITES)
        list!!.forEach {
            Log.i(MyTag, "initData\t" + it.toString())
        }
        val maps = list.groupBy { (it as Model).keyword }
        for (item in maps.toList()) {
            mKeyWords.add(Pair(item.first!!,
                item.second.map { TUrl((it as Model).site!!, false) }.toMutableList()))
        }
        mKeyWords.forEach {
            Log.e(MyTag, "initData after \t" + it.toString())
        }

        val siteInfo = mKeyWords[mKeyWordIndex].second
        val jsList = StringBuilder()
        jsList.append("[")
        for (i in siteInfo.indices) {
            if (!siteInfo[i].isRequested) {
                jsList.append("\"${siteInfo[i].url}\",")
            }
        }
        jsList.append("]")
        Log.e(MyTag, "jsList=" + jsList)
    }

    private fun
            hasTargetSite(url: String, siteInfo: MutableList<TUrl>): Boolean {
        siteInfo.forEach {
            if (url.contains(it.url)) {
                return true
            }
        }
        return false
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

//      <meta content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0" name="viewport"/>搜狗不支持缩放
//        wv.setInitialScale(100)

// 设置是否开启DOM存储API权限，默认false，未开启，设置为true，WebView能够使用DOM storage API
        webSettings.domStorageEnabled = true

        webSettings.javaScriptEnabled = true

        webSettings.userAgentString = "User-Agent:Android"
//        webSettings.userAgentString = "Mozilla/5.0 (Linux; Android 10; MAR-AL00 Build/HUAWEIMAR-AL00; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/76.0.3809.89 Mobile Safari/537.36 T7/11.19 SP-engine/2.15.0 baiduboxapp/11.19.5.10 (Baidu; P1 10)"

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

    private fun restore() {
        mCircleCount = getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getInt(LOOP_COUNT, 0)

        mSingleLoopMaxPages = getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getInt(
            SINGLE_LOOP_PAGE_MAX,
            SINGLE_LOOP_PAGE_MAX_DEFAULT)

        mSwitchIPPages =
            getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getInt(SWITCH_IP_PAGE_NUM, 0)

        mLookTime =
            getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getInt(PAGE_LOOP_TIME, 0)
    }
}


private class InJavaScriptLocalObj(val context: Context) {
    @JavascriptInterface
    fun showSource(html: String, url: String) {
        Log.i(MyTag, "showSource")
        appendFile(
            "\n" + "url=" + url + "\n" + html.subSequence(0, 50) + "\n",
            context.getExternalFilesDir(null)!!.absolutePath + File.separator + "htmls.txt",
            context
        )
        // 写入当前网络加载成功的时间
        val date = Date()
        val curTime = date.time
        context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).edit {
            putLong(KEY_LOAD_PAGE_TIME, curTime)
        }

    }

    @JavascriptInterface
    fun saveLog(content: String) {
        Log.i(MyTag,
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
        Log.i(MyTag, "requestFinished" + (Looper.myLooper() == Looper.getMainLooper()))
        GlobalScope.launch(Dispatchers.Main) {
            // 页面没有"下一页"，页面异常
            (context as WebSouGouActivity).handler.sendEmptyMessage(
                MSG_PAGE_NEXT_EXCEPTION_OR_NOT_FOUNT)
        }
    }

    @JavascriptInterface
    fun finish() {
        Log.i(MyTag, "finish")
        GlobalScope.launch(Dispatchers.Main) {
            // 目标网页跳转成功
            (context as WebSouGouActivity).handler.sendEmptyMessage(MSG_TARGET_JUMP_SUC)
        }
    }

    @JavascriptInterface
    fun swipe() {
        Log.i(MyTag, "swipe")
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

    @JavascriptInterface
    fun setItemStartIndex(itemStartIndex: Int) {
        Log.i(MyTag, "itemStartIndex=$itemStartIndex")
        GlobalScope.launch(Dispatchers.Main) {
            // 目标网页跳转成功
            val msg = Message()
            msg.what = MSG_WEB_VIEW_SET_INDEX
            msg.arg1 = itemStartIndex
            (context as WebSouGouActivity).handler.sendMessage(msg)
        }
    }

    @JavascriptInterface
    fun requestPageNUM(pageNum: Int) {
        GlobalScope.launch(Dispatchers.Main) {
            // 目标网页跳转成功
            val msg = Message()
            msg.what = MSG_PAGE_INDEX
            msg.arg1 = pageNum
            (context as WebSouGouActivity).handler.sendMessage(msg)
        }

    }


}
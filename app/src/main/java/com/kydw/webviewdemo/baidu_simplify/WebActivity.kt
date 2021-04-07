package com.kydw.webviewdemo.baidu_simplify

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.*
import android.util.Log
import android.view.MotionEvent
import android.view.WindowManager
import android.webkit.JavascriptInterface
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.kydw.webviewdemo.*
import com.kydw.webviewdemo.adapter.Model
import com.kydw.webviewdemo.baidu_sougou.ERR_ARG1
import com.kydw.webviewdemo.baidu_sougou.ERR_ARG2
import com.kydw.webviewdemo.baidu_sougou.PageState
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


const val TAG_CHECK = "check exception:\t"
const val CHECK_TIME_INTERVAL = 60 * 1000L

class WebActivity : AppCompatActivity() {
    var mCurPageState: PageState = PageState.Index
    var isDealingWebError = false
    var mCircleCount = 1
    var mCircleIndex = 1
    var stoped = false

    val isRoot = true
    private var mLoadingSwitchFlyDialog: JAlertDialog? = null
    private var mLoadingCheckNetDialog: JAlertDialog? = null

    private var mPinPageIndex = 0
    private var mPinItemIndex = 0


    private var mPinIPPage = 0

    private var mCurPageIndex=0

    private val obj = InJavaScriptLocalObj(this)
    val baiduIndexUrl = "https://www.baidu.com/"

    val mKeyWords =
        mutableListOf<Pair<String, MutableList<TUrl>>>()
    var mKeyWordIndex = 0
//    var mLatestLookedUrl=""

    val handler = MyHandler(this)

    class MyHandler(activity: WebActivity) : Handler() {
        private val mActivity: WeakReference<WebActivity> = WeakReference(activity)

        override fun handleMessage(msg: Message) {
            if (mActivity.get() == null) {
                return
            }
            val activity = mActivity.get()
            when (msg.what) {
                MSG_KEY_WORD -> {
                    activity?.nextKeyWord()
                }
                MSG_TARGET_JUMP_SUC -> {
                    //一个keyword  的单个请求访问成功
                    activity?.onTargetJumpSuc()
                }
                MSG_WEB_VIEW_RECEIVE_ERROR -> {
                    val errInfo = msg.data.getString(ERR_ARG1)!!
                    val errUrl = msg.data.getString(ERR_ARG2)!!
                    activity?.dealWebException(errInfo, errUrl)
                }
                MSG_CHECKING_WEB_UPDATE -> {
                    activity?.checkWebUpdate()
                }
                MSG_PAGE_PIN_INDEX -> {
                    activity?.setPinIndex(msg.arg1, msg.arg2)
                }
                MSG_PAGE_INDEX -> {
                    activity?.setPageIndex(msg.arg1)
                }
                MSG_LOOK_PAGE_DIVS_SMALL->{
                    activity?.lookPageDivsSmall()
                }
                CLICK_NEXT_PAGE->{
                    activity?.resetPin()
                }
                else -> {
                }
            }
        }
    }
    private fun dealWebException(errInfo: String, errUrl: String) {
        LogUtils.d(MyTag, "dealWebException网络不可用\n$errInfo\n$errUrl")
        if (Looper.myLooper() == mainLooper) {
            if (errInfo == "net::ERR_NAME_NOT_RESOLVED") {
                AlertDialog.Builder(this).setTitle("提示").setMessage("网络不可用\n$errInfo\n$errUrl")
                    .setCancelable(true)
                    .show()
            } else {
                ToastUtil.show(this, "网络不可用\n$errInfo\n$errUrl, 正在重载")
                LogUtils.d(MyTag, "dealWebException webview reload")
                webview.reload()
            }

        }
    }

    private fun resetPin() {
        mPinItemIndex=0
        mPinPageIndex=0
    }

    private fun lookPageDivsSmall() {
        val tip = AlertDialog.Builder(this).setTitle("注意")
            .setMessage("目标页面显示似乎异常(DIVS<3)\n").create()
        tip.show()
        GlobalScope.launch(Dispatchers.IO) {
            delay(10000)
            withContext(Dispatchers.Main) {
                tip.dismiss()
            }
            delay(100)
            withContext(Dispatchers.Main) {
                webview.goBack()
            }
        }
    }


    @SuppressLint("SetTextI18n")
    private fun updateUI() {
        tv_cur_page.text = "第$mCurPageIndex 页"
        tv_rec_look.text = """最近浏览:${mPinPageIndex}页${(mPinItemIndex)}项"""
        tv_loop.text = """第${mCircleIndex}次循环"""
        tv_kw.text = "关键词:" + mKeyWords[mKeyWordIndex].first
    }

    private fun setPageIndex(pageIndex: Int) {

        mCurPageIndex=pageIndex
        updateUI()

        val switchIPPages =
            getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getInt(SWITCH_IP_PAGE_NUM, 0)
        val pageMax=getSharedPreferences(SP_NAME,Context.MODE_PRIVATE).getInt(SINGLE_LOOP_PAGE_MAX,0)

        if (switchIPPages > 0 && (pageIndex > mPinIPPage) && (pageIndex % switchIPPages == 0)&&(pageIndex!=pageMax)) {
            Log.e(TAG,"正在切换IP: 当前页$mCurPageIndex / 多少页切换IP $switchIPPages")
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
                    if (NetState.hasNetWorkConnection(this@WebActivity) && isOnline()) {
                        val result1 = ShellUtils.execCommand(CMD.IP + " rmnet_data0", isRoot)
                        break
                    } else {
                        Log.i(MyTag, "网络未建立，再等2秒,$i")
                        delay(2000)
                    }
                }
                delay(2000)
                withContext(Dispatchers.Main) {
                    if(mLoadingSwitchFlyDialog!=null){
                        mLoadingSwitchFlyDialog?.dismiss()
                    }
                    goonWebView()
                }
            }

        }


    }

    private fun setPinIndex(pinPage: Int, pinIndex: Int) {
        mPinPageIndex = pinPage
        mPinItemIndex = pinIndex
        updateUI()
    }

    private fun onTargetJumpSuc() {
        nextRequest()
    }


    private fun checkWebUpdate() {
        if (stoped) {
            return
        }
        val curTime = Date().time
        val lastTime = getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
            .getLong(KEY_LOAD_PAGE_TIME, curTime)
        Log.i(MyTag,   "检查页面有无卡住,当前页面已加载"+(curTime - lastTime)/1000+"s")
        if ((curTime - lastTime) > CHECK_TIME_INTERVAL) {
            val min=CHECK_TIME_INTERVAL/60000
            Log.e(MyTag, "检查到停在一个界面超过"+ CHECK_TIME_INTERVAL/60000+"MIN")
            val tip =
                AlertDialog.Builder(this).setTitle("提示").setMessage("检查到界面有卡住超过 $min 分钟\n将回退到上一个界面")
                    .create()
            tip.show()
            GlobalScope.launch(Dispatchers.IO) {
                delay(6000)
                withContext(Dispatchers.Main) {
                    tip.dismiss()
                    webViewGoBack()
                }
            }
        }
    }

    /*
    * 检查到页面卡住时
    * */
    private fun dealWebException() {
        if (!isDealingWebError) {
            isDealingWebError = true
            var count = 0
            GlobalScope.launch(Dispatchers.Main) {
                if (mLoadingCheckNetDialog == null) {
                    mLoadingCheckNetDialog =
                        JAlertDialog.Builder(this@WebActivity)
                            .setContentView(R.layout.dialog_waiting_net)
                            .setText(R.id.content, "发现异")
                            .setWidth_Height_dp(300, 120).setCancelable(false)
                            .create()
                }
                mLoadingCheckNetDialog?.show()
                do {
                    count++
                    ShellUtils.execCommand(CMD.CMD_1, true)
                    ShellUtils.execCommand(CMD.CMD_2, true)
                    delay(3000)
                    if (NetState.hasNetWorkConnection(this@WebActivity) && isOnline()) {
                        isDealingWebError = false
                        if (mLoadingCheckNetDialog!!.isShowing) {
                            mLoadingCheckNetDialog?.dismiss()
                        }
                        webViewGoBack()
                        return@launch
                    }
                } while (true)
            }
        }
    }
    private fun webViewGoBack() {
        if (webview.canGoBack()) {
            if (mCurPageState == PageState.LOOK || mCurPageState == PageState.SOU) {
                webview.goBack()
            } else {
                webview.reload()
            }
        } else {
            webview.loadUrl(baiduIndexUrl)
        }
    }


    /*
    * 同一个keyword 下一个请求
    * 目标页返回百度搜索页
    * */
    private fun nextRequest() {
        if (webview.canGoBack()) {
            webview.goBack()
        }
    }

    private fun nextKeyWord() {
        resetPin()

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
                if (NetState.hasNetWorkConnection(this@WebActivity) && isOnline()) {
                    val result1 = ShellUtils.execCommand(CMD.IP + " rmnet_data0", isRoot)
                    if (result1?.successMsg != null) {
                        Log.i(MyTag, "result1.sucMsg=" + result1.successMsg?.toString())
                        appendFile(result1.successMsg + "\n\n",
                            getExternalFilesDir(null)!!.absolutePath + File.separator + "ip.txt",
                            this@WebActivity)
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
                            ToastUtil.show(this@WebActivity, "循环结束")
                            finish()
                        } else {
                            //开启下一次循环
                            nextCircle()
                        }
                    }
                }
            }
        }


    }

    private fun nextCircle() {
        mCircleIndex++

        mPinPageIndex=0
        mPinItemIndex=0
        mLoadingSwitchFlyDialog?.dismiss()
        mKeyWordIndex = 0
        updateUI()

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
                Log.e(MyTag, TAG_CHECK + "onReceivedError")
                Log.e(MyTag, TAG_CHECK + "p1=" + p1 + ",p2=" + p2 + ",p3=" + p3)

                val msg = Message()
                msg.what = MSG_WEB_VIEW_RECEIVE_ERROR
                msg.data.putString(ERR_ARG1, p2)
                msg.data.putString(ERR_ARG2, p3)
                handler.sendMessage(msg)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                Log.i(TAG, "onPageFinished = $url")

                super.onPageFinished(view, url)
                if (stoped) {
                    return
                }

                view!!.loadUrl(
                    "javascript:" + "var url=\"${url!!}\";" +
                            "window.java_obj.showSource("
                            + "document.getElementsByTagName('html')[0].innerHTML,url);"
                )

                val keyWord = mKeyWords[mKeyWordIndex].first
                val siteInfo = mKeyWords[mKeyWordIndex].second

                if (url == baiduIndexUrl) {
                    mCurPageState=PageState.Index
                    Log.e(TAG, "百度首页=" + url)
                    //首页，提交表单
                    val jsForm =
                        application.assets.open("baidu_bsr/js_bd_2second.js").bufferedReader().use {
                            it.readText()
                        }
                    val head = "var keyword=\"$keyWord\";"
                    view.loadUrl("javascript:$head$jsForm")
                    Log.i(MyTag, "首页注入js    $head")

                } else if (hasTargetSite(url,
                        siteInfo) && !url.startsWith("https://www.baidu.com/") && !url.startsWith("https://m.baidu.com/")
                ) {
                    mCurPageState=PageState.LOOK
                    Log.e(TAG, "目标页加载成功=$url")
                    siteInfo.forEach {
                        if (url.contains(it.url)) {
                            it.isRequested = true
                        }
                    }
                    val spLookTime =
                        getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getInt(PAGE_LOOP_TIME,
                            0)
                    val lookTime = if (spLookTime < 5) 5000 else (spLookTime*1000)
                    val head = "var look_time=$lookTime;"

                    val jsLook = application.assets.open("js_look.js").bufferedReader().use {
                        it.readText()
                    }
                    view.loadUrl("javascript:$head$jsLook")
                } else if (url.contains("baidu.com")) {
                    if (url.contains("wappass.baidu.com/static/captcha/tuxing")) {
                        //验证码
                        Log.e(MyTag, "发现验证码界面加载$url")
                        val jsSwipe =
                            application.assets.open("js_swipe_vc_by_cb.js").bufferedReader().use {
                                it.readText()
                            }
                        view.loadUrl("javascript:$jsSwipe")
                    } else {
                        mCurPageState=PageState.SOU
                        Log.e(MyTag, "发现下一页加载=" + url)
                        //Next 页
                        val jsToNext =
                            application.assets.open("baidu_bsr/js_to_next.js").bufferedReader().use {
                                it.readText()
                            }

                        val jsList = StringBuilder()
                        jsList.append("[")
                        for (i in siteInfo) {
//                            if (siteInfo[i].url!=mLatestLookedUrl) {
                            jsList.append("\"${i.url}\",")
//                            }
                        }
                        jsList.append("]")
                        LogUtils.e("jsList=$jsList")

                        val pageMax = getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getInt(
                            SINGLE_LOOP_PAGE_MAX,
                            SINGLE_LOOP_PAGE_MAX_DEFAULT)
                        val head =
                            "var targetSites=$jsList; var page_max=$pageMax;var pinPage=$mPinPageIndex; var pinIndex=$mPinItemIndex;"
                        view.loadUrl("javascript:$head$jsToNext")
                        Log.e(MyTag, "下一页  注入 js=\t$head")


                    }
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
            goonWebView()
        }


        //循环检查网页
        val runnable = object : Runnable {
            override fun run() {
                handler.sendEmptyMessage(MSG_CHECKING_WEB_UPDATE)
                handler.postDelayed(this, CHECK_TIME_INTERVAL)
            }
        }
        handler.postDelayed(runnable, CHECK_TIME_INTERVAL)
    }

    private fun goonWebView() {
        stoped = false
        webview.reload()
    }

    private fun stopWebView() {
        stoped = true
        webview.reload()
    }

    private fun initData(intent: Intent) {
        val list = intent.getParcelableArrayExtra(KEYWORD_SITES)
        mCircleCount = getSharedPreferences(SP_NAME,Context.MODE_PRIVATE).getInt(LOOP_COUNT,0)

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
        for (i in siteInfo) {
                jsList.append("\"${i.url}\",")
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

//      <meta content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0" name="viewport"/>百度不支持缩放
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


    private fun setCurPage(pageIndex: Int) {
        mCurPageIndex = pageIndex
        updateUI()
    }




}


private class InJavaScriptLocalObj(val context: Context) {
    @JavascriptInterface
    fun showSource(html: String, url: String) {
        Log.i(MyTag, "showSource")
//        appendFile(
//            "\n" + "url=" + url + "\n" + html.subSequence(0, 50) + "\n",
//            context.getExternalFilesDir(null)!!.absolutePath + File.separator + "htmls.txt",
//            context
//        )
        // 写入当前网络加载成功的时间
        val date = Date()
        val curTime = date.time
        context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).edit {
            putLong(KEY_LOAD_PAGE_TIME, curTime)
        }

    }

    @JavascriptInterface
    fun saveLog(content: String) {
//        Log.i(TAG,
//            "saveLog" + context.getExternalFilesDir(null)!!.absolutePath + File.separator + "baidu_dianji.txt")
//        if (PermissionUtil.hasRequiredPermissions(context))
//            appendFile(
//                content,
//                context.getExternalFilesDir(null)!!.absolutePath + File.separator + "baidu_dianji.txt",
//                context
//            )

    }

    @JavascriptInterface
    fun nextKeyword() {
        GlobalScope.launch(Dispatchers.Main) {
            (context as WebActivity).handler.sendEmptyMessage(MSG_KEY_WORD)
        }
    }

    @JavascriptInterface
    fun finish() {
        GlobalScope.launch(Dispatchers.Main) {
            // 目标网页跳转成功
            (context as WebActivity).handler.sendEmptyMessage(MSG_TARGET_JUMP_SUC)
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
            val result = ShellUtils.execSwipe(x0, y0, x1, y1, 700)
            Log.i(MyTag, result.toString())
        }
    }

    @JavascriptInterface
    fun pinIndex(pinPage: Int, pinIndex: Int) {
        GlobalScope.launch(Dispatchers.Main) {
            // 目标网页跳转成功
            val msg = Message()
            msg.what = MSG_PAGE_PIN_INDEX
            msg.arg1 = pinPage
            msg.arg2 = pinIndex
            (context as WebActivity).handler.sendMessage(msg)
        }
    }

    @JavascriptInterface
    fun pageIndex(page: Int) {
        GlobalScope.launch(Dispatchers.Main) {
            val msg = Message()
            msg.what = MSG_PAGE_INDEX
            msg.arg1 = page
            (context as WebActivity).handler.sendMessage(msg)
        }
    }
    @JavascriptInterface
    fun  lookPageError(){
        GlobalScope.launch(Dispatchers.Main) {
            val msg = Message()
            msg.what = MSG_LOOK_PAGE_DIVS_SMALL
            (context as WebActivity).handler.sendMessage(msg)
        }
    }

    @JavascriptInterface
    fun  clickNextPage(){
        GlobalScope.launch(Dispatchers.Main) {
            val msg = Message()
            msg.what = CLICK_NEXT_PAGE
            (context as WebActivity).handler.sendMessage(msg)
        }
    }

}



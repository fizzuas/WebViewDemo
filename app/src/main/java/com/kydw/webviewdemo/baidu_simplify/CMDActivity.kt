package com.kydw.webviewdemo.baidu_simplify

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.*
import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.chad.library.adapter.base.listener.OnItemSwipeListener
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kydw.webviewdemo.*
import com.kydw.webviewdemo.adapter.Model
import com.kydw.webviewdemo.adapter.ModelAdapter
import com.kydw.webviewdemo.adapter.SITE
import com.kydw.webviewdemo.baidu_360.Web360Activity
import com.kydw.webviewdemo.baidu_sougou.WebSouGouActivity
import com.kydw.webviewdemo.baidu_sougou_wx.WebSouGouWXActivity
import com.kydw.webviewdemo.baidu_toutiao.WebToutTiaoActivity
import com.kydw.webviewdemo.dialog.*
import com.kydw.webviewdemo.network.UpdateService
import com.kydw.webviewdemo.network.UploadFileInfo
import com.kydw.webviewdemo.network.UploadFileResult
import com.kydw.webviewdemo.util.*
import com.kydw.webviewdemo.util.shellutil.ShellUtils
import com.zhy.http.okhttp.OkHttpUtils
import com.zhy.http.okhttp.callback.FileCallBack
import kotlinx.android.synthetic.main.activity_c_m_d.*
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.angmarch.views.NiceSpinner
import org.angmarch.views.OnSpinnerItemSelectedListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.*
import kotlin.system.exitProcess


const val MyTag: String = "oyx"

enum class BrowserType(val i: String) {
    BaiDu("百度"),
    SouGou("搜狗"),

    TouTiao("头条"),
    B360("360"),
    SouGou_WX("搜狗(WX)")
}

class CMDActivity : AppCompatActivity(), DialogAddKeySite.OnConfirmClickListener,
    DialogInputSite.OnOKListener, DialogEditKW.OnKW2Listener, DialogEditSite.OnSite2Listener {


    var models = mutableListOf<Model>(
//        Model("site:pewdo.com", "pewdo.com")
//        Model("site:68681313.cn", "68681313.cn")
//
//
        Model("site:oillara.com", "oillara.com"),
        Model("site:lcbc.net baidukey.com", "lcbc.net baidukey.com")
    )

    private val modelAdapter: ModelAdapter = ModelAdapter(models)
    private val mDialog: ProgressDialog by lazy { ProgressDialog(this) }

    private val mDialogAddKeySite: DialogAddKeySite = DialogAddKeySite()
    private val mStartDialog: DialogInputSite = DialogInputSite()
    var intentFilter = IntentFilter("android.intent.action.AIRPLANE_MODE")
    private var mSearchType: String = BrowserType.BaiDu.i

    var receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val isAirplaneModeOn = intent.getBooleanExtra("state", false)
            Log.i(MyTag, "Service state changed" + isAirplaneModeOn)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_c_m_d)

        PermissionUtil.askForRequiredPermissions(this)

        registerReceiver(receiver, intentFilter)

        but_addkw.setOnClickListener {
            mDialogAddKeySite.show(supportFragmentManager, DIALOG_INPUT)
        }

        recy_configs.apply {
            this.layoutManager = LinearLayoutManager(this.context)
            modelAdapter.setOnItemClickListener { _, view, position ->
                Log.i(MyTag, models[position].toString())
            }


            adapter = modelAdapter
//            modelAdapter.draggableModule.isSwipeEnabled = true
//            modelAdapter.draggableModule.setOnItemSwipeListener(onItemSwipeListener)


        }
        tv_version_name.text = "version:" + getAppVersionName(this)

        but_add_site.setOnClickListener {
            mStartDialog.show(supportFragmentManager, DIALOG_INPUT_SITE)
        }

        but_set.setOnClickListener {
            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
        }

        //spinner

        val niceSpinner: NiceSpinner = findViewById<View>(R.id.nice_spinner) as NiceSpinner
        val dataset: List<String> = LinkedList(
            Arrays.asList(
                BrowserType.BaiDu.i,
                BrowserType.SouGou.i
//            BrowserType.SouGou_WX.i

//            BrowserType.TouTiao.i,
//            BrowserType.B360.i),
            ))
        niceSpinner.attachDataSource(dataset)
        niceSpinner.setOnSpinnerItemSelectedListener(object : OnSpinnerItemSelectedListener {
            override fun onItemSelected(
                parent: NiceSpinner?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val item = parent!!.getItemAtPosition(position)
                Log.i(MyTag, "item=$item")
                mSearchType = item.toString()
            }
        })


        but_sou.setOnClickListener {

            startSearch(mSearchType)
//            checkUpdate()
        }

    }

    override fun onStop() {
        super.onStop()
        saveCache()

    }


    private fun initData() {
        val content: String? =
            ACache.get(this@CMDActivity).getAsString(KEY_CACHE_LIST)
        if (content != null) {
            val type = object : TypeToken<MutableList<Model>>() {}.type
            models.clear()
            models.addAll(Gson().fromJson(content, type))
            modelAdapter.notifyDataSetChanged()
        }
    }


    // 侧滑监听
    var onItemSwipeListener: OnItemSwipeListener = object : OnItemSwipeListener {
        override fun onItemSwipeStart(viewHolder: ViewHolder, pos: Int) {
            Log.d(TAG, "view swiped start: $pos")
            val holder = viewHolder as BaseViewHolder
        }

        override fun clearView(viewHolder: ViewHolder, pos: Int) {
            Log.d(TAG, "View reset: $pos")
            val holder = viewHolder as BaseViewHolder
        }

        override fun onItemSwiped(viewHolder: ViewHolder, pos: Int) {
            Log.d(TAG, "View Swiped: $pos")
        }

        override fun onItemSwipeMoving(
            canvas: Canvas,
            viewHolder: ViewHolder,
            dX: Float,
            dY: Float,
            isCurrentlyActive: Boolean
        ) {
            canvas.drawColor(ContextCompat.getColor(this@CMDActivity,
                R.color.color_light_blue))

        }
    }

    override fun onResume() {
        super.onResume()
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        checkUpdate()
        initData()
    }

    override fun onDestroy() {
        super.onDestroy()
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        unregisterReceiver(receiver)
        exitProcess(0)

    }


    private fun checkUpdate() {
        LogUtils.i("checkUpdate")
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        val httpClient: OkHttpClient.Builder = OkHttpClient.Builder()
        httpClient.addInterceptor(logging)

        val retrofit = Retrofit.Builder()
            .baseUrl("$HOST_API")
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient.build())
            .build()

        val service = retrofit.create(UpdateService::class.java)
        val info = UploadFileInfo()
        val version = packageManager.getPackageInfo(packageName, 0).versionName
        info.Versions = version.toDouble()
        LogUtils.i(info.toString())

        val param = EncryptUtils.encryptData(info, UploadFileInfo::class.java)
        val fileInfo = service.getUpdateApk(param!!)
        fileInfo.enqueue(object : Callback<UploadFileResult> {
            override fun onResponse(
                call: Call<UploadFileResult>,
                response: Response<UploadFileResult>
            ) {
                val fileResult = response.body()
                if (fileResult?.Value != null) {
                    if (!fileResult.Value.FileAddressV2.isNullOrEmpty() || !fileResult.Value.FileAddress.isNullOrEmpty()) {
                        // 下载apk后安装
                        LogUtils.i(fileResult.toString())
                        showUpdateApkInfo(fileResult)
                        return
                    }
                } else {
                    LogUtils.i("不需要升级")
                }
            }

            override fun onFailure(call: Call<UploadFileResult>, t: Throwable) {
                LogUtils.e(t.toString())
            }
        })
    }


    private fun showUpdateApkInfo(fileResult: UploadFileResult) {
        val builder = AlertDialog.Builder(this)
        val updateMsg =
            if (fileResult.Value == null || fileResult.Value.UpdateRemark.isNullOrEmpty()) "暂无" else fileResult.Value.UpdateRemark
        builder.setTitle(getString(R.string.update_version) + fileResult.Value?.Versions)
            .setMessage(updateMsg)
            .setPositiveButton(getString(R.string.update)) { dialog, _ ->
                dialog.dismiss()
                if (!fileResult.Value?.FileAddressV2.isNullOrEmpty()) {
                    loadNewVersion(fileResult.Value?.FileAddressV2)
                } else {
                    if (!fileResult.Value?.FileAddress.isNullOrEmpty()) {
                        val fileAddress = fileResult.Value?.FileAddress
                        val url = "$HOST_DOWN$fileAddress".replace("~", "")
                        loadNewVersion(url)
                    }
                }


            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun loadNewVersion(url: String?) {
        if (!url.isNullOrEmpty()) {
//            val url = "$HOST_DOWN$fileAddress".replace("~", "")
            Log.e("oyx", url)
            mDialog.setTitle(getString(R.string.upgradeing_soft_package))
            mDialog.max = 100
            mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
            mDialog.show()
            OkHttpUtils.get().url(url).build()
                .execute(object : FileCallBack(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path,
                    System.currentTimeMillis().toString() + "app.apk"
                ) {
                    override fun inProgress(progress: Float, total: Long, id: Int) {
                        if (!mDialog.isShowing) {
                            mDialog.show()
                        }
                        mDialog.progress = (100 * progress).toInt()
                    }

                    override fun onResponse(response: File?, id: Int) {
                        // 下载完毕
                        LogUtils.i(response?.path)
                        mDialog.dismiss()
                        val intent = Intent()
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        intent.addCategory(Intent.CATEGORY_DEFAULT)
                        intent.action = Intent.ACTION_VIEW
                        intent.setDataAndType(
                            Uri.fromFile(response),
                            "application/vnd.android.package-archive"
                        )
                        startActivity(intent)
                    }

                    override fun onError(call: okhttp3.Call?, e: Exception?, id: Int) {
                        Log.e("下载失败", e.toString())
                        ToastUtil.showShort(this@CMDActivity, "网络错误")
                        mDialog.dismiss()
                    }
                })
        }
    }

    private fun saveCache() {
        //保存到缓存
        GlobalScope.launch(Dispatchers.IO) {
            ACache.get(this@CMDActivity).clear()
            ACache.get(this@CMDActivity)
                .put(KEY_CACHE_LIST, Gson().toJson(models), 30 * ACache.TIME_DAY)
        }
    }


    override fun onAddSiteOk(site: String) {
        val newSite = site.replace(" ", "")
        models.add(Model(newSite,
            if (newSite.startsWith("site:")) newSite.substring(5) else newSite,
            SITE))
        modelAdapter.notifyDataSetChanged()
    }

    override fun onEditKWOK(position: Int, kw: String, site: String) {
        val newSite = site.replace(" ", "")
        Log.e("oyx", "position=$position,\tKW=$kw,\tsite=$newSite")
        models[position].keyword = kw.replace(" ", "")
        models[position].site = newSite
        modelAdapter.notifyDataSetChanged()
    }

    override fun onEditSiteOK(position: Int, site: String) {
        val newSite = site.replace(" ", "")
        Log.e("oyx", "position=$position,\tsite$newSite")
        models[position].keyword = newSite
        models[position].site = if (newSite.startsWith("site:")) newSite.substring(5) else newSite
        modelAdapter.notifyDataSetChanged()
    }

    override fun onAddKeySiteConfirm(kws: MutableList<String>, sites: MutableList<String>) {
        Log.i(MyTag, "kws$kws,sites$sites")

        for (i in 0..kws.lastIndex) {
            sites.forEach {
                if (kws[i].trim().isNotEmpty() && it.trim().isNotEmpty()) {
                    Log.i(MyTag, kws[i].replace(" ", ""))
                    Log.i(MyTag, it.replace(" ", ""))


                    models.add(Model(kws[i].replace(" ", ""), it.replace(" ", "")))
                }
            }
        }
        Log.i(MyTag, models.toString())
        modelAdapter.notifyDataSetChanged()
    }

    private fun startSearch(type: String) {
        val intent: Intent =
            when (type) {
                BrowserType.SouGou.i -> Intent(this, WebSouGouActivity::class.java)
                BrowserType.BaiDu.i -> Intent(this, WebActivity::class.java)
                BrowserType.TouTiao.i -> Intent(this, WebToutTiaoActivity::class.java)
                BrowserType.B360.i -> Intent(this, Web360Activity::class.java)
                BrowserType.SouGou_WX.i -> Intent(this, WebSouGouWXActivity::class.java)
                else -> Intent(this, WebActivity::class.java)
            }
        models.forEach {
            Log.e("oyx", "but_tonext" + it.toString())
        }

        intent.putExtra(KEYWORD_SITES, models.toTypedArray())

        if (models.size < 1) {
            ToastUtil.show(this, "请输入关键词或网址")
            return
        }

        //root permission
//        if (!ShellUtils.checkRootPermission()) {
//            AlertDialog.Builder(this).setTitle("请给应用授予root权限：")
//                .setMessage("操作：" +
//                        "点击root权限通知 或者\n" +
//                        "设置->授权管理->Root权限管理->打开${appName(this)}权限").setCancelable(true)
//                .setPositiveButton("确定", object : DialogInterface.OnClickListener {
//                    override fun onClick(dialog: DialogInterface?, which: Int) {
//                        startActivity(Intent(Settings.ACTION_SETTINGS));//直接跳转到设置界面
//                    }
//                })
//                .create().show()
//            return
//        }

        // sd permission
        if (!PermissionUtil.hasRequiredPermissions(this)) {
            PermissionUtil.askForRequiredPermissions(this)
            return
        }

//          4G
//            val statue = NetState.getNetWorkStatus(this)
//            val isOn = NetState.hasNetWorkConnection(this)
//            Log.e(MyTag, "isON" + isOn + ";statue" + statue)
//            if (isOn && statue == NetState.NETWORK_CLASS_4_G) {
//            startActivity(intent)
//            } else {
//                ToastUtil.show(this@CMDActivity, "请关闭wifi,打开4G,并能上网")
//            }

//             WIFI
        startActivity(intent)
    }

}



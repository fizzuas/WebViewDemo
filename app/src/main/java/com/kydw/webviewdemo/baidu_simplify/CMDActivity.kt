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
import com.kydw.webviewdemo.adapter.*
import com.kydw.webviewdemo.baidu_sougou.WebSouGouActivity
import com.kydw.webviewdemo.dialog.*
import com.kydw.webviewdemo.network.UpdateService
import com.kydw.webviewdemo.network.UploadFileInfo
import com.kydw.webviewdemo.network.UploadFileResult
import com.kydw.webviewdemo.util.*
import com.kydw.webviewdemo.util.shellutil.ShellUtils
import com.zhy.http.okhttp.OkHttpUtils
import com.zhy.http.okhttp.callback.FileCallBack
import kotlinx.android.synthetic.main.activity_c_m_d.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File


const val MyTag: String = "oyx"

class CMDActivity : AppCompatActivity(), DialogAddKeySite.OnConfirmClickListener,
    DialogInputSite.OnOKListener, DialogEditKW.OnKW2Listener, DialogEditSite.OnSite2Listener {


    var models = mutableListOf<Model>(
//        Model("钥匙机", "www.kydz-wx.com")
//        Model("钥匙机", "baike.baidu.com"),
//        Model("www.kydz-wx.com", "www.kydz-wx.com", SITE),
//        Model("手机", "www.oneplus.com")

    )

    private val modelAdapter: ModelAdapter = ModelAdapter(models)
    private val mDialog: ProgressDialog by lazy { ProgressDialog(this) }

    private val mDialogAddKeySite: DialogAddKeySite = DialogAddKeySite()
    private val mStartDialog: DialogInputSite = DialogInputSite()
    var intentFilter = IntentFilter("android.intent.action.AIRPLANE_MODE")

    var receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val isAirplaneModeOn = intent.getBooleanExtra("state", false)
            Log.i(MyTag, "Service state changed" + isAirplaneModeOn)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initData()
        setContentView(R.layout.activity_c_m_d)

        PermissionUtil.askForRequiredPermissions(this)

        registerReceiver(receiver, intentFilter)
        but_baidu.setOnClickListener {
            val intent = Intent(this, WebActivity::class.java)
            models.forEach {
                Log.e("oyx", "but_tonext" + it.toString())
            }

            intent.putExtra(KEYWORD_SITES, models.toTypedArray())

            if (models.size < 1) {
                ToastUtil.show(this, "请输入关键词或网址")
                return@setOnClickListener
            }

            //root permission
            if (!ShellUtils.checkRootPermission()) {
                AlertDialog.Builder(this).setTitle("请给应用授予root权限：")
                    .setMessage("操作：" +
                            "点击root权限通知 或者\n" +
                            "设置->授权管理->Root权限管理->打开${appName(this)}权限").setCancelable(true)
                    .setPositiveButton("确定", object : DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface?, which: Int) {
                            startActivity(Intent(Settings.ACTION_SETTINGS));//直接跳转到设置界面
                        }
                    })
                    .create().show()
                return@setOnClickListener
            }

            // sd permission
            if (!PermissionUtil.hasRequiredPermissions(this)) {
                PermissionUtil.askForRequiredPermissions(this)
                return@setOnClickListener
            }

//          4G
            val statue = NetState.getNetWorkStatus(this)
            val isOn = NetState.hasNetWorkConnection(this)
            Log.e(MyTag, "isON" + isOn + ";statue" + statue)
            if (isOn && statue == NetState.NETWORK_CLASS_4_G) {
            startActivity(intent)
            } else {
                ToastUtil.show(this@CMDActivity, "请关闭wifi,打开4G,并能上网")
            }

//            startActivity(intent)
        }

        but_sougou.setOnClickListener {
            val intent = Intent(this, WebSouGouActivity::class.java)
            models.forEach {
                Log.e("oyx", "but_tonext" + it.toString())
            }

            intent.putExtra(KEYWORD_SITES, models.toTypedArray())

            if (models.size < 1) {
                ToastUtil.show(this, "请输入关键词或网址")
                return@setOnClickListener
            }

            //root permission
            if (!ShellUtils.checkRootPermission()) {
                AlertDialog.Builder(this).setTitle("请给应用授予root权限：")
                    .setMessage("操作：" +
                            "点击root权限通知 或者\n" +
                            "设置->授权管理->Root权限管理->打开${appName(this)}权限").setCancelable(true)
                    .setPositiveButton("确定", object : DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface?, which: Int) {
                            startActivity(Intent(Settings.ACTION_SETTINGS));//直接跳转到设置界面
                        }
                    })
                    .create().show()
                return@setOnClickListener
            }

            // sd permission
            if (!PermissionUtil.hasRequiredPermissions(this)) {
                PermissionUtil.askForRequiredPermissions(this)
                return@setOnClickListener
            }

            val statue = NetState.getNetWorkStatus(this)
            val isOn = NetState.hasNetWorkConnection(this)
            Log.e(MyTag, "isON" + isOn + ";statue" + statue)

            if (isOn && statue == NetState.NETWORK_CLASS_4_G) {
                startActivity(intent)
            } else {
                ToastUtil.show(this@CMDActivity, "请关闭wifi,打开4G,并能上网")
            }
//            if (isOn && statue == NetState.NETWORK_WIFI) {
//                startActivity(intent)
//            } else {
//                ToastUtil.show(this@CMDActivity, "请关闭wifi,打开4G,并能上网")
//            }
        }

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

    }

    override fun onStop() {
        super.onStop()
        saveCache()

    }

    private fun initData() {
        GlobalScope.launch(Dispatchers.Main) {
            val content: String? =
                ACache.get(this@CMDActivity).getAsString(KEY_CACHE_LIST)
            if (content != null) {
                val type = object : TypeToken<MutableList<Model>>() {}.type
                models.addAll(Gson().fromJson(content, type))
                modelAdapter.notifyDataSetChanged()
            }
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
    }

    override fun onDestroy() {
        super.onDestroy()
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        unregisterReceiver(receiver)
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
                    if (!fileResult.Value.FileAddress.isNullOrEmpty()) {
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
                loadNewVersion(fileResult.Value?.FileAddress)
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun loadNewVersion(fileAddress: String?) {
        if (!fileAddress.isNullOrEmpty()) {
            val url = "$HOST_DOWN$fileAddress".replace("~", "")
            Log.e("下载", url)
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
        models.add(Model(site, if (site.startsWith("site:")) site.substring(5) else site, SITE))
        modelAdapter.notifyDataSetChanged()
    }

    override fun onEditKWOK(position: Int, kw: String, site: String) {
        Log.e("oyx", "position=$position,\tKW=$kw,\tsite=$site")
        models[position].keyword = kw
        models[position].site = site
        modelAdapter.notifyDataSetChanged()
    }

    override fun onEditSiteOK(position: Int, site: String) {
        Log.e("oyx", "position=$position,\tsite$site")
        models[position].keyword = site
        models[position].site =  if (site.startsWith("site:")) site.substring(5) else site
        modelAdapter.notifyDataSetChanged()
    }

    override fun onAddKeySiteConfirm(kws: MutableList<String>, sites: MutableList<String>) {
        Log.i(MyTag, "kws" + kws.toString() + ",sites" + sites.toString())

        for (i in 0..kws.lastIndex) {
            sites.forEach {
                if (kws[i].trim().isNotEmpty() && it.trim().isNotEmpty()) {
                    Log.i(MyTag, kws[i])
                    Log.i(MyTag, it)

                    models.add(Model(kws[i], it))
                }
            }
        }
        Log.i(MyTag, models.toString())
        modelAdapter.notifyDataSetChanged()
    }


}



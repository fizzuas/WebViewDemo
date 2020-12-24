package com.kydw.webviewdemo.pc

import android.annotation.SuppressLint
import android.content.*
import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.chad.library.adapter.base.listener.OnItemSwipeListener
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.kydw.webviewdemo.CIRCLE_COUNT
import com.kydw.webviewdemo.DIALOG_INPUT
import com.kydw.webviewdemo.KEYWORD_SITES
import com.kydw.webviewdemo.R
import com.kydw.webviewdemo.adapter.Model
import com.kydw.webviewdemo.adapter.ModelAdapter
import com.kydw.webviewdemo.dialog.DialogInput
import com.kydw.webviewdemo.dialog.JAlertDialog
import com.kydw.webviewdemo.util.*
import com.kydw.webviewdemo.util.shellutil.ShellUtils
import kotlinx.android.synthetic.main.activity_c_m_d.*


const val MyTag: String = "oyx"

class CMDActivity : AppCompatActivity(), DialogInput.OnConfirmClickListener {
//    var models = mutableListOf<Model>(Model("关键词", "网址"), Model("钥匙机", "www.kydz-wx.com"))

        var models = mutableListOf<Model>(Model("关键词", "网址"))
    private val modelAdapter: ModelAdapter = ModelAdapter(models)

    private val mDialogInput: DialogInput = DialogInput()
    private var mLoadingDbDialog: JAlertDialog? = null

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
        setContentView(R.layout.activity_c_m_d)

        PermissionUtil.askForRequiredPermissions(this)

        registerReceiver(receiver, intentFilter)
        but_tonext.setOnClickListener {
            val intent = Intent(this, WebActivity::class.java)
            models.subList(1, models.size).forEach {
                Log.e("oyx", "but_tonext" + it.toString())
            }
            intent.putExtra(KEYWORD_SITES, models.subList(1, models.size).toTypedArray())
            val count = et_count.text.toString().toIntOrNull()

            if (count != null) {
                intent.putExtra(CIRCLE_COUNT, count)
            } else {
                ToastUtil.show(this, "请输入循环次数")
                return@setOnClickListener
            }

            if (models.size < 2) {
                ToastUtil.show(this, "请输入关键词")
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


        }

        but_addkw.setOnClickListener {
            mDialogInput.show(supportFragmentManager, DIALOG_INPUT)
        }

        recy_configs.apply {
            this.layoutManager = LinearLayoutManager(this.context)
            modelAdapter.setOnItemClickListener { _, view, position ->
                Log.i(MyTag, models[position].toString())
            }
            adapter = modelAdapter
            modelAdapter.draggableModule.isSwipeEnabled = true
            modelAdapter.draggableModule.setOnItemSwipeListener(onItemSwipeListener)
        }
        tv_version_name.text = "version:" + getAppVersionName(this)

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
    }

    override fun onDestroy() {
        super.onDestroy()
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        unregisterReceiver(receiver)
    }

    override fun onConfirm(kw: String, sites: MutableList<String>) {
        Log.i(MyTag, "kw" + kw + ",sites" + sites.toString())
        sites.forEach {
            models.add(Model(kw, it))
        }
        modelAdapter.notifyDataSetChanged()
    }
}



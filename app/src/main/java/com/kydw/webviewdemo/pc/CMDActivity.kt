package com.kydw.webviewdemo.pc

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
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
import com.kydw.webviewdemo.util.ToastUtil
import kotlinx.android.synthetic.main.activity_c_m_d.*


const val MyTag: String = "oyx"

class CMDActivity : AppCompatActivity(), DialogInput.OnConfirmClickListener {
    var models = mutableListOf<Model>(Model("关键词", "网址"), Model("钥匙机","www.kydz-wx.com"))
    private val modelAdapter: ModelAdapter = ModelAdapter(models)

    private val mDialogInput: DialogInput = DialogInput()

    var intentFilter = IntentFilter("android.intent.action.AIRPLANE_MODE")

    var receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val isAirplaneModeOn = intent.getBooleanExtra("state", false)
            Log.i(MyTag, "Service state changed" + isAirplaneModeOn)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_c_m_d)
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
                ToastUtil.show(this, "循环次数为正整数")
            }
            if(models.size<2){

                ToastUtil.show(this,"请输入关键词")
            }else{
                startActivity(intent)

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
        modelAdapter.setOnItemClickListener(OnItemClickListener { adapter, view, position ->
            //编辑
        })
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
            isCurrentlyActive: Boolean,
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



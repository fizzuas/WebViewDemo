package com.kydw.webviewdemo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.kydw.webviewdemo.shellutil.CMD
import com.kydw.webviewdemo.shellutil.ShellUtils
import kotlinx.android.synthetic.main.activity_c_m_d.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


const val MyTag: String = "oyx"

class CMDActivity : AppCompatActivity() {
    var intentFilter = IntentFilter("android.intent.action.AIRPLANE_MODE")

    var receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val isAirplaneModeOn = intent.getBooleanExtra("state", false)
            Log.i(MyTag, "Service state changed"+isAirplaneModeOn)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_c_m_d)

        registerReceiver(receiver, intentFilter)
        button2.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        button4.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {

                val result0 = ShellUtils.execCommand(CMD.IP + " rmnet_data0", true)
                val sucMsg0 = result0.successMsg
                Log.i(MyTag, "result0.sucMsg0=" + sucMsg0.toString() + ", ")


                ShellUtils.execCommand(CMD.AIRPLANE_MODE_ON, true)
                delay(1000)
                ShellUtils.execCommand(CMD.AIRPLANE_MODE_OFF, true)

                //关掉飞行时，4G 需要慢慢打开
                delay(5000)
                val result1 = ShellUtils.execCommand(CMD.IP + " rmnet_data0", true)
                Log.i(MyTag, "result1.sucMsg=" + result1.successMsg.toString())
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)

    }
}
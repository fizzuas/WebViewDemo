package com.kydw.webviewdemo.test

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.kydw.webviewdemo.R
import com.kydw.webviewdemo.baidu_simplify.MyTag
import com.kydw.webviewdemo.util.isOnline
import kotlinx.android.synthetic.main.activity_test_net.*

class TesNetActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_net)


        button2.setOnClickListener {
            val hasNet = isOnline()
            Log.e(MyTag, "has NET" + hasNet)
        }
    }
}
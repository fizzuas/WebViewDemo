package com.sogou.activity.test

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.sogou.activity.R
import com.sogou.activity.baidu_simplify.MyTag
import com.sogou.activity.util.isOnline
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
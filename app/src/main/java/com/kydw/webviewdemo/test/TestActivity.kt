package com.kydw.webviewdemo.test

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.kydw.webviewdemo.R
import com.kydw.webviewdemo.pc.MyTag
import kotlinx.android.synthetic.main.activity_test2.*

class TestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test2)


        button2.setOnClickListener {
//            Log.e(MyTag, "isOnline" + isOnline().toString())
        }
    }
}
package com.sogou.activity.test

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sogou.activity.R
import kotlinx.android.synthetic.main.activity_test_coroutines.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

class TestCoroutinesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_coroutines)
        GlobalScope.launch(Dispatchers.Main) {
            do {
                delay(2000)
//                ShellUtils.execCommand(CMD.DATA_ON, true)
//                if (NetState.hasNetWorkConnection(this@TestCoroutinesActivity) && isOnline()) {
                    textView3.text = Date().time.toString()
//                    return@launch
//                }
            } while (true)
        }


    }
}
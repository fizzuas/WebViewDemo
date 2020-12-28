package com.kydw.webviewdemo.test

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kydw.webviewdemo.R
import com.kydw.webviewdemo.dialog.JAlertDialog
import kotlinx.android.synthetic.main.activity_test_dialog.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TestDialogActivity : AppCompatActivity() {
    private var mLoadingCheckNetDialog: JAlertDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_dialog)
        if (mLoadingCheckNetDialog == null) {
            mLoadingCheckNetDialog =
                JAlertDialog.Builder(this@TestDialogActivity)
                    .setContentView(R.layout.dialog_waiting_net)
                    .setWidth_Height_dp(300, 120).setCancelable(false)
                    .create()
        }
        button4.setOnClickListener {

            mLoadingCheckNetDialog?.show()
            mLoadingCheckNetDialog?.show()
            mLoadingCheckNetDialog?.show()
            GlobalScope.launch (Dispatchers.Main){
                delay(2000)
                mLoadingCheckNetDialog?.dismiss()
            }

        }
        button5.setOnClickListener {
        }
    }
}
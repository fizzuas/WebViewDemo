package com.kydw.webviewdemo

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

    }

    override fun onStart() {
        super.onStart()
        restore()
    }

    private fun restore() {
        val loopCount = getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getInt(LOOP_COUNT, 0)
        et_loop.setText(loopCount.toString())

        val singleLoopMaxPages = getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getInt(
            SINGLE_LOOP_PAGE_MAX,
            SINGLE_LOOP_PAGE_MAX_DEFAULT)
        et_page_max.setText(singleLoopMaxPages.toString())

        val switchIPPages =
            getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getInt(SWITCH_IP_PAGE_NUM, 0)
        et_ip_switch_pages.setText(switchIPPages.toString())

        val lookTime =
            getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getInt(PAGE_LOOP_TIME, 5)
        et_look_time.setText(lookTime.toString())
    }

    override fun onStop() {
        super.onStop()
        save()
    }

    private fun save() {
        val sp = getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
        sp.edit {
            val loopCount = et_loop.text.toString().toIntOrNull()
            if (loopCount != null) {
                sp.edit {
                    Log.i("oyx", "et_page_max_put" + loopCount)
                    putInt(LOOP_COUNT, loopCount)
                }
            }


            val pageMax = et_page_max.text.toString().toIntOrNull()
            if (pageMax != null) {
                sp.edit {
                    putInt(SINGLE_LOOP_PAGE_MAX, pageMax)
                }
            }

            val ipPages = et_ip_switch_pages.text.toString().toIntOrNull()
            if (ipPages != null) {
                sp.edit {
                    putInt(SWITCH_IP_PAGE_NUM, ipPages)
                }
            }

            val loopTime = et_look_time.text.toString().toIntOrNull()
            if (loopTime != null) {
                sp.edit {
                    putInt(PAGE_LOOP_TIME, loopTime)
                }
            }

        }
    }


}
package com.sogou.activity

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_img.*


class ImgActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_img)
//        Glide.with(this).load("http://192.168.0.111:9355/上传排行榜.png").into(img_board)


        scrool_board.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                if (event?.action == MotionEvent.ACTION_UP) {
                    scrool_board.requestDisallowInterceptTouchEvent(false)
                } else {
                    scrool_board.requestDisallowInterceptTouchEvent(true)
                }
                return false
            }

        })
    }
}
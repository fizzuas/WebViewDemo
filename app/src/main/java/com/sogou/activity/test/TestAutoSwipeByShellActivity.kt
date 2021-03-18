package com.sogou.activity.test

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.sogou.activity.R
import com.sogou.activity.baidu_simplify.MyTag
import com.sogou.activity.util.getScreenRealHeight
import com.sogou.activity.util.getScreenRealWidth
import com.sogou.activity.util.statueHeight
import kotlinx.android.synthetic.main.activity_shell_click.*

class TestAutoSwipeByShellActivity : AppCompatActivity() {
    //记录btn初始位置
    var startX = 0
    var startY = 0

    //记录手指抬起后的坐标
    var moveX = 0
    var moveY = 0

    //isclick这个布尔值在全局声明，是解决两种事件冲突的关键，
    // 因为只有当onTouch返回false的时候，click事件才会生效，
    // 所以我们利用isclick值来控制事件的响应，当按下的时候设其为false，倘若不移动，返回false，响应click的事件
    //，如果按钮坐标发生移动就设置为true，这样在移动完按钮以后就不会同时响应OnClick事件了。
    var isclick = false


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shell_click)

        button.setOnClickListener(View.OnClickListener {
            Toast.makeText(this,
                "哈哈",
                Toast.LENGTH_SHORT).show()
            Log.e(MyTag, "button_click")
        })


        button.setOnTouchListener(View.OnTouchListener { view, motionEvent ->
            Log.i(MyTag,
                "x" + motionEvent.rawX.toString() + "," + "y" + motionEvent.rawX.toString())

            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    isclick = false //当按下的时候设置isclick为false
                    //1.获取控件的初始位置
                    /*getX:手按下的位置和当前控件(手要移动的控件)x轴的位置
                             * getRawX:手按下的位置与x轴的距离
                             * */
                    //motionEvent.getX();
                    startX = motionEvent.rawX.toInt()
                    startY = motionEvent.rawY.toInt()
                }
                MotionEvent.ACTION_MOVE -> {
                    isclick = true //当按下的时候设置isclick为false
                    //2.记录手指抬起后的位置
                    moveX = motionEvent.rawX.toInt()
                    moveY = motionEvent.rawY.toInt()
                    //3.计算偏移量
                    val disX: Int = moveX - startX
                    val disY: Int = moveY - startY
                    //让偏移量设置给button的位置上去
                    //4.移动后的控件所在屏幕的(左，上)角位置,
                    val left: Int = button.getLeft() + disX //当前控件左边缘与屏幕左边缘的间距
                    val top: Int = button.getTop() + disY //上边缘间距
                    val right: Int = button.getRight() + disX //右侧坐标，都是距左上角的距离
                    val bottom: Int = button.getBottom() + disY //底部坐标
                    //7.容错处理
                    if (left < 0) {
                        return@OnTouchListener true
                    }
                    if (right > (getSystemService(WINDOW_SERVICE) as WindowManager).defaultDisplay.width) {
                        return@OnTouchListener true
                    }
                    //上边缘不能超过屏幕可显示区域
                    if (top < 0) {
                        return@OnTouchListener true
                    }
                    //下边缘(屏幕的高度-22(状态栏高度))
                    //状态栏高度没有api去拿到，只能手动去指定一个高度
                    if (bottom > (getSystemService(WINDOW_SERVICE) as WindowManager).defaultDisplay.height - 150) {
                        return@OnTouchListener true
                    }
                    //5.告知移动的控件按计算出来的坐标去展示
                    button.layout(left, top, right, bottom)

                    //6.重至btn的位置
                    startX = motionEvent.rawX.toInt()
                    startY = motionEvent.rawY.toInt()
                }
                MotionEvent.ACTION_UP -> {
                }
            }
            /*
                     * 返回值
                     * false:不相应事件，
                     * true响应事件
                     * */
            isclick
        })

        button3.setOnClickListener {
            Log.i(MyTag, "height=" + getScreenRealHeight(this).toString())
            Log.i(MyTag, "width=" + getScreenRealWidth(this).toString())
            Log.i(MyTag, "statue=" + statueHeight(application))


//            val x0 = (button.left + button.right) / 2
//            val y0 = (button.top + button.bottom) / 2 + statueHeight(application)
//            val x1 = x0 + 100
//            val y1 = y0 + 100
//            Log.i(MyTag, "$x0,$y0;$x1,$y1")
//            GlobalScope.launch {
//                delay(500)
//                val result = ShellUtils.execSwipe(x0, y0, x1, y1, 500)
//                Log.i(MyTag, result.toString())
//            }
        }
    }
}
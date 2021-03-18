package com.sogou.activity.util;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

/**
 * @author dell
 * on 2016/7/28 0028.
 * Toast工具类
 * 确保客户端只存在一个Toast对象，防止多次点击时Toast无限弹出
 */
public class ToastUtil {
    /**
     * 显示Toast
     *
     * @param context 上下文对象
     * @param message 文本消息
     */
    public static void show(final Context context, final String message) {
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context.getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        });
    }

    public static void showShort(final Context context, final String message) {
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context.getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

}

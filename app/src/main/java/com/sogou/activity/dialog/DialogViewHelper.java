package com.sogou.activity.dialog;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.lang.ref.WeakReference;

/**
 * 万能弹窗辅助处理类，最终事件的走向
 */
public  class DialogViewHelper {

    private View mContentView = null;
    // 防止内存侧漏
    private SparseArray<WeakReference<View>> mViews;

    private JAlertDialog mDialog;

    public DialogViewHelper(Context context, int layoutResId,JAlertDialog dialog) {
        this();
        mContentView = LayoutInflater.from(context).inflate(layoutResId, null);
        this.mDialog=dialog;
    }


    public DialogViewHelper() {
        mViews = new SparseArray<>();
    }

    /**
     * 设置布局View
     *
     * @param contentView
     */
    public void setContentView(View contentView,JAlertDialog dialog) {
        this.mContentView = contentView;
        this.mDialog=dialog;
    }

    /**
     * 获取ContentView
     *
     * @return
     */
    public View getContentView() {
        return mContentView;
    }
    /**
     * 设置文本
     *
     * @param viewId
     * @param text
     */
    public void setText(int viewId, CharSequence text) {
        // 每次都 findViewById   减少findViewById的次数
        TextView tv = getView(viewId);
        tv.setVisibility(View.VISIBLE);
        if (tv != null) {
            tv.setText(text);
        }
    }
    /**
     * 设置点击事件
     *
     * @param viewId
     * @param listener
     */
    public void setOnclickListener(int viewId, View.OnClickListener listener) {
        View view = getView(viewId);
        view.setVisibility(View.VISIBLE);
        if (view != null) {
            view.setOnClickListener(listener);
        }
    }

    /**
     * 设置点击事件,带Dialog参数,方便点击按钮关闭dialog
     *
     * @param viewId
     * @param listener
     */
    public void setOnSpeclickListener(int viewId, final OnSpeClickListener listener) {
        View view = getView(viewId);
        view.setVisibility(View.VISIBLE);
        if (view != null) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.OnSpeClick(mDialog,v);
                }
            });
        }
    }

    /**
     * 设置复选框选中事件
     * @param viewId
     * @return
     */
    public void setOnChecklickListener(int viewId, final OnCheckListener listener) {
        CheckBox view =mContentView.findViewById(viewId);
        if (view != null) {
            view.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    listener.OnCheckClick(mDialog,buttonView,isChecked);
                }
            });
        }
    }




    public <T extends View> T getView(int viewId) {
        WeakReference<View> viewReference = mViews.get(viewId);
        //debug时或许小朋友会有很多问号？可是要搞清楚你的dialog是不是又被new了，new了肯定没有走复用啊
        View view = null;
        if (viewReference != null) { //复用缓存
            view = viewReference.get();
        }

        if (view == null) {
            view = mContentView.findViewById(viewId);
            if (view != null) {
                mViews.put(viewId, new WeakReference<>(view));
                return (T) new WeakReference<>(view).get();
            }
        }
        return (T) view;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public  int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    //特别的爱给特别的你
    public interface OnSpeClickListener{
        void OnSpeClick(JAlertDialog dialog, View view);

    }


    public interface OnCheckListener{
        void OnCheckClick(JAlertDialog mDialog, CompoundButton buttonView, boolean isChecked);
    }



}

package com.sogou.activity.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;


/**
   万能弹窗控制器
 */
class AlertController {

    private JAlertDialog mDialog;
    private Window mWindow;

    private DialogViewHelper mViewHelper;

    public AlertController(JAlertDialog dialog, Window window) {
        this.mWindow = window;
        this.mDialog=dialog;
    }

    public void setViewHelper(DialogViewHelper viewHelper) {
        this.mViewHelper = viewHelper;
    }


    public <T extends View> T getView(int viewId) {
        return mViewHelper.getView(viewId);
    }
    /**
     * 设置文本
     *
     * @param viewId
     * @param text
     */
    public void setText(int viewId, CharSequence text) {
        mViewHelper.setText(viewId, text);
    }
    /**
     * 设置点击事件
     *
     * @param viewId
     * @param listener
     */
    public void setOnclickListener(int viewId, View.OnClickListener listener) {
        mViewHelper.setOnclickListener(viewId, listener);
    }

    /**
     * 设置选中事件
     *
     * @param viewId
     * @param listener
     */
    public void setOnCheckListener(int viewId, DialogViewHelper.OnCheckListener listener) {
        mViewHelper.setOnChecklickListener(viewId, listener);
    }

    /**
     * 设置特别回调点击事件
     *
     * @param viewId
     * @param listener
     */
    public void setOnSpeclickListener(int viewId, DialogViewHelper.OnSpeClickListener listener) {
        mViewHelper.setOnSpeclickListener(viewId, listener);
    }
    /**
     * 获取Dialog
     *
     * @return
     */
    public JAlertDialog getDialog() {
        return mDialog;
    }

    /**
     * 获取Dialog的Window
     *
     * @return
     */
    public Window getWindow() {
        return mWindow;
    }

    public static class AlertParams {
        public Context mContext;
        public int mThemeResId;
        // 点击空白是否能够取消  默认点击阴影可以取消
        public boolean mCancelable = true;
        // dialog Cancel监听
        public DialogInterface.OnCancelListener mOnCancelListener;
        // dialog Dismiss监听
        public DialogInterface.OnDismissListener mOnDismissListener;
        // dialog Key监听
        public DialogInterface.OnKeyListener mOnKeyListener;
        // 布局View
        public View mView;
        // 布局layout id
        public int mViewLayoutResId;
        // 存放字体的修改,当key为Int型时利用SparseArray能节省内存
        public SparseArray<CharSequence> mTextArray = new SparseArray<>();
        // 存放点击事件,当key为Int型时利用SparseArray能节省内存
        public SparseArray<View.OnClickListener> mClickArray = new SparseArray<>();
        // 存放点击事件,当key为Int型时利用SparseArray能节省内存
        public SparseArray<DialogViewHelper.OnCheckListener> mCheckArray = new SparseArray<>();
        // 存放点击事件,当key为Int型时利用SparseArray能节省内存
        public SparseArray<DialogViewHelper.OnSpeClickListener> mSpeClickArray = new SparseArray<>();
        // 宽度
        public int mWidth = ViewGroup.LayoutParams.WRAP_CONTENT;
        // 动画
        public int mAnimations = 0;
        // 位置
        public int mGravity = Gravity.CENTER;
        // 高度
        public int mHeight = ViewGroup.LayoutParams.WRAP_CONTENT;

        public AlertParams(Context context, int themeResId) {
            this.mContext = context;
            this.mThemeResId = themeResId;
        }
        /**
         * 绑定和设置参数
         *
         * @param mAlert
         */
        public void apply(AlertController mAlert) {
            // 1. 设置Dialog布局  DialogViewHelper
            DialogViewHelper viewHelper = null;
            if (mViewLayoutResId != 0) {
                viewHelper = new DialogViewHelper(mContext, mViewLayoutResId,mAlert.getDialog());
            }

            if (mView != null) {
                viewHelper = new DialogViewHelper();
                viewHelper.setContentView(mView,mAlert.getDialog());
            }

            if (viewHelper == null) {
                throw new IllegalArgumentException("请设置布局setContentView()");
            }

            // 给Dialog 设置布局
            mAlert.getDialog().setContentView(viewHelper.getContentView());

            // 设置 Controller的辅助类
            mAlert.setViewHelper(viewHelper);

            // 2.设置文本
            int textArraySize = mTextArray.size();
            for (int i = 0; i < textArraySize; i++) {
                mAlert.setText(mTextArray.keyAt(i), mTextArray.valueAt(i));
            }


            // 3.设置点击
            int clickArraySize = mClickArray.size();
            for (int i = 0; i < clickArraySize; i++) {
                mAlert.setOnclickListener(mClickArray.keyAt(i), mClickArray.valueAt(i));
            }

            //3.1.设置checkBox回调的点击事件
            int checkClickArraySize=mCheckArray.size();
            for (int i=0;i<checkClickArraySize;i++){
                mAlert.setOnCheckListener(mCheckArray.keyAt(i), mCheckArray.valueAt(i));
            }


            //4.设置特定回调的点击事件
            int speClickArraySize=mSpeClickArray.size();
            for (int i=0;i<speClickArraySize;i++){
                mAlert.setOnSpeclickListener(mSpeClickArray.keyAt(i), mSpeClickArray.valueAt(i));
            }

            // 4.配置自定义的效果  全屏  从底部弹出    默认动画
            Window window = mAlert.getWindow();
            // 设置位置
            window.setGravity(mGravity);

            // 设置动画
            if (mAnimations != 0) {
                window.setWindowAnimations(mAnimations);
            }

            // 设置宽高
            WindowManager.LayoutParams params = window.getAttributes();
            params.width =viewHelper.dip2px(mContext,mWidth)  ;
            params.height =viewHelper.dip2px(mContext,mHeight)  ;

            window.setAttributes(params);
        }
    }





}

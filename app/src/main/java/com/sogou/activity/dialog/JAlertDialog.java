package com.sogou.activity.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import com.sogou.activity.R;


/**
 * JULIAN_CYW
 * Description: 自定义万能Dialog，view层直接使用这个 new JAlertDialog.Builder(this)
 */
public class JAlertDialog extends Dialog {

    private AlertController mAlertController;


    private JAlertDialog(Context context, int themeResId) {
        super(context, themeResId);
        mAlertController = new AlertController(this, getWindow());
    }

    public void setText(int viewId, CharSequence text) {
        mAlertController.setText(viewId,text);
    }
    public <T extends View> T getView(int viewId){
        return mAlertController.getView(viewId);
    }

    public static class Builder {

        private final AlertController.AlertParams P;

        public Builder(Context context) {
            this(context, R.style.dialog);
        }

        public Builder(Context context, int themeResId) {
            P = new AlertController.AlertParams(context, themeResId);
        }

        public Builder setContentView(View view) {
            P.mView = view;
            P.mViewLayoutResId = 0;
            return this;
        }

        // 设置布局内容的layoutId
        public Builder setContentView(int layoutId) {
            P.mView = null;
            P.mViewLayoutResId = layoutId;
            return this;
        }

        // 设置文本
        public Builder setText(int viewId, CharSequence text) {
            P.mTextArray.put(viewId, text);
            return this;
        }

        // 设置点击事件
        public Builder setOnClickListener(int view, View.OnClickListener listener) {
            P.mClickArray.put(view, listener);
            return this;
        }


        //设置check事件
        public Builder setOnCheckListener(int view, DialogViewHelper.OnCheckListener listener) {
            P.mCheckArray.put(view, listener);
            return this;
        }

        // 设置带dialog回调的特别点击事件
        public Builder setOnSpeClickListener(int view, DialogViewHelper.OnSpeClickListener listener) {
            P.mSpeClickArray.put(view, listener);
            return this;
        }




        // 配置一些万能的参数
        public Builder fullWidth() {
            P.mWidth = ViewGroup.LayoutParams.MATCH_PARENT;
            return this;
        }

        /**
         * 从底部弹出
         *
         * @param isAnimation 是否有动画
         * @return
         */
        public Builder formBottom(boolean isAnimation) {
            if (isAnimation) {
                P.mAnimations = R.style.dialog_from_bottom_anim;
            }
            P.mGravity = Gravity.BOTTOM;
            return this;
        }


        /**
         * 设置Dialog的宽高，直接传dp值
         *
         * @param width
         * @param height
         * @return
         */
        public Builder setWidth_Height_dp(int width, int height) {
            P.mWidth = width;
            P.mHeight = height;
            return this;
        }

        /**
         * 添加默认动画
         *
         * @return
         */
        public Builder addDefaultAnimation() {
            P.mAnimations = R.style.dialog_scale_anim;
            return this;
        }

        /**
         * 设置动画
         *
         * @param styleAnimation
         * @return
         */
        public Builder setAnimations(int styleAnimation) {
            P.mAnimations = styleAnimation;
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            P.mCancelable = cancelable;
            return this;
        }


        public Builder setOnCancelListener(OnCancelListener onCancelListener) {
            P.mOnCancelListener = onCancelListener;
            return this;
        }

        public Builder setOnDismissListener(OnDismissListener onDismissListener) {
            P.mOnDismissListener = onDismissListener;
            return this;
        }

        public Builder setOnKeyListener(OnKeyListener onKeyListener) {
            P.mOnKeyListener = onKeyListener;
            return this;
        }


        public JAlertDialog create() {
            // Context has already been wrapped with the appropriate theme.
            final JAlertDialog dialog = new JAlertDialog(P.mContext, P.mThemeResId);
            P.apply(dialog.mAlertController);
            dialog.setCancelable(P.mCancelable);
            if (P.mCancelable) {
                dialog.setCanceledOnTouchOutside(true);
            }
            if (P.mOnCancelListener!=null)
            dialog.setOnCancelListener(P.mOnCancelListener);
            if (P.mOnDismissListener!=null)
            dialog.setOnDismissListener(P.mOnDismissListener);
            if (P.mOnKeyListener != null)
                dialog.setOnKeyListener(P.mOnKeyListener);

            return dialog;
        }

        public void show() {
            create().show();
        }
    }
}

package com.tencent.testshengao.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;

import com.tencent.testshengao.R;


/**
 * 用法
 * val outlinedialog= AlertDialog.Builder(this)
 * .setContentView(R.layout.my_dialog)
 * .fullWidth()
 * .fromBottom(true)
 * .setOnClickListener(R.id.share_qq,{view->toast { "QQ被点击" }})
 * .show()
 * val editText = outlinedialog.getView<EditText>(R.id.et)
 * outlinedialog.setOnClickListener(R.id.share_weibo,{view->toast { editText.text.toString().trim() }})
 */
public class AlertDialog extends Dialog {

    private AlertController mAlert;

    /*构造方法*/
    private AlertDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        mAlert = new AlertController(this, getWindow());
    }


    /*设置文本*/
    public void setText(int viewId, CharSequence text) {
        mAlert.setText(viewId, text);
    }

    /**
     * 减少findViewByid的次数
     *
     * @param viewId
     * @return
     */
    public <T extends View> T getView(int viewId) {
        return mAlert.getView(viewId);
    }

    /*设置点击事件*/
    public void setOnClickListener(int viewId, View.OnClickListener valueAt) {
        mAlert.setOnClickListener(viewId, valueAt);
    }


    /*内部类*/
    public static class Builder {

        private final AlertController.AlertParams P;

        public Builder(@NonNull Context context) {
            this(context, R.style.dialog);
        }

        public Builder(@NonNull Context context, @StyleRes int themeResId) {
            /*通过构造方法注入对象*/
            P = new AlertController.AlertParams(context, themeResId);
        }

        /*设置自定义布局*/
        public Builder setContentView(View view) {
            P.mView = view;
            P.mViewLayoutResId = 0;
            return this;
        }

        /*设置自定义布局*/
        public Builder setContentView(int layoutResId) {
            P.mView = null;
            P.mViewLayoutResId = layoutResId;
            return this;
        }

        /*设置取消按钮监听*/
        public Builder setOnCancelListener(OnCancelListener onCancelListener) {
            P.mOnCancelListener = onCancelListener;
            return this;
        }

        /*设置对话框消失监听*/
        public Builder setOnDismissListener(OnDismissListener onDismissListener) {
            P.mOnDismissListener = onDismissListener;
            return this;
        }

        /*设置按键监听*/
        public Builder setOnKeyListener(OnKeyListener onKeyListener) {
            P.mOnKeyListener = onKeyListener;
            return this;
        }

        /*设置文本*/
        public Builder setText(int viewId, CharSequence text) {
            P.textArray.put(viewId, text);
            return this;
        }

        /*设置点击事件*/
        public Builder setOnClickListener(int viewId, View.OnClickListener listener) {
            P.clickArray.put(viewId, listener);
            return this;
        }

        /*设置点击屏幕外部是否消失dialog*/
        public Builder setCancelable(boolean mCancelable) {
            P.mCancelable = mCancelable;
            return this;
        }


        /*设置点击返回键是否消失*/
        public Builder setback(boolean mCancelable) {
            P.back = mCancelable;
            return this;
        }


        /*设置宽度全屏*/
        public Builder fullWidth() {
            P.mWidth = ViewGroup.LayoutParams.MATCH_PARENT;
            return this;
        }

        /*设置从底部弹出*/
        public Builder fromBottom(boolean isAnimation) {
            if (isAnimation) {
                P.mAnimation = R.style.dialog_from_bottom_animation;
            }
            P.mGravity = Gravity.BOTTOM;
            return this;
        }

        /*设置从顶部弹出*/
        public Builder fromTop(int x, int y) {
            P.x = x;
            P.y = y;
            P.mGravity = Gravity.TOP;
            return this;
        }


        /**
         * 设置dialog的宽高
         *
         * @param width
         * @param height
         * @return
         */
        public Builder setWidthAndHeight(int width, int height) {
            P.mWidth = width;
            P.mHeight = height;
            return this;
        }

        /**
         * 设置默认动画
         *
         * @return
         */
        public Builder setDefaultAnimation() {
            P.mAnimation = R.style.scale_anim;
            return this;
        }

        /**
         * 添加默认动画
         *
         * @param styleAnim
         * @return
         */
        public Builder setAnimation(int styleAnim) {
            P.mAnimation = styleAnim;
            return this;
        }

        /**
         * 设置dialog背景的透明度
         *
         * @param v
         * @return
         */
        public Builder setBackgroundTransparence(float v) {
            P.mTransparence = v;
            return this;
        }

        /**
         * true表示不影响Activity的事件触发
         *
         * @param mFouse
         * @return
         */
        public Builder setNoFouse(boolean mFouse) {
            P.mFouse = mFouse;
            return this;
        }


        /*真正去创建dialog的方法*/
        private AlertDialog create() {
            final AlertDialog dialog = new AlertDialog(P.mContext, P.mThemeResId);
            /*绑定数据*/
            P.apply(dialog.mAlert);
            /*设置点击空白是否能够取消*/
            dialog.setCanceledOnTouchOutside(P.mCancelable);
            dialog.setCancelable(P.back||P.mCancelable);
            /*设置一些监听*/
            dialog.setOnCancelListener(P.mOnCancelListener);
            dialog.setOnDismissListener(P.mOnDismissListener);
            if (P.mOnKeyListener != null) {
                dialog.setOnKeyListener(P.mOnKeyListener);
            }
            return dialog;

        }

        /*让dialog去显示*/
        public AlertDialog show() {
            final AlertDialog dialog = create();
            /*解决弹出dialog后，底部按键显示问题*/
//            outlinedialog.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
//            outlinedialog.getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
//                @Override
//                public void onSystemUiVisibilityChange(int visibility) {
//                    int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
//                            //布局位于状态栏下方
//                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
//                            //全屏
//                            View.SYSTEM_UI_FLAG_FULLSCREEN |
//                            //隐藏导航栏
//                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
//                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
//                    uiOptions |= 0x00001000;
//                    outlinedialog.getWindow().getDecorView().setSystemUiVisibility(uiOptions);
//                }
//            });
            dialog.show();
            return dialog;
        }


    }
}

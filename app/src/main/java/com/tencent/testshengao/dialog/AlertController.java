package com.tencent.testshengao.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

public class AlertController {

    private AlertDialog mAlertDialog;
    private Window mWindow;
    private DialogViewHelper mViewHelper;

    public AlertController(AlertDialog alertDialog, Window window) {
        this.mAlertDialog = alertDialog;
        this.mWindow = window;
    }

    /*获取Dialog*/
    public AlertDialog getmAlertDialog() {
        return mAlertDialog;
    }

    /*获取window*/
    public Window getmWindow() {
        return mWindow;
    }


    /*设置文本*/
    public void setText(int viewId, CharSequence text) {
        mViewHelper.setText(viewId, text);

    }

    /**
     * 减少findViewByid的次数
     *
     * @param viewId
     * @return
     */
    public <T extends View> T getView(int viewId) {
        return mViewHelper.getView(viewId);
    }

    /*设置点击事件*/
    public void setOnClickListener(int viewId, View.OnClickListener valueAt) {
        mViewHelper.setOnClickListener(viewId,valueAt);
    }

    public void  setViewHelper(DialogViewHelper mViewHelper){
            this.mViewHelper = mViewHelper;
    }



    public static class AlertParams {

        public Context mContext;
        public int mThemeResId;
        /*点击空白，是否能够取消 默认可以取消*/
        public boolean mCancelable = true;
        /*点击返回键是否能够取消，默认可以取消*/
        public boolean back=true;
        /*doalog cancle监听*/
        public DialogInterface.OnCancelListener mOnCancelListener;
        /*设置从顶部弹出dialog位置*/
        public int x=0;
        public int y=0;
        /*outlinedialog 消失监听*/
        DialogInterface.OnDismissListener mOnDismissListener;
        /*按键监听*/
        DialogInterface.OnKeyListener mOnKeyListener;
        /*自定义布局*/
        View mView;
        /*自定义布局ID*/
        int mViewLayoutResId;
        /*存放文本修改*/
        SparseArray<CharSequence> textArray = new SparseArray();
        /*存放点击事件*/
        SparseArray<View.OnClickListener> clickArray = new SparseArray();
        /*dialog的宽度*/
        public int mWidth = ViewGroup.LayoutParams.MATCH_PARENT;
        /*dialog的高度*/
        public int mHeight = ViewGroup.LayoutParams.WRAP_CONTENT;
        /*从哪个方向弹出*/
        public int mGravity = Gravity.CENTER;
        /*默认动画*/
        public int mAnimation = 0;
        /*设置背景的透明度*/
        public float mTransparence = 0.5F;
        /*设置不影响底层事件触发，默认不会*/
        public boolean mFouse = false;

        /*构造方法*/
        public AlertParams(Context context, int themeResId) {
            this.mContext = context;
            this.mThemeResId = themeResId;
        }

        /*去绑定和设置参数*/
        public void apply(AlertController mAlert) {
            /*设置dialog布局*/
            DialogViewHelper viewHelper = null;
            if (mViewLayoutResId != 0) {
                viewHelper = new DialogViewHelper(mContext, mViewLayoutResId);
            }
            if (mView != null) {
                viewHelper = new DialogViewHelper();
                viewHelper.setContentView(mView);
            }
            if (viewHelper == null) {
                throw new IllegalArgumentException("请设置dialog的布局");
            }
            /*设置dialog布局*/
            mAlert.getmAlertDialog().setContentView(viewHelper.getContentView());
            /*注入对象*/
            mAlert.setViewHelper(viewHelper);
            /*设置文本*/
            for (int i = 0; i < textArray.size(); i++) {
                mAlert.setText(textArray.keyAt(i), textArray.valueAt(i));
            }
            /*设置点击事件*/
            for (int i = 0; i < clickArray.size(); i++) {
                mAlert.setOnClickListener(clickArray.keyAt(i), clickArray.valueAt(i));
            }

            /*配置window*/
            Window window = mAlert.getmWindow();
            /*设置位置*/
            window.setGravity(mGravity);
            /*设置动画*/
            if (mAnimation != 0) {
                window.setWindowAnimations(mAnimation);
            }
            /*设置宽高*/
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = mWidth;
            params.height = mHeight;
            params.x = x;
            params.y = y;
            window.setAttributes(params);
            /*设置透明度*/
            window.setDimAmount(mTransparence);
            /*不影响Activity的事件触发*/
            if (mFouse){
                window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
            }

        }
    }
}

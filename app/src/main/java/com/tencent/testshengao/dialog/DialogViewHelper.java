package com.tencent.testshengao.dialog;


import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.lang.ref.WeakReference;

class DialogViewHelper {

    private View mContentView = null;
    /*减少findViewByID的次数*/
    private SparseArray<WeakReference<View>> mViews;

    /*构造方法*/
    public DialogViewHelper() {
        mViews = new SparseArray<>();
    }

    /*构造方法*/
    public DialogViewHelper(Context mContext, int mViewLayoutResId) {
        this.mContentView = LayoutInflater.from(mContext).inflate(mViewLayoutResId, null);
        mViews = new SparseArray<>();
    }

    /*设置布局 */
    public void setContentView(View mView) {
        this.mContentView = mView;
    }

    /*设置文本*/
    public void setText(int viewId, CharSequence text) {
        TextView tv = getView(viewId);
        tv.setText(text);

    }

    /**
     * 减少findViewByid的次数
     *
     * @param viewId
     * @return
     */
    public <T extends View> T getView(int viewId) {
        WeakReference<View> viewReference = mViews.get(viewId);
        View view = null;
        if (viewReference != null) {
            view = viewReference.get();
        }
        if (view == null) {
            view = mContentView.findViewById(viewId);
            if (view != null) {
                mViews.put(viewId, new WeakReference<>(view));
            }
        }
        return (T) view;
    }

    /*设置点击事件*/
    public void setOnClickListener(int viewId, View.OnClickListener valueAt) {
        View view = getView(viewId);
        if (view != null) {
            view.setOnClickListener(valueAt);
        }
    }

    /*返回当前视图*/
    public View getContentView() {
        return mContentView;
    }
}

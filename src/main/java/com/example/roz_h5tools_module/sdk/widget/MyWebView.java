package com.example.roz_h5tools_module.sdk.widget;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.webkit.WebView;

/**
 * @Author horseLai
 * CreatedAt 2018/12/6 10:24
 * Desc: 去除掉长按显示ActionMode菜单功能
 * Update:
 */
public class MyWebView extends WebView {
    public MyWebView(Context context) {
        super(context);
    }

    public MyWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MyWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    // 去除ActionMode
    @Override
    public ActionMode startActionMode(ActionMode.Callback callback) {
    //  return super.startActionMode(callback);
        return null;
    }

    // 去除ActionMode
    @Override
    public ActionMode startActionMode(ActionMode.Callback callback, int type) {
    //  return super.startActionMode(callback, type);
        return null;
    }
}

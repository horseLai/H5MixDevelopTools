package com.example.roz_h5tools_module.sdk.widget.helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.widget.FrameLayout;

import com.example.roz_h5tools_module.R;
import com.example.roz_h5tools_module.sdk.widget.MyWebView;
import com.roz.module.util.LogUtils;

/**
 * @Author horseLai
 * CreatedAt 2018/12/10 10:11
 * Desc: 用于持有MyWebView实例，减少每次都重新创建和销毁造成的开销
 * Update:
 */
public final class MyWebViewHolder {

    private MyWebView mWebView;
    private static MyWebViewHolder sMyWebViewHolder;
    private View pageNoneNet;
    private boolean mShouldClearHistory = false;


    public boolean shouldClearHistory() {
        return mShouldClearHistory;
    }

    public void shouldClearHistory(boolean shouldClearHistory) {
        this.mShouldClearHistory = shouldClearHistory;
    }

    private MyWebViewHolder() {
    }

    public static MyWebViewHolder getHolder() {
        if (sMyWebViewHolder != null) return sMyWebViewHolder;

        synchronized (MyWebViewHolder.class) {
            if (sMyWebViewHolder == null) {
                sMyWebViewHolder = new MyWebViewHolder();
            }
        }
        return sMyWebViewHolder;
    }

    /**
     * 务必在使用WebView前调用此方法进行初始化
     *
     * @param context
     */
    public void prepareWebView(Context context) {
        if (mWebView != null) return;
        synchronized (this) {
            if (mWebView == null) {
                mWebView = new MyWebView(context);
            }
        }
        LogUtils.d("prepare MyWebView OK...");
    }

    public MyWebView getMyWebView() {

        return mWebView;
    }

    public void detach() {
        if (mWebView != null) {
            LogUtils.d("detach MyWebView, but not destroy...");
            ((ViewGroup) mWebView.getParent()).removeView(mWebView);

            mWebView.clearDisappearingChildren();
            mWebView.removeAllViews();
            mWebView.clearAnimation();
            mWebView.clearFormData();
//            mWebView.clearHistory();
            mShouldClearHistory = true;
            mWebView.clearMatches();
            mWebView.getSettings().setJavaScriptEnabled(false);
        }
    }

    public void attach(ViewGroup parent, int index) {
        if (mWebView != null) {
            LogUtils.d("attach MyWebView, index of ViewGroup is " + index);

            WebSettings settings = mWebView.getSettings();
            // 不加此配置会无法加载显示界面
            settings.setDomStorageEnabled(true);
            settings.setSupportZoom(false);
            settings.setJavaScriptEnabled(true);
            settings.setUseWideViewPort(true);
            settings.setAllowFileAccess(true);
            settings.setAllowFileAccessFromFileURLs(true);

            mWebView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            mWebView.setVerticalScrollBarEnabled(false);
            mWebView.setHorizontalScrollBarEnabled(false);

            FrameLayout frameLayout = new FrameLayout(parent.getContext());
            frameLayout.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            frameLayout.addView(mWebView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            pageNoneNet = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_null_net, frameLayout, false);
            frameLayout.addView(pageNoneNet);
            pageNoneNet.setVisibility(View.GONE);
            pageNoneNet.findViewById(R.id.btn_try).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pageNoneNet.setVisibility(View.GONE);
                    mWebView.reload();
                }
            });
            parent.addView(frameLayout, index);
        }
    }

    public void showNoneNetPage() {
        if (pageNoneNet != null)
            pageNoneNet.setVisibility(View.VISIBLE);
    }

    public void hideNoneNetPage() {
        if (pageNoneNet != null)
            pageNoneNet.setVisibility(View.GONE);
    }


    public void attach(ViewGroup parent) {
        attach(parent, parent.getChildCount());
    }


    public void destroy() {
        if (mWebView != null) {
            LogUtils.d("destroy MyWebView...");
            mWebView.destroy();
            mWebView = null;
        }
    }

    public void pause() {
        if (mWebView != null) {
            LogUtils.d("pause MyWebView...");
            mWebView.onPause();
        }
    }

    public void resume() {
        if (mWebView != null) {
            LogUtils.d("resume MyWebView...");
            mWebView.onResume();
        }
    }

    public void removeJSInterfaces(String... names) {
        if (names == null || names.length == 0) return;
        for (String name : names) {
            LogUtils.d(String.format("removeJSInterfaces:: %s ..", name));
            mWebView.removeJavascriptInterface(name);
        }
    }
}

package com.example.roz_h5tools_module.sdk.util;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.webkit.ValueCallback;
import android.webkit.WebView;

/**
 * @Author horseLai
 * CreatedAt 2018/10/22 17:42
 * Desc: JS 代码执行器，包含通过WebView执行JS代码的通用方法。
 * Update:
 */
public final class JsExecutor {

    private static final String TAG = "JsExecutor";
    private JsExecutor() {
    }

    /**
     * JS方法不带参，且无返回值时用此方法
     *
     * @param webView
     * @param jsCode
     */
    public static void executeJsRaw(@NonNull WebView webView, @NonNull String jsCode) {
        executeJsRaw(webView, jsCode, null);
    }

    /**
     * JS方法带参，且有返回值时用此方法
     *
     * @param webView
     * @param jsCode
     * @param callback
     */
    public static void executeJsRaw(@NonNull WebView webView, @NonNull String jsCode, @Nullable ValueCallback<String> callback) {
        if (Build.VERSION.SDK_INT >= 19) {
            webView.evaluateJavascript(jsCode, callback);
        } else {
            webView.loadUrl(jsCode);
        }
    }


    /**
     * JS方法带参，且有返回值时用此方法
     *
     * @param webView
     * @param methodName
     * @param callback
     * @param params
     */
    public static void executeJs(@NonNull WebView webView, @NonNull CharSequence methodName, @Nullable ValueCallback<String> callback, @NonNull CharSequence... params) {

        StringBuilder sb = new StringBuilder();
        sb.append("javascript:")
                .append(methodName)
                .append("(");
        if (params != null && params.length > 0) {
            for (int i = 0; i < params.length; i++) {
                sb.append("\"")
                        .append(params[i])
                        .append("\"");
                if (i < params.length - 1)
                    sb.append(",");
            }
        }
        sb.append(");");
        Log.i(TAG, "executeJs: " + sb);
        executeJsRaw(webView, sb.toString(), callback);

    }

    /**
     * JS方法带参，且无返回值时用此方法
     *
     * @param webView
     * @param methodName
     * @param params
     */
    public static void executeJs(@NonNull WebView webView, @NonNull CharSequence methodName, @NonNull CharSequence... params) {
        executeJs(webView, methodName, null, params);
    }


}

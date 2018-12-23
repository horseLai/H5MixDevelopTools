package com.example.roz_h5tools_module.sdk;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.roz_h5tools_module.sdk.interfs.IH5JsUI;
import com.example.roz_h5tools_module.sdk.util.IntentActionUtil;
import com.example.roz_h5tools_module.sdk.util.PermissionChecker;
import com.example.roz_h5tools_module.sdk.util.PixelUtil;
import com.example.roz_h5tools_module.ui.H5MainActivity;
import com.kongzue.dialog.v2.WaitDialog;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @Author horseLai
 * CreatedAt 2018/11/30 10:33
 * Desc:
 * Update:
 */
public class H5JsUI implements IH5JsUI {
    private final WebView mWebView;
    private Activity mContext;
    private BottomSheetDialog mSheetDialog;
//    private Stack<String> mPageStack = new Stack<>();

    public H5JsUI(WebView webView, Activity context) {
        mContext = context;
        this.mWebView = webView;
    }

    @JavascriptInterface
    @Override
    public void alertDialog(String title, String msg) {
        new AlertDialog.Builder(mContext)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }


    @JavascriptInterface
    @Override
    public void showToast(String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
    }

    @JavascriptInterface
    @Override
    public void showWaitingDialog(String msg) {
        WaitDialog.show(mContext, msg);
    }

    @JavascriptInterface
    @Override
    public void hideWaitingDialog() {
        WaitDialog.dismiss();
    }

    @JavascriptInterface
    @Override
    public void showLoadFailedPage(final String statusCode) {
        if (mOnPageLoadCommandListener != null) {
            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mOnPageLoadCommandListener.onLoadStateError(statusCode);
                }
            });
        }
    }


    @JavascriptInterface
    @Override
    public void requestLogin() {
        try {
            mContext.startActivity(new Intent(mContext, Class.forName("com.example.admin.webappdemo.LoginActivity")));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    @Override
    public void requestQRScanner() {

    }

    @JavascriptInterface
    @Override
    public void requestPicSelection() {

        if (!confirmCameraPermissions()) return;
        showPicSheet();
    }

    private boolean confirmCameraPermissions() {
        if (!PermissionChecker.isPermissionGranted(mContext, Manifest.permission.CAMERA)
                || !PermissionChecker.isPermissionGranted(mContext, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            PermissionChecker.requestPermissions(mContext, new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE

            });
            return false;
        }
        return true;
    }


    @JavascriptInterface
    @Override
    public void requestFileSelection() {

    }

    @Override
    public void requestImagePreview(String url) {

    }


    private void showPicSheet() {
        if (mSheetDialog != null && !mSheetDialog.isShowing()) {
            mSheetDialog.show();
            return;
        }

        mSheetDialog = new BottomSheetDialog(mContext);
        // container
        LinearLayoutCompat container = new LinearLayoutCompat(mContext);
        LinearLayoutCompat.LayoutParams layoutParams = new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        container.setOrientation(LinearLayoutCompat.VERTICAL);
        container.setLayoutParams(layoutParams);
        container.setBackground(mContext.getWindow().getDecorView().getBackground());

        final int px = PixelUtil.dp2px(mContext, 12);
        // camera
        final TextView camera = new TextView(mContext);
        layoutParams = new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        camera.setLayoutParams(layoutParams);
        camera.setGravity(Gravity.CENTER);
        camera.setPadding(0, px, 0, px);
        camera.setText("拍照");
        camera.setBackground(createStateListDrawable());
        container.addView(camera);
        // gallery
        final TextView gallery = new TextView(mContext);
        layoutParams = new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        gallery.setLayoutParams(layoutParams);
        gallery.setGravity(Gravity.CENTER);
        gallery.setPadding(0, px, 0, px);
        gallery.setText("图库");
        gallery.setBackground(createStateListDrawable());
        container.addView(gallery);
        // cancel
        final TextView cancel = new TextView(mContext);
        layoutParams = new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.topMargin = px;
        cancel.setGravity(Gravity.CENTER);
        cancel.setLayoutParams(layoutParams);
        cancel.setPadding(0, px, 0, px);
        cancel.setText("取消");
        cancel.setBackground(createStateListDrawable());
        container.addView(cancel);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == cancel)
                    mSheetDialog.dismiss();
                else if (v == camera)
                    IntentActionUtil.takePhotoAndReturnUri(mContext, H5JsStorage.REQ_CAM);
                else if (v == gallery)
                    IntentActionUtil.picPicture(mContext, H5JsStorage.REQ_PIC);
                mSheetDialog.dismiss();
            }
        };
        camera.setOnClickListener(listener);
        cancel.setOnClickListener(listener);
        gallery.setOnClickListener(listener);


        mSheetDialog.setContentView(container);
        mSheetDialog.setCancelable(true);
        mSheetDialog.setCanceledOnTouchOutside(true);

        mSheetDialog.show();

    }

    @NonNull
    private StateListDrawable createStateListDrawable() {
        Drawable background = mContext.getWindow().getDecorView().getBackground();
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{-android.R.attr.state_pressed}, new ColorDrawable(Color.WHITE));
        stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, background);
        return stateListDrawable;
    }


    /**
     * 页面跳转
     *
     * @param jsonParams
     */
    @JavascriptInterface
    @Override
    public void gotoPage(final String jsonParams) {
//        Toast.makeText(mContext, jsonParams, Toast.LENGTH_SHORT).show();
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (TextUtils.isEmpty(jsonParams) || !jsonParams.startsWith("{")) {
                    if (mOnPageLoadCommandListener != null)
                        mOnPageLoadCommandListener.onLoadStateError("");
                    return;
                }
                try {
                    JSONObject jsonObject = new JSONObject(jsonParams);
                    String title = jsonObject.getString("title");
                    String url = jsonObject.getString("url");
                    if (TextUtils.isEmpty(url)) {
                        if (mOnPageLoadCommandListener != null)
                            mOnPageLoadCommandListener.onLoadStateError("");
                        return;
                    }
                    if (mOnPageLoadCommandListener != null)
                        mOnPageLoadCommandListener.onLoadPage(title, String.format("%s%s", "file:///android_asset/", url));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @JavascriptInterface
    @Override
    public void closeWindow() {
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((H5MainActivity) mContext).finish();
            }
        });
    }

    @JavascriptInterface
    @Override
    public void goBack() {
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mWebView.canGoBack())
                    mWebView.goBack();
            }
        });
    }

    @JavascriptInterface
    @Override
    public void goForward() {
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mWebView.canGoForward())
                    mWebView.goForward();
            }
        });
    }

    @JavascriptInterface
    @Override
    public void canGoForward() {
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mWebView.canGoForward();
            }
        });
    }

    @JavascriptInterface
    @Override
    public void canGoBack() {
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mWebView.canGoBack();
            }
        });
    }

    @JavascriptInterface
    @Override
    public void clearHistory() {
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mWebView.clearHistory();
            }
        });
    }


    //---------------------------------------------------打电话-------------------------------------------------------------
    @JavascriptInterface
    @Override
    public void call(final String phoneNumber) {
        new AlertDialog.Builder(mContext)
                .setTitle("温馨提示")
                .setMessage(String.format("是否拨打%s?", phoneNumber))
                .setPositiveButton("拨打", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        IntentActionUtil.call(mContext, phoneNumber);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }
    //----------------------------------------------------------------------------------------------------------------

    private OnPageLoadCommandListener mOnPageLoadCommandListener;

    public H5JsUI setOnPageLoadCommandListener(OnPageLoadCommandListener listener) {
        this.mOnPageLoadCommandListener = listener;
        return this;
    }

    public interface OnPageLoadCommandListener {
        void onLoadPage(String title, String url);

        void onLoadStateError(String statusCode);
    }

}

package com.example.roz_h5tools_module.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.RenderProcessGoneDetail;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.roz_h5tools_module.R;
import com.example.roz_h5tools_module.sdk.H5JsLocation;
import com.example.roz_h5tools_module.sdk.H5JsNet;
import com.example.roz_h5tools_module.sdk.H5JsStorage;
import com.example.roz_h5tools_module.sdk.H5JsUI;
import com.example.roz_h5tools_module.sdk.H5Manager;
import com.example.roz_h5tools_module.sdk.H5ManagerSettings;
import com.example.roz_h5tools_module.sdk.bean.User;
import com.example.roz_h5tools_module.sdk.receiver.NetStatusReceiver;
import com.example.roz_h5tools_module.sdk.util.JsExecutor;
import com.example.roz_h5tools_module.sdk.util.PermissionChecker;
import com.example.roz_h5tools_module.sdk.widget.MyWebView;
import com.example.roz_h5tools_module.sdk.widget.helper.MyWebViewHolder;
/**
 * @Author horseLai
 * CreatedAt 2018/10/29 16:25
 * Desc: 集成了通用H5交互功能
 * Update:
 */
public class H5MainActivity extends AppCompatActivity implements NetStatusReceiver.OnNetChangeListener,
        H5JsUI.OnPageLoadCommandListener, H5Manager.OnUpdateListener {

    private static final String TAG = "H5MainActivity";
    private MyWebView webView;
    private NetStatusReceiver mNetStatusReceiver;
    private H5JsStorage h5JsStorage;
    private H5Manager mH5Manager;
    private H5ManagerSettings mH5ManagerSettings;
    private User mUser;
    public static final String URL = "url";
    public static final String PACK_TYPE = "packType";
    private String mIndexUrl;
    private String mPackType;
    private boolean mJsExecutorReady = false;


    public static void launch(@NonNull Context context, @NonNull String packType, @NonNull String indexUrl) {
        Bundle bundle = new Bundle();
        bundle.putString(URL, indexUrl);
        bundle.putString(PACK_TYPE, packType);
        Intent intent = new Intent(context, H5MainActivity.class);
        intent.putExtras(bundle);
        context.startActivity( intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_h5_main);

        initView(savedInstanceState);
    }


    public void initView(Bundle savedInstanceState) {
        mH5ManagerSettings = new H5ManagerSettings(this);
        mH5Manager = H5Manager.get(mH5ManagerSettings).setOnUpdateListener(this);
        obtainExtras();
        registerNetStatusReceiver();
        setupWebView();

        PermissionChecker.requestPermissions(this, new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
        });
        webView.loadUrl(mIndexUrl);

        mH5Manager.checkVersion(mUser.getUserAccount(), mPackType);
    }


    private void obtainExtras() {
        Bundle extras = getIntent().getExtras();
        if (extras == null) return;
//        mCurrUserAccount = extras.getString(RequestParamsConstants.CURR_USR_ACCOUNT);
        mPackType = extras.getString(PACK_TYPE);
        // file://data/data/com.example.admin.webappdemo/cache/h5Pack/index.html
        mIndexUrl = extras.getString(URL);

        mUser = new User();
        mUser.setUserAccount("sdkjfkr")
                .setPassWord("12345")
                .setOrgId("122");
    }


    private void registerNetStatusReceiver() {
        mNetStatusReceiver = new NetStatusReceiver();
        mNetStatusReceiver.subscribe(this);
        registerReceiver(mNetStatusReceiver, new IntentFilter(NetStatusReceiver.ACTION));
    }

    private void setupWebView() {

        LinearLayout parent = findViewById(R.id.parent);
//        webView = findViewById(R.id.webView);
        webView = MyWebViewHolder.getHolder().getMyWebView();
//        webView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));

        MyWebViewHolder.getHolder().attach(parent);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                if (MyWebViewHolder.getHolder().shouldClearHistory()){
                    webView.clearHistory();
                    MyWebViewHolder.getHolder().shouldClearHistory(false);
                }
                super.onPageFinished(view, url);

                mJsExecutorReady = true;
//                showToast("onPageFinished");
                Log.d(TAG, "onPageFinished");
//                JsExecutor.executeJs(webView, "window.onUserAccountReceived", mUser.getUserAccount(), mUser.getPassword());
//                JsExecutor.executeJs(webView, "window.getLastUsedLocation", new ValueCallback<String>() {
//                    @Override
//                    public void onReceiveValue(String value) {
//                        JsExecutor.executeJs(webView, "window.showToast", value);
//                    }
//                });
            }

            @Override
            public boolean onRenderProcessGone(WebView view, RenderProcessGoneDetail detail) {
                Log.d(TAG,"onRenderProcessGone");
                return super.onRenderProcessGone(view, detail);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);

            }

            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
                Log.d(TAG,"onLoadResource");
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Log.d(TAG,"onPageStarted");
            }
        });

        H5JsNet h5JsNet = new H5JsNet(this);
        H5JsUI h5JsUI = new H5JsUI(webView, this);
        h5JsUI.setOnPageLoadCommandListener(this);
        h5JsStorage = new H5JsStorage(this, mUser);
        H5JsLocation h5JsLocation = new H5JsLocation(this, webView);

        webView.addJavascriptInterface(h5JsUI, "h5JsUI");
        webView.addJavascriptInterface(h5JsNet, "h5JsNet");
        webView.addJavascriptInterface(h5JsStorage, "h5JsStorage");
        webView.addJavascriptInterface(h5JsLocation, "h5JsLocation");

//        webView.loadUrl(mIndexUrl);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        h5JsStorage.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onNetStatusChanged(boolean connected, int netType) {
        if (mJsExecutorReady)
            JsExecutor.executeJs(webView, "onNetStatusChanged", "" + netType);
    }


    //------------------------------------------------------Lifecycle-----------------------------------------

    @Override
    protected void onPause() {
        super.onPause();
        MyWebViewHolder.getHolder().pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyWebViewHolder.getHolder().resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mNetStatusReceiver != null) {
            mNetStatusReceiver.unsubscribe(this);
            unregisterReceiver(mNetStatusReceiver);
        }
        mH5Manager.release();
        mH5Manager = null;

        MyWebViewHolder.getHolder().removeJSInterfaces("h5JsUI", "h5JsNet", "h5JsStorage", "h5JsLocation");
        MyWebViewHolder.getHolder().detach();

    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            MyWebViewHolder.getHolder().hideNoneNetPage();
            webView.goBack();
        }
        else {
            super.onBackPressed();
        }
    }

    //------------------------------------------------------Loading Page-----------------------------------------
    @Override
    public void onLoadPage(final String title, final String url) {

    }

    @Override
    public void onLoadStateError(String statusCode) {
        MyWebViewHolder.getHolder().showNoneNetPage();
    }

    //---------------------------------------------------------------------------------------------
    @Override
    public void onUpdating(final float progress, String newVersionCode, String packType) {
    }

    @Override
    public void onSuccess(String newVersionCode, String packType) {
        showToast("更新完成!");
        if (mH5ManagerSettings.containsModule(H5ManagerSettings.PackType.ADD_RECORD)) {
            webView.clearHistory();
            webView.loadUrl(mH5ManagerSettings.getModuleUrl(H5ManagerSettings.PackType.ADD_RECORD));
        }
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFailed(String newVersionCode, String packType) {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull final String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionChecker.onRequestPermissionsResult(requestCode, permissions, grantResults, new PermissionChecker.OnPermissionListener() {
            @Override
            public void onPermissionGranted(String permission) {
                Log.d(TAG,String.format("onPermissionGranted:: %s ", permission));
            }

            @Override
            public void onPermissionDenied(String permission) {
                super.onPermissionDenied(permission);
            }
        });
    }
}

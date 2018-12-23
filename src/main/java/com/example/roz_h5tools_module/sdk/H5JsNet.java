package com.example.roz_h5tools_module.sdk;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

/**
 * @Author horseLai
 * CreatedAt 2018/10/22 14:34
 * Desc: H5 通过 JS 控制 Android 网络状态
 * Update:
 */
public class H5JsNet {
    private static final int REQ_ENABLE_BL = 1111;
    private Context mContext;

    public H5JsNet(Context context) {
        this.mContext = context;
    }

    /**
     * 网络类型
     */
    public interface NetType {
        /**
         * 无网络
         */
        int NONE = 0;
        int WIFI = 1;
        /**
         * 运营商网络
         */
        int MOBILE = 2;
        /**
         * 蓝牙
         */
        int BLUETOOTH = 3;
        int UNKNOWN = 4;
    }

    /**
     * 获取网络状态
     *
     * @return
     */
    @JavascriptInterface
    public int getNetStatus() {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        int netType = NetType.NONE;
        if (connectivityManager == null)
            return netType;

        if (Build.VERSION.SDK_INT < 21) {
            NetworkInfo[] allNetworkInfo = connectivityManager.getAllNetworkInfo();
            for (NetworkInfo info : allNetworkInfo) {
                netType = checkNetType(info);
                if (netType != NetType.NONE) break;
            }
        } else {
            Network[] allNetworks = connectivityManager.getAllNetworks();
            for (Network n : allNetworks) {
                netType = checkNetType(connectivityManager.getNetworkInfo(n));
                if (netType != NetType.NONE) break;
            }
        }
//        Toast.makeText(mContext, "java::" + netType, Toast.LENGTH_SHORT).show();
        return netType;
    }

    private int checkNetType(NetworkInfo info) {
        int netType = NetType.NONE;
        if (info != null && info.isConnected()) {
            if (info.getType() == ConnectivityManager.TYPE_WIFI)
                netType = NetType.WIFI;
            else if (info.getType() == ConnectivityManager.TYPE_MOBILE)
                netType = NetType.MOBILE;
            else if (info.getType() == ConnectivityManager.TYPE_BLUETOOTH)
                netType = NetType.BLUETOOTH;
            else netType = NetType.UNKNOWN;

        }
        return netType;
    }

    /**
     *
     */
    @JavascriptInterface
    public void openBluetooth() {
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (defaultAdapter == null){
            Toast.makeText(mContext, "该设备不支持蓝牙功能!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!defaultAdapter.isEnabled()){
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            ((Activity) mContext).startActivityForResult(enableIntent, REQ_ENABLE_BL);
        }
    }

    /**
     *
     */
    @JavascriptInterface
    public void closeBluetooth() {
       /* BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (defaultAdapter == null){
            Toast.makeText(mContext, "该设备不支持蓝牙功能!", Toast.LENGTH_SHORT).show();
            return;
        }
        if ( defaultAdapter.isEnabled()){
            defaultAdapter.disable();
        }*/
    }

}

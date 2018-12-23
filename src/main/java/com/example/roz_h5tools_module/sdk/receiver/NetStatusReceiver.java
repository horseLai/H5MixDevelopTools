package com.example.roz_h5tools_module.sdk.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @Author horseLai
 * CreatedAt 2018/12/3 9:05
 * Desc: 用于被动监听网络状态改变，当系统网络状态发生改变时会通过此广播通知所有订阅者
 * Update:
 */
public class NetStatusReceiver extends BroadcastReceiver {
    private LinkedList<OnNetChangeListener> mNetChangeListeners;
    private List<OnNetChangeListener> mNetChangeCache;
    private volatile boolean mIsNotifying = false;
    private int mCurrentNetType = NetType.MOBILE;
    public static final String ACTION = ConnectivityManager.CONNECTIVITY_ACTION;

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


    public NetStatusReceiver() {
        mNetChangeListeners = new LinkedList<>();
        mNetChangeCache = new ArrayList<>();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) return;

        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            getNetType(context);
            mIsNotifying = true;
            for (OnNetChangeListener listener : mNetChangeListeners) {
                listener.onNetStatusChanged(mCurrentNetType != NetType.NONE, mCurrentNetType);
            }
            if (!mNetChangeCache.isEmpty()) {
                mNetChangeListeners.removeAll(mNetChangeCache);
                mNetChangeCache.clear();
            }
            mIsNotifying = false;
        }
    }

    private void getNetType(Context context) {
        ConnectivityManager manager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager == null) return;
        NetworkInfo activeNetworkInfo = manager.getActiveNetworkInfo();
        if (activeNetworkInfo == null || !activeNetworkInfo.isConnected()) {
            mCurrentNetType = NetType.NONE;
            return;
        }
        if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI)
            mCurrentNetType = NetType.WIFI;
        else if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE)
            mCurrentNetType = NetType.MOBILE;
        else if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_BLUETOOTH)
            mCurrentNetType = NetType.BLUETOOTH;
        else
            mCurrentNetType = NetType.UNKNOWN;
    }

    public NetStatusReceiver subscribe(OnNetChangeListener listener) {
        if (listener != null && !mNetChangeListeners.contains(listener))
            mNetChangeListeners.push(listener);
        return this;
    }


    public NetStatusReceiver unsubscribe(OnNetChangeListener listener) {
        if (listener == null) return this;
        if (!mIsNotifying)
            mNetChangeListeners.remove(listener);
        else mNetChangeCache.add(listener);
        return this;
    }

    public interface OnNetChangeListener {
        void onNetStatusChanged(boolean connected, int netType);
    }
}
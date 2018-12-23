package com.example.roz_h5tools_module.sdk.receiver;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
/**
 * @Author horseLai
 * CreatedAt 2018/7/13 8:45
 * Desc: 用于监听蓝牙状态
 * Update:
 */
public class BluetoothStatusChangeReceiver extends BroadcastReceiver {

    public static final String ACTION_STATE_CHANGED = BluetoothAdapter.ACTION_STATE_CHANGED;
    public static final String ACTION_CONNECTION_STATE_CHANGED = BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED;
    private int mCurrStatus = Status.OFF;
    private int mPreviousStatus = Status.OFF;
    private int mCurrConnStatus = Status.OFF;
    private int mPreviousConnStatus = Status.OFF;


    private volatile boolean mIsStatusNotifying = false;
    private volatile boolean mIsConnStatusNotifying = false;
    private Queue<OnConnStatusChangeListener> mOnConnStatusChangeListeners = new LinkedList<>();
    private Queue<OnStatusChangeListener> mOnStatusChangeListeners = new LinkedList<>();

    private List<OnConnStatusChangeListener> mConnStatusCache = new ArrayList<>();
    private List<OnStatusChangeListener> mStatusCache = new ArrayList<>();

    public interface Status {
        int ON = BluetoothAdapter.STATE_ON;
        int TURNING_ON = BluetoothAdapter.STATE_TURNING_ON;
        int OFF = BluetoothAdapter.STATE_OFF;
        int TURNING_OFF = BluetoothAdapter.STATE_TURNING_OFF;

        //-----------------------------------------------------

        int CONNECTED = BluetoothAdapter.STATE_CONNECTED;
        int CONNECTING = BluetoothAdapter.STATE_CONNECTING;
        int DISCONNECTED = BluetoothAdapter.STATE_DISCONNECTED;
        int DISCONNECTING = BluetoothAdapter.STATE_DISCONNECTING;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) return;
        Bundle extras = intent.getExtras();
        if (extras == null) return;
        if (ACTION_STATE_CHANGED.equals(intent.getAction())) {
            mCurrStatus = extras.getInt(BluetoothAdapter.EXTRA_STATE);
            mPreviousStatus = extras.getInt(BluetoothAdapter.EXTRA_PREVIOUS_STATE);
            notifyStatusToAll();
        } else if (ACTION_CONNECTION_STATE_CHANGED.equals(intent.getAction())) {
            mCurrConnStatus = extras.getInt(BluetoothAdapter.EXTRA_CONNECTION_STATE);
            mPreviousConnStatus = extras.getInt(BluetoothAdapter.EXTRA_PREVIOUS_CONNECTION_STATE);
            notifyConnStatusToAll();
        }
    }

    private void notifyStatusToAll() {
        mIsStatusNotifying = true;
        for (OnStatusChangeListener l : mOnStatusChangeListeners) {
            l.onStatusChanged(mPreviousStatus, mCurrStatus);
        }
        if (!mStatusCache.isEmpty()) {
            mOnStatusChangeListeners.removeAll(mStatusCache);
            mStatusCache.clear();
        }
        mIsStatusNotifying = false;
    }

    private void notifyConnStatusToAll() {
        mIsConnStatusNotifying = true;
        for (OnConnStatusChangeListener l : mOnConnStatusChangeListeners) {
            l.onConnStatusChanged(mPreviousStatus, mCurrStatus);
        }
        if (!mConnStatusCache.isEmpty()) {
            mOnConnStatusChangeListeners.removeAll(mConnStatusCache);
            mConnStatusCache.clear();
        }
        mIsConnStatusNotifying = false;
    }

    public interface OnStatusChangeListener {
        void onStatusChanged(int preStatus, int currStatus);
    }

    public interface OnConnStatusChangeListener {
        void onConnStatusChanged(int preStatus, int currStatus);
    }

    public void subscribStatusChange(OnStatusChangeListener listener) {
        if (listener == null)
            return;
        if (!mOnStatusChangeListeners.contains(listener))
            mOnStatusChangeListeners.add(listener);
    }

    public void unsubscribStatusChange(OnStatusChangeListener listener) {
        if (listener == null) return;
        if (!mIsStatusNotifying)
            this.mOnStatusChangeListeners.remove(listener);
        else mStatusCache.add(listener);
    }

    public void subscribConnStatusChange(OnConnStatusChangeListener listener) {
        if (listener == null)
            return;
        if (!mOnConnStatusChangeListeners.contains(listener))
            mOnConnStatusChangeListeners.add(listener);
    }

    public void unsubscribConnStatusChange(OnConnStatusChangeListener listener) {
        if (listener == null) return;
        if (!mIsConnStatusNotifying)
            this.mOnConnStatusChangeListeners.remove(listener);
        else mConnStatusCache.add(listener);
    }

    public int getCurrentStatus() {
        return mCurrStatus;
    }

    public int getPreviousStatus() {
        return mPreviousStatus;
    }

    public int getCurrentConnStatus() {
        return mCurrConnStatus;
    }

    public int getPreviousConnStatus() {
        return mPreviousConnStatus;
    }
}

package com.example.roz_h5tools_module.sdk;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.example.roz_h5tools_module.sdk.util.JsExecutor;
import com.example.roz_h5tools_module.sdk.util.LocationHelper;
import com.example.roz_h5tools_module.sdk.util.PermissionChecker;

/**
 * @Author horseLai
 * CreatedAt 2018/12/4 15:16
 * Desc: 用于与JS层互动位置信息
 * Update:
 */
public class H5JsLocation {

    private final LocationHelper mLocationHelper;
    private WebView mWebView;
    private Activity mContext;
    private final String[] sPermissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};


    public H5JsLocation(Activity context, WebView webView) {
        this.mWebView = webView;
        mContext = context;
        mLocationHelper = new LocationHelper(context);
    }

    private boolean isPermissionInvalidate() {
        return ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED;
    }

    /**
     * JS调用此方法获取最近使用的位置信息，最终会通过JS中的`onLastUsedLocationOk`传递数据至JS端
     *
     * @return
     */
    @JavascriptInterface
    public void getLastUsedLocation() {
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isPermissionInvalidate()) {
                    PermissionChecker.requestPermissions(mContext, sPermissions);
                    return;
                }
                Location lastUsedLocation = mLocationHelper.getLastUsedLocation();
                if (lastUsedLocation != null)
                    JsExecutor.executeJs(mWebView, "onGetLocationOk", String.valueOf(lastUsedLocation.getLongitude()), String.valueOf(lastUsedLocation.getLatitude()));
                else
                    JsExecutor.executeJs(mWebView, "onGetLocationFailed");
            }
        });
    }

    /**
     * JS调用此方法获取当前位置信息，最终会通过JS中的`onLastUsedLocationOk`传递数据至JS端
     *
     * @return
     */
    @JavascriptInterface
    public void getCurrentNetWorkLocation() {
        if (isPermissionInvalidate()) {
            PermissionChecker.requestPermissions(mContext, sPermissions);
            return;
        }
        mLocationHelper.getCurrentLocation(LocationManager.NETWORK_PROVIDER, new LocationHelper.SimpleLocationListener() {
            @Override
            public void onLocationChanged(final Location location) {
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (location != null)
                            JsExecutor.executeJs(mWebView, "onGetLocationOk", String.valueOf(location.getLongitude()), String.valueOf(location.getLatitude()));
                        else
                            JsExecutor.executeJs(mWebView, "onGetLocationFailed");
                    }
                });
            }

            @Override
            public void onProviderDisabled(String provider) {
                super.onProviderDisabled(provider);
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JsExecutor.executeJs(mWebView, "onGetLocationFailed");
                    }
                });
            }

            @Override
            public void onStatusChanged(final String provider, final int status, Bundle extras) {
                super.onStatusChanged(provider, status, extras);
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (LocationManager.NETWORK_PROVIDER.equals(provider) &&
                                (status == LocationProvider.TEMPORARILY_UNAVAILABLE ||
                                        status == LocationProvider.OUT_OF_SERVICE))
                            JsExecutor.executeJs(mWebView, "onGetLocationFailed");
                    }
                });
            }
        });
    }

    /**
     * JS调用此方法获取当前位置信息，最终会通过JS中的`onLastUsedLocationOk`传递数据至JS端
     *
     * @return
     */
    @JavascriptInterface
    public void getCurrentGPSLocation() {
        if (isPermissionInvalidate()) {
            PermissionChecker.requestPermissions(mContext, sPermissions);
            return;
        }
        mLocationHelper.getCurrentLocation(LocationManager.GPS_PROVIDER, new LocationHelper.SimpleLocationListener() {
            @Override
            public void onLocationChanged(final Location location) {
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (location != null)
                            JsExecutor.executeJs(mWebView, "onGetLocationOk", String.valueOf(location.getLongitude()), String.valueOf(location.getLatitude()));
                    }
                });
            }

            @Override
            public void onProviderDisabled(String provider) {
                super.onProviderDisabled(provider);
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JsExecutor.executeJs(mWebView, "onGetLocationFailed");
                    }
                });
            }

            @Override
            public void onStatusChanged(final String provider, final int status, Bundle extras) {
                super.onStatusChanged(provider, status, extras);
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (LocationManager.NETWORK_PROVIDER.equals(provider) &&
                                (status == LocationProvider.TEMPORARILY_UNAVAILABLE ||
                                        status == LocationProvider.OUT_OF_SERVICE))
                            JsExecutor.executeJs(mWebView, "onGetLocationFailed");
                    }
                });
            }
        });
    }

    /**
     * JS调用此方法持续获取当前位置信息，最终会通过JS中的`onLastUsedLocationOk`传递数据至JS端
     *
     * @return
     */
    @JavascriptInterface
    public void getGPSLocationFrequently() {
        if (isPermissionInvalidate()) {
            PermissionChecker.requestPermissions(mContext, sPermissions);
            return;
        }
        mLocationHelper.requestUpdateFrequently(LocationManager.GPS_PROVIDER, new LocationHelper.SimpleLocationListener() {
            @Override
            public void onLocationChanged(final Location location) {
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (location != null)
                            JsExecutor.executeJs(mWebView, "onGetLocationOk", String.valueOf(location.getLongitude()), String.valueOf(location.getLatitude()));
                    }
                });
            }

            @Override
            public void onProviderDisabled(String provider) {
                super.onProviderDisabled(provider);
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JsExecutor.executeJs(mWebView, "onGetLocationFailed");
                    }
                });
            }

            @Override
            public void onStatusChanged(final String provider, final int status, Bundle extras) {
                super.onStatusChanged(provider, status, extras);
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (LocationManager.NETWORK_PROVIDER.equals(provider)) {
                            if (LocationProvider.TEMPORARILY_UNAVAILABLE == status)
                                JsExecutor.executeJs(mWebView, "onStatusChanged", "TEMPORARILY_UNAVAILABLE");
                            else if (LocationProvider.OUT_OF_SERVICE == status)
                                JsExecutor.executeJs(mWebView, "onStatusChanged", "OUT_OF_SERVICE");
                            else if (LocationProvider.AVAILABLE == status)
                                JsExecutor.executeJs(mWebView, "onStatusChanged", "AVAILABLE");
                        }
                    }
                });
            }
        });
    }

}

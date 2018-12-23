package com.example.roz_h5tools_module.sdk.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import com.example.roz_h5tools_module.sdk.interfs.LocationInterface;

import static android.content.Context.LOCATION_SERVICE;

/**
 * @Author horseLai
 * CreatedAt 2018/12/4 11:28
 * Desc: 获取用户位置信息
 * Update:
 */
public class LocationHelper implements LocationInterface {

    private final LocationManager mLocationManager;
    private final Context mContext;

    public LocationHelper(@NonNull Context context) {
        this.mContext = context;
        mLocationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
    }

    /**
     * 获取近期使用的已有位置信息
     *
     * @return
     */
    @Override
    public Location getLastUsedLocation( ) {
        if (mLocationManager == null) return null;

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            throw new IllegalStateException("You have no permission for getting location!!! Please checkout ACCESS_FINE_LOCATION and ACCESS_COARSE_LOCATION..");
        }
        Location gpsLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Location netLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        //  Log.i(TAG, "onGetLocationInfo: gpsLocation::" + gpsLocation );
        // Log.i(TAG, "onGetLocationInfo: netLocation::" + netLocation );
        if (netLocation != null) return netLocation;
        if (gpsLocation != null) return gpsLocation;

        return null;
    }

    /**
     * 快速定位，只用一次
     *
     * @param provider LocationManager.GPS_PROVIDER || LocationManager.NETWORK_PROVIDER
     * @param listener
     */
    @Override
    public void getCurrentLocation(  @NonNull String provider, @NonNull final LocationListener listener) {
        if (mLocationManager == null) return;

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            throw new IllegalStateException("You have no permission for getting location!!! Please checkout ACCESS_FINE_LOCATION and ACCESS_COARSE_LOCATION..");
        }
        mLocationManager.requestLocationUpdates(provider, 1000, 1, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                listener.onLocationChanged(location);
                mLocationManager.removeUpdates(this);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                listener.onStatusChanged(provider, status, extras);
            }

            @Override
            public void onProviderEnabled(String provider) {
                listener.onProviderEnabled(provider);
            }

            @Override
            public void onProviderDisabled(String provider) {
                listener.onProviderDisabled(provider);

            }
        });
    }

    /**
     *  频繁更新位置时用这个
     *
     * @param provider
     * @param listener
     */
    @Override
    public void requestUpdateFrequently( @NonNull String provider, @NonNull final LocationListener listener) {
        if (mLocationManager == null) return;

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            throw new IllegalStateException("You have no permission for getting location!!! Please checkout ACCESS_FINE_LOCATION and ACCESS_COARSE_LOCATION..");
        }
        mLocationManager.requestLocationUpdates(provider, 5000, 10,  listener);
    }

    /**
     *  移除位置更新监听器
     *
     * @param listener
     */
    public void removeUpdateListener(@NonNull LocationListener listener){
        if (mLocationManager == null) return;
        mLocationManager.removeUpdates(listener);
    }


    public abstract static class SimpleLocationListener implements  LocationListener{
        @Override
        public abstract void onLocationChanged(Location location);

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }


}

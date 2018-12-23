package com.example.roz_h5tools_module.sdk.interfs;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.support.annotation.NonNull;
/**
 * @Author horseLai
 * CreatedAt 2018/12/4 13:53
 * Desc:
 * Update:
 */
public interface LocationInterface {

    /**
     * 获取近期使用的已有位置信息
     *
     * @return 返回最近使用的位置信息
     */
    Location getLastUsedLocation();

    /**
     * 快速定位，一次性
     * @param provider LocationManager.GPS_PROVIDER || LocationManager.NETWORK_PROVIDER
     * @param listener
     */
    void getCurrentLocation(@NonNull String provider, @NonNull final LocationListener listener);

    /**
     * 以一定频率更新位置信息
     * @param provider
     */
    void requestUpdateFrequently(@NonNull String provider, @NonNull final LocationListener listener);

}

package com.example.roz_h5tools_module.sdk.net;

import com.example.roz_h5tools_module.sdk.bean.CommResponseEntry;
import com.example.roz_h5tools_module.sdk.bean.VersionEntry;

import java.util.Map;

import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * @Author horseLai
 * CreatedAt 2018/7/13 8:45
 * Desc:
 * Update:
 */
public class AppVersionLoader extends ObjectLoaderWrap {

    private final Service mService;

    public AppVersionLoader() {
        mService = RetrofitServiceHelper.getInstance().create(Service.class);
    }

    public Observable<CommResponseEntry<VersionEntry>> getDoctorH5Version(Map<String,String> params) {
        return observeOnIo(mService.getH5Version(params));
    }


    interface Service {

        @POST("doctor/getH5Version")
        @FormUrlEncoded
        Observable<CommResponseEntry<VersionEntry>> getH5Version(@FieldMap Map<String, String> params);
    }
}

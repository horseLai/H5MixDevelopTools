package com.example.roz_h5tools_module.sdk.net;


import com.zhy.http.okhttp.OkHttpUtils;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @Author horseLai
 * CreatedAt 2018/7/13 8:45
 * Desc:
 * Update:
 */
public class RetrofitServiceHelper {
    private Retrofit mRetrofit;
    private RetrofitServiceHelper() {
        // 创建Retrofit
        mRetrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(OkHttpUtils.getInstance().getOkHttpClient())
                .baseUrl(UrlsManager.MAIN_ADDRESS)
                .build();
    }

    private static class SingletonHolder {
        private static final RetrofitServiceHelper INSTANCE = new RetrofitServiceHelper();
    }

    /**
     * 获取RetrofitServiceManager
     * @return
     */
    public static RetrofitServiceHelper getInstance() {
        return SingletonHolder.INSTANCE;
    }


    /**
     * 获取对应的Service
     *
     * @param service Service 的 class
     * @param <T>
     * @return
     */
    public <T> T create(Class<T> service) {
        return mRetrofit.create(service);
    }
}

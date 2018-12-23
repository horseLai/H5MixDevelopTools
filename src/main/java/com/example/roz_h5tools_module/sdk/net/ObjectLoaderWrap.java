package com.example.roz_h5tools_module.sdk.net;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @Author horseLai
 * CreatedAt 2018/7/13 8:45
 * Desc:
 * Update:
 */
public class ObjectLoaderWrap {

    /**
     * @param observable
     * @param <T>
     * @return
     */
    public  <T> Observable<T> observeOnIo(Observable<T> observable) {
        return observable
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

}

package com.xiecc.seeWeather.common.utils;

import com.xiecc.seeWeather.common.PLog;
import rx.Observable;
import rx.Scheduler;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.plugins.RxJavaErrorHandler;
import rx.plugins.RxJavaPlugins;
import rx.schedulers.Schedulers;

/**
 * Created by HugoXie on 16/5/19.
 *
 * Email: Hugo3641@gamil.com
 * GitHub: https://github.com/xcc3641
 * Info: 封装 Rx 的一些方法
 */
public class RxUtils {

    public static <T> Observable.Transformer<T, T> rxSchedulerHelper() {
        return tObservable -> tObservable.subscribeOn(Schedulers.io())
            .unsubscribeOn(AndroidSchedulers.mainThread())
            .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 可自定义线程
     */
    public static <T> Observable.Transformer<T, T> rxSchedulerHelper(Scheduler scheduler) {
        return tObservable -> tObservable.subscribeOn(scheduler)
            .unsubscribeOn(AndroidSchedulers.mainThread())
            .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 自定义处理 Rx 错误线程
     */
    public static void unifiedErrorHandler() {
        RxJavaPlugins.getInstance().registerErrorHandler(new RxJavaErrorHandler() {
            @Override
            public void handleError(Throwable e) {
                PLog.e(e.toString());
            }
        });
    }

    public static void unsubscribe(Subscription subscription) {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }
}

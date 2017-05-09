package com.xiecc.seeWeather.common.utils;

import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.FragmentEvent;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.trello.rxlifecycle2.components.support.RxFragment;
import io.reactivex.FlowableTransformer;
import io.reactivex.ObservableTransformer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by HugoXie on 16/5/19.
 *
 * Email: Hugo3641@gamil.com
 * GitHub: https://github.com/xcc3641
 * Info: 封装 Rx 的一些方法
 */
public class RxUtil {

    private static <T> ObservableTransformer<T, T> schedulerTransformer(Scheduler scheduler) {
        return observable ->
            observable
                .subscribeOn(scheduler)
                .observeOn(AndroidSchedulers.mainThread(), true);
    }

    public static <T> ObservableTransformer<T, T> io() {
        return schedulerTransformer(Schedulers.io());
    }

    private static <T> FlowableTransformer<T, T> schedulerTransformerF(Scheduler scheduler) {
        return flowable ->
            flowable
                .subscribeOn(scheduler)
                .observeOn(AndroidSchedulers.mainThread(), true);
    }

    public static <T> FlowableTransformer<T, T> ioF() {
        return schedulerTransformerF(Schedulers.io());
    }

    public static <T> ObservableTransformer<T, T> activityLifecycle(RxAppCompatActivity activity) {
        return observable ->
            observable.compose(activity.bindUntilEvent(ActivityEvent.DESTROY));
    }

    public static <T> ObservableTransformer<T, T> fragmentLifecycle(RxFragment fragment) {
        return observable ->
            observable.compose(fragment.bindUntilEvent(FragmentEvent.DESTROY));
    }

    public static <T> FlowableTransformer<T, T> activityLifecycleF(RxAppCompatActivity activity) {
        return flowable ->
            flowable.compose(activity.bindUntilEvent(ActivityEvent.DESTROY));
    }

    public static <T> FlowableTransformer<T, T> fragmentLifecycleF(RxFragment fragment) {
        return flowable ->
            flowable.compose(fragment.bindUntilEvent(FragmentEvent.DESTROY));
    }

}

package com.xiecc.seeWeather.common.utils;

import com.xiecc.seeWeather.common.PLog;
import rx.Subscriber;

/**
 * Created by HugoXie on 16/7/25.
 *
 * Email: Hugo3641@gmail.com
 * GitHub: https://github.com/xcc3641
 * Info: 简化 Rx 的模式,用在只关乎 onNext() 逻辑
 */
public abstract class SimpleSubscriber<T> extends Subscriber<T> {
    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {
        PLog.e(e.toString());
    }
}

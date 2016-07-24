package com.xiecc.seeWeather.component;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by HugoXie on 16/5/14.
 *
 * Email: Hugo3641@gamil.com
 * GitHub: https://github.com/xcc3641
 */
public class RxManager {
    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    public void add(Subscription s) {
        compositeSubscription.add(s);
    }

    public void clear() {
        compositeSubscription.unsubscribe();
    }
}

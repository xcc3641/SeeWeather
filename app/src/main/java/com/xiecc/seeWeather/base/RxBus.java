package com.xiecc.seeWeather.base;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * RxBus
 * Created by YoKeyword on 2015/6/17.
 */
public class RxBus {

    private static volatile RxBus instance;
    // 主题
    private final Subject<Object, Object> bus;

    // PublishSubject只会把在订阅发生的时间点之后来自原始Observable的数据发射给观察者
    public RxBus() {
        bus = new SerializedSubject<>(PublishSubject.create());
    }

    // 单例RxBus
    public static RxBus getDefault() {
        if (instance == null) {
            synchronized (RxBus.class) {
                if (instance == null) {
                    instance = new RxBus();
                }
            }
        }
        return instance;
    }

    // 提供了一个新的事件
    public void post(Object o) {
        bus.onNext(o);
    }

    // 根据传递的 eventType 类型返回特定类型(eventType)的 被观察者
    public <T> Observable<T> toObserverable(Class<T> eventType) {
        return bus.ofType(eventType);
        //        这里感谢小鄧子的提醒: ofType = filter + cast
        //        return bus.filter(new Func1<Object, Boolean>() {
        //            @Override
        //            public Boolean call(Object o) {
        //                return eventType.isInstance(o);
        //            }
        //        }) .cast(eventType);
    }
}

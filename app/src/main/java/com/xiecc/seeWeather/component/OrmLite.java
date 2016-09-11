package com.xiecc.seeWeather.component;

import com.litesuits.orm.LiteOrm;
import com.xiecc.seeWeather.BuildConfig;
import com.xiecc.seeWeather.base.BaseApplication;
import com.xiecc.seeWeather.base.C;
import com.xiecc.seeWeather.common.PLog;
import com.xiecc.seeWeather.common.utils.RxUtils;
import com.xiecc.seeWeather.common.utils.SimpleSubscriber;
import com.xiecc.seeWeather.modules.main.domain.CityORM;
import rx.Observable;

/**
 * Created by HugoXie on 16/7/24.
 *
 * Email: Hugo3641@gamil.com
 * GitHub: https://github.com/xcc3641
 * Info:
 */
public class OrmLite {

    static LiteOrm liteOrm;

    public static LiteOrm getInstance() {
        getOrmHolder();
        return liteOrm;
    }

    private static OrmLite getOrmHolder() {
        return OrmHolder.sInstance;
    }

    private OrmLite() {
        if (liteOrm == null) {
            liteOrm = LiteOrm.newSingleInstance(BaseApplication.getmAppContext(), C.ORM_NAME);

        }
        liteOrm.setDebugged(BuildConfig.DEBUG);
    }

    private static class OrmHolder {
        private static final OrmLite sInstance = new OrmLite();
    }

    public static <T> void OrmTest(Class<T> t) {
        Observable.from(getInstance().query(t))
            .compose(RxUtils.rxSchedulerHelper())
            .subscribe(new SimpleSubscriber<T>() {
                @Override
                public void onNext(T t) {
                    if (t instanceof CityORM) {
                        PLog.w(((CityORM) t).getName());
                    }
                }
            });
    }
}

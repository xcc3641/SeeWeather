package com.xiecc.seeWeather.common;

import com.litesuits.orm.LiteOrm;
import com.xiecc.seeWeather.BuildConfig;
import com.xiecc.seeWeather.base.BaseApplication;

/**
 * Created by HugoXie on 16/7/24.
 *
 * Email: Hugo3641@gamil.com
 * GitHub: https://github.com/xcc3641
 * Info:
 */
public class OrmLite {
    private static OrmLite ourInstance = new OrmLite();
    static LiteOrm liteOrm;

    public static LiteOrm getInstance() {
        return liteOrm;
    }

    private OrmLite() {
        if (liteOrm == null) {
            liteOrm = LiteOrm.newSingleInstance(BaseApplication.getmAppContext(), "cities.db");
        }
        liteOrm.setDebugged(BuildConfig.DEBUG);
    }
}

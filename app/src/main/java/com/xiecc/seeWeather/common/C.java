package com.xiecc.seeWeather.common;

import com.xiecc.seeWeather.BuildConfig;
import com.xiecc.seeWeather.base.BaseApplication;
import java.io.File;

/**
 * Created by HugoXie on 16/5/23.
 *
 * Email: Hugo3641@gamil.com
 * GitHub: https://github.com/xcc3641
 * Info: 常量类
 */
public class C {

    public static final String API_TOKEN = BuildConfig.FirToken;
    public static final String KEY = BuildConfig.WeatherKey;// 和风天气 key

    public static final String MULTI_CHECK = "multi_check";

    public static final String ORM_NAME = "cities.db";

    public static final String UNKNOWN_CITY = "unknown city";

    public static final String NET_CACHE = BaseApplication.getAppCacheDir() + File.separator + "NetCache";
}

package com.xiecc.seeWeather.common;

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

    public static final String WEATHER_CACHE = "weatherData";
    public static final String API_TOKEN = "7db041d0c3013b63e4bed2a554f02d85";//fir.im api_token
    public static final String KEY = "282f3846df6b41178e4a2218ae083ea7";// 和风天气 key

    public static final String MULTI_CHECK = "multi_check";

    public static final String ORM_NAME = "cities.db";

    public static final String UNKNOWN_CITY = "unknown city";

    public static final String NET_CACHE = BaseApplication.getAppCacheDir() + File.separator + "NetCache";

}

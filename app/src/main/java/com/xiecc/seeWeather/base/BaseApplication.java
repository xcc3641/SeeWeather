package com.xiecc.seeWeather.base;

import android.app.Application;
import android.content.Context;
import android.support.v7.app.AppCompatDelegate;
import com.tencent.bugly.crashreport.CrashReport;
import com.xiecc.seeWeather.BuildConfig;
import com.xiecc.seeWeather.common.CrashHandler;

/**
 * Created by xcc on 2015/12/16.
 */
public class BaseApplication extends Application {

    public static String cacheDir;
    public static Context mAppContext = null;

    // TODO: 16/8/1 这里的夜间模式 UI 有些没有适配好
    static {
        AppCompatDelegate.setDefaultNightMode(
            AppCompatDelegate.MODE_NIGHT_NO);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mAppContext = getApplicationContext();
        CrashHandler.init(new CrashHandler(getApplicationContext()));
        if (!BuildConfig.DEBUG) {
            CrashReport.initCrashReport(getApplicationContext(), "900028220", false);
        }
        //BlockCanary.install(this, new AppBlockCanaryContext()).start();
        //LeakCanary.install(this);

        //RxUtils.unifiedErrorHandler();
        //Thread.setDefaultUncaughtExceptionHandler(new MyUnCaughtExceptionHandler());
        /**
         * 如果存在SD卡则将缓存写入SD卡,否则写入手机内存
         */
        if (getApplicationContext().getExternalCacheDir() != null && ExistSDCard()) {
            cacheDir = getApplicationContext().getExternalCacheDir().toString();
        } else {
            cacheDir = getApplicationContext().getCacheDir().toString();
        }
    }

    private boolean ExistSDCard() {
        return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
    }

    public static Context getmAppContext() {
        return mAppContext;
    }

    public static String getCachedir(){
        return cacheDir;
    }
}

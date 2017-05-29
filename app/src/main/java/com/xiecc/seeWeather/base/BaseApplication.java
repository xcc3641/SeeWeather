package com.xiecc.seeWeather.base;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatDelegate;

import com.github.moduth.blockcanary.BlockCanary;
import com.hugo.watcher.Watcher;
import com.squareup.leakcanary.LeakCanary;
import com.xiecc.seeWeather.BuildConfig;
import com.xiecc.seeWeather.component.CrashHandler;

import im.fir.sdk.FIR;

public class BaseApplication extends Application {

    private static String sCacheDir;
    private static Context sAppContext;

    // TODO: 16/8/1 这里的夜间模式 UI 有些没有适配好 暂时放弃夜间模式
    static {
        AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_NO);
    }

    /**
     * @return Application级别的Context对象
     */
    @NonNull
    public static Context getAppContext() {
        return sAppContext;
    }

    /**
     * @return 缓存文件夹路径
     */
    @NonNull
    public static String getAppCacheDir() {
        return sCacheDir;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sAppContext = getApplicationContext();
        CrashHandler.init(new CrashHandler(getApplicationContext()));
        if (!BuildConfig.DEBUG) {
            FIR.init(this);
        } else {
            Watcher.getInstance().start(this);//初始化帧数,内存占用,当前Activity监听服务 https://github.com/xcc3641/Watcher
        }
        BlockCanary.install(this, new AppBlockCanaryContext()).start();//监听ANR与内存泄露
        LeakCanary.install(this);
        /*
         * 如果存在SD卡则将缓存写入SD卡,否则写入手机内存
         */
        if (getApplicationContext().getExternalCacheDir() != null && isSDCardExists()) {
            sCacheDir = getApplicationContext().getExternalCacheDir().toString();
        } else {
            sCacheDir = getApplicationContext().getCacheDir().toString();
        }
    }

    private boolean isSDCardExists() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }
}

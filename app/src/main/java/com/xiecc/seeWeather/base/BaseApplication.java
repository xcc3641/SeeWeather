package com.xiecc.seeWeather.base;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import com.xiecc.seeWeather.common.CrashHandler;
import com.xiecc.seeWeather.component.RetrofitSingleton;
import com.xiecc.seeWeather.modules.ui.MainActivity;

/**
 * Created by xcc on 2015/12/16.
 */
public class BaseApplication extends Application {

    public static String cacheDir = "";
    public static Context mAppContext = null;


    @Override public void onCreate() {
        super.onCreate();
        mAppContext = getApplicationContext();
        // 初始化 retrofit
        RetrofitSingleton.init(getApplicationContext());
        CrashHandler.init(new CrashHandler(getApplicationContext()));
        //Thread.setDefaultUncaughtExceptionHandler(new MyUnCaughtExceptionHandler());

        /**
         * 如果存在SD卡则将缓存写入SD卡,否则写入手机内存
         */

        if (getApplicationContext().getExternalCacheDir() != null && ExistSDCard()) {
            cacheDir = getApplicationContext().getExternalCacheDir().toString();

        }
        else {
            cacheDir = getApplicationContext().getCacheDir().toString();
        }
    }

    class MyUnCaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
            new AlertDialog.Builder(getApplicationContext()).setTitle("")
                .setMessage("程序出现异常了~").setPositiveButton("重启", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(BaseApplication.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    BaseApplication.this.startActivity(intent);
                    android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(1);
                }
            }).setNegativeButton("退出", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(1);
                }
            }).show();
        }
    }
    private boolean ExistSDCard() {
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            return true;
        }
        else {
            return false;
        }
    }
}

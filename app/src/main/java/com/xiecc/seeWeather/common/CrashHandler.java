package com.xiecc.seeWeather.common;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Created by zk on 2015/12/24.
 * 捕获程序崩溃信息
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private static Thread.UncaughtExceptionHandler defaultHandler = null;

    private Context context = null;

    private final String TAG = CrashHandler.class.getSimpleName();

    public CrashHandler(Context context) {
        this.context = context;
    }

    /**
     * 初始化,设置该CrashHandler为程序的默认处理器
     */
    public static void init(CrashHandler crashHandler) {
        defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(crashHandler);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        System.out.println(ex.toString());
        PLog.e(TAG, ex.toString());
        PLog.e(TAG, collectCrashDeviceInfo());
        PLog.e(TAG, getCrashInfo(ex));
        // 调用系统错误机制
        defaultHandler.uncaughtException(thread, ex);
    }


    /**
     * 得到程序崩溃的详细信息
     *
     * @param ex
     * @return
     */
    public String getCrashInfo(Throwable ex) {
        Writer result = new StringWriter();
        PrintWriter printWriter = new PrintWriter(result);
        ex.setStackTrace(ex.getStackTrace());
        ex.printStackTrace(printWriter);
        return result.toString();
    }


    /**
     * 收集程序崩溃的设备信息
     *
     * @return
     */
    public String collectCrashDeviceInfo() {

        String versionName = getVersionName();
        String model = android.os.Build.MODEL;
        String androidVersion = android.os.Build.VERSION.RELEASE;
        String manufacturer = android.os.Build.MANUFACTURER;

        return versionName + "  " + model + "  " + androidVersion + "  " + manufacturer;

    }

    /**
     * 获取当前应用版本号
     *
     * @return
     */
    public String getVersionName() {

        PackageManager packageManager = context.getPackageManager();
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(context.getPackageName(),
                    0);
        } catch (PackageManager.NameNotFoundException e) {

            e.printStackTrace();
            return "0.0.0";
        }
        String version = packInfo.versionName;

        return version;
    }

}

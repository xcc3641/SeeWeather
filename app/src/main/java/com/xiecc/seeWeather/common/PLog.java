package com.xiecc.seeWeather.common;

import android.util.Log;
import com.xiecc.seeWeather.BuildConfig;
import com.xiecc.seeWeather.base.BaseApplication;
import com.xiecc.seeWeather.common.utils.Time;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by zk on 2015/12/24.
 * 使用此Log类将会把信息打印在日志文件和LogCat中
 *
 * update by hugo on 2016/5/5 thanks for bruce
 */
public class PLog {
    public static boolean isDebug = BuildConfig.DEBUG;
    public static final String PATH = BaseApplication.cacheDir;
    public static final String PLOG_FILE_NAME = "log.txt";

    /**
     * 是否写入日志文件
     */
    public static final boolean PLOG_WRITE_TO_FILE = true;

    /**
     * 错误信息
     */
    public static void e(String TAG, String msg) {
        Log.e(TAG, log(msg));
        if (PLOG_WRITE_TO_FILE) {
            writeLogtoFile("e", TAG, msg);
        }
    }

    /**
     * 警告信息
     */
    public static void w(String TAG, String msg) {
        if (isDebug) {
            Log.w(TAG, log(msg));
            if (PLOG_WRITE_TO_FILE) {
                writeLogtoFile("w", TAG, msg);
            }
        }
    }

    /**
     * 调试信息
     */

    public static void d(String TAG, String msg) {
        if (isDebug) {

            Log.d(TAG, log(msg));
            if (PLOG_WRITE_TO_FILE) {
                writeLogtoFile("d", TAG, msg);
            }
        }
    }

    /**
     * 提示信息
     */
    public static void i(String TAG, String msg) {
        if (isDebug) {
            Log.i(TAG, log(msg));
            if (PLOG_WRITE_TO_FILE) {
                writeLogtoFile("i", TAG, msg);
            }
        }
    }

    public static void e(String msg) {
        e(getClassName(), msg);
    }

    public static void w(String msg) {
        w(getClassName(), msg);
    }

    public static void d(String msg) {
        d(getClassName(), msg);
    }

    public static void i(String msg) {
        i(getClassName(), msg);
    }

    /**
     * 写入日志到文件中
     */
    private static void writeLogtoFile(String mylogtype, String tag, String msg) {
        isExist(PATH);
        //isDel();
        String needWriteMessage = "\r\n"
            + Time.getNowMDHMSTime()
            + "\r\n"
            + mylogtype
            + "    "
            + tag
            + "\r\n"
            + msg;
        File file = new File(PATH, PLOG_FILE_NAME);
        try {
            FileWriter filerWriter = new FileWriter(file, true);
            BufferedWriter bufWriter = new BufferedWriter(filerWriter);
            bufWriter.write(needWriteMessage);
            bufWriter.newLine();
            bufWriter.close();
            filerWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除日志文件
     */
    public static void delFile() {

        File file = new File(PATH, PLOG_FILE_NAME);
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * 判断文件夹是否存在,如果不存在则创建文件夹
     */
    public static void isExist(String path) {
        File file = new File(path);
        if (!file.exists()) {
            try {
                file.mkdirs();
            } catch (Exception e) {
                PLog.e(e.getMessage());
            }
        }
    }

    /**
     * @return 当前的类名(simpleName)
     */
    private static String getClassName() {

        String result;
        StackTraceElement thisMethodStack = Thread.currentThread().getStackTrace()[2];
        result = thisMethodStack.getClassName();
        int lastIndex = result.lastIndexOf(".");
        result = result.substring(lastIndex + 1);

        int i = result.indexOf("$");// 剔除匿名内部类名

        return i == -1 ? result : result.substring(0, i);
    }

    /**
     * 打印 Log 行数位置
     */
    private static String log(String message) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        StackTraceElement targetElement = stackTrace[5];
        String className = targetElement.getClassName();
        className = className.substring(className.lastIndexOf('.') + 1) + ".java";
        int lineNumber = targetElement.getLineNumber();
        if (lineNumber < 0) lineNumber = 0;
        return "(" + className + ":" + lineNumber + ") " + message;
    }
}

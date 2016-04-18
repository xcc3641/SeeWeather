package com.xiecc.seeWeather.common;

import android.util.Log;
import com.xiecc.seeWeather.base.BaseApplication;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by zk on 2015/12/24.
 * 使用此Log类将会把信息打印在日志文件和LogCat中
 */
public class PLog {

    public static final String PATH = BaseApplication.cacheDir + "/Log";

    public static final String PLOG_FILE_NAME = "log.txt";

    /**
     * 是否写入日志文件
     */
    public static final boolean PLOG_WRITE_TO_FILE = true;


    /**
     * 错误信息
     *
     * @param TAG
     * @param msg
     */
    public final static void e(String TAG, String msg) {
        Log.e(TAG, msg);
        if (PLOG_WRITE_TO_FILE)
            writeLogtoFile("e", TAG, msg);
    }

    /**
     * 警告信息
     *
     * @param TAG
     * @param msg
     */
    public final static void w(String TAG, String msg) {
        Log.w(TAG, msg);
        if (PLOG_WRITE_TO_FILE)
            writeLogtoFile("w", TAG, msg);
    }

    /**
     * 调试信息
     *
     * @param TAG
     * @param msg
     */
    public final static void d(String TAG, String msg) {
        Log.d(TAG, msg);
        if (PLOG_WRITE_TO_FILE)
            writeLogtoFile("d", TAG, msg);
    }

    /**
     * 提示信息
     *
     * @param TAG
     * @param msg
     */
    public final static void i(String TAG, String msg) {
        Log.i(TAG, msg);
        if (PLOG_WRITE_TO_FILE)
            writeLogtoFile("i", TAG, msg);
    }


    /**
     * 写入日志到文件中
     *
     * @param mylogtype
     * @param tag
     * @param msg
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


    //private static void isDel() {
    //    String date = Time.getNowYMD();
    //    if (!SharedPreferencesUtil.getLogDate().equals(date)) {
    //        delFile();
    //    }
    //    SharedPreferencesUtil.setLogDate(date);
    //}


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
     *
     * @param path
     */
    public static void isExist(String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }

    }

}

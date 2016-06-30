package com.xiecc.seeWeather.common.utils;

/**
 * Created by HugoXie on 16/6/25.
 *
 * Email: Hugo3641@gamil.com
 * GitHub: https://github.com/xcc3641
 * Info: 来源 brucezz 双击退出
 */
public class DoubleClickExit {
    /**
     * 双击退出检测, 阈值 1000ms
     */
    public static long lastClick = 0L;
    private static final int THRESHOLD = 2000;// 1000ms

    public static boolean check() {
        long now = System.currentTimeMillis();
        boolean b = now - lastClick < THRESHOLD;
        lastClick = now;
        return b;
    }
}

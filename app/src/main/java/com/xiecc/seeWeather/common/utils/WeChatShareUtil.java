package com.xiecc.seeWeather.common.utils;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

/**
 * Created by HugoXie on 2017/5/21.
 * <p>
 * Email: Hugo3641@gmail.com
 * GitHub: https://github.com/xcc3641
 * Info: 临时微信分享工具
 */

public class WeChatShareUtil {

    /**
     * 分享至好友
     * @throws ActivityNotFoundException 用户未安装微信
     */
    public static void toFriends(Context context, String text) throws ActivityNotFoundException {
        Intent intent = new Intent();
        ComponentName comp = new ComponentName("com.tencent.mm",
                "com.tencent.mm.ui.tools.ShareImgUI");
        intent.setComponent(comp);
        intent.setAction("android.intent.action.SEND");
        intent.putExtra(Intent.EXTRA_TEXT, text);
        context.startActivity(intent);
    }

    /**
     * 分享至朋友圈
     * @throws ActivityNotFoundException 用户未安装微信
     */
    public static void toTimeLine(Context context, String text) throws ActivityNotFoundException {
        Intent intent = new Intent();
        ComponentName comp = new ComponentName("com.tencent.mm",
                "com.tencent.mm.ui.tools.ShareToTimeLineUI");
        intent.setComponent(comp);
        intent.setAction("android.intent.action.SEND");
        intent.putExtra(Intent.EXTRA_TEXT, text);
        context.startActivity(intent);
    }
}

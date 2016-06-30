package com.xiecc.seeWeather.common.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import com.xiecc.seeWeather.common.PLog;
import com.xiecc.seeWeather.component.RetrofitSingleton;
import com.xiecc.seeWeather.modules.about.domain.VersionAPI;

/**
 * Created by hugo on 2016/2/21 0021.
 */
public class CheckVersion {

    public static void checkVersion(final Context context) {
        RetrofitSingleton.getInstance().fetchVersion()
            .subscribe(versionAPI -> {
                String firVersionName = versionAPI.versionShort;
                String currentVersionName = Util.getVersion(context);
                if (currentVersionName.compareTo(firVersionName) < 0) {
                    showUpdateDialog(versionAPI, context);
                } else {
                    ToastUtil.showShort("已经是最新版本(⌐■_■)");
                }
            }, throwable -> {
                PLog.e(throwable.toString());
            });
    }

    public static void showUpdateDialog(final VersionAPI versionAPI, final Context context) {
        String title = "发现新版" + versionAPI.name + "版本号：" + versionAPI.versionShort;

        new AlertDialog.Builder(context).setTitle(title)
            .setMessage(versionAPI.changelog)
            .setPositiveButton("下载", (dialog, which) -> {
                Uri uri = Uri.parse(versionAPI.updateUrl);   //指定网址
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);           //指定Action
                intent.setData(uri);                            //设置Uri
                context.startActivity(intent);        //启动Activity
            })
            .show();
    }
}

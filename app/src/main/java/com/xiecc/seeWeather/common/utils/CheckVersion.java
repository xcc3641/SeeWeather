package com.xiecc.seeWeather.common.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import com.xiecc.seeWeather.component.RetrofitSingleton;
import com.xiecc.seeWeather.modules.about.domain.VersionAPI;

/**
 * Created by hugo on 2016/2/21 0021.
 */
public class CheckVersion {

    public static void checkVersion(Context context) {
        RetrofitSingleton.getInstance().fetchVersion()
            .subscribe(new SimpleSubscriber<VersionAPI>() {
                @Override
                public void onNext(VersionAPI versionAPI) {
                    String firVersionName = versionAPI.versionShort;
                    String currentVersionName = Util.getVersion(context);
                    if (currentVersionName.compareTo(firVersionName) < 0) {
                        if (!SharedPreferenceUtil.getInstance().getString("version", "").equals(versionAPI.versionShort)) {
                            showUpdateDialog(versionAPI, context);
                        }
                    }
                }
            });
    }

    public static void checkVersion(Context context, boolean force) {
        RetrofitSingleton.getInstance().fetchVersion()
            .subscribe(new SimpleSubscriber<VersionAPI>() {
                @Override
                public void onNext(VersionAPI versionAPI) {
                    String firVersionName = versionAPI.versionShort;
                    String currentVersionName = Util.getVersion(context);
                    if (currentVersionName.compareTo(firVersionName) < 0) {
                        showUpdateDialog(versionAPI, context);
                    } else {
                        ToastUtil.showShort("已经是最新版本(⌐■_■)");
                    }
                }
            });
    }

    public static void showUpdateDialog(VersionAPI versionAPI, final Context context) {
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
            .setNegativeButton("跳过此版本", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SharedPreferenceUtil.getInstance().putString("version", versionAPI.versionShort);
                }
            })
            .show();
    }
}

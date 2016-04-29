package com.xiecc.seeWeather.common;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.View;
import com.xiecc.seeWeather.component.RetrofitSingleton;
import com.xiecc.seeWeather.modules.domain.VersionAPI;
import com.xiecc.seeWeather.modules.ui.setting.Setting;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by hugo on 2016/2/21 0021.
 */
public class CheckVersion {
    private static String TAG = CheckVersion.class.getSimpleName();

    public static void checkVersion(final Context context, final View view) {
        RetrofitSingleton.getApiService(context)
            .mVersionAPI(Setting.API_TOKEN)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(versionAPI -> {
                String firVersionName = versionAPI.versionShort;
                String currentVersionName = Util.getVersion(context);
                if (currentVersionName.compareTo(firVersionName) < 0) {
                    showUpdateDialog(versionAPI, context);
                } else {
                    Snackbar.make(view, "已经是最新版本(⌐■_■)", Snackbar.LENGTH_SHORT).show();
                }
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

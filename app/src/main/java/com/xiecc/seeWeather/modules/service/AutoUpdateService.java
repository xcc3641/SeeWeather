package com.xiecc.seeWeather.modules.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import com.xiecc.seeWeather.R;
import com.xiecc.seeWeather.common.utils.SharedPreferenceUtil;
import com.xiecc.seeWeather.common.utils.Util;
import com.xiecc.seeWeather.component.RetrofitSingleton;
import com.xiecc.seeWeather.modules.main.domain.Weather;
import com.xiecc.seeWeather.modules.main.ui.MainActivity;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import java.util.concurrent.TimeUnit;

/**
 * Created by HugoXie on 16/4/18.
 *
 * Email: Hugo3641@gamil.com
 * GitHub: https://github.com/xcc3641
 */
public class AutoUpdateService extends Service {

    private final String TAG = AutoUpdateService.class.getSimpleName();
    private SharedPreferenceUtil mSharedPreferenceUtil;
    private Disposable mDisposable;
    private boolean mIsUnSubscribed = true;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mSharedPreferenceUtil = SharedPreferenceUtil.getInstance();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        synchronized (this) {
            unSubscribed();
            if (mIsUnSubscribed) {
                unSubscribed();
                if (mSharedPreferenceUtil.getAutoUpdate() != 0) {
                    mDisposable = Observable.interval(mSharedPreferenceUtil.getAutoUpdate(), TimeUnit.HOURS)
                        .doOnNext(aLong -> {
                            mIsUnSubscribed = false;
                            fetchDataByNetWork();
                        })
                        .subscribe();
                }
            }
        }
        return START_REDELIVER_INTENT;
    }

    private void unSubscribed() {
        mIsUnSubscribed = true;
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
    }

    @Override
    public boolean stopService(Intent name) {
        return super.stopService(name);
    }

    private void fetchDataByNetWork() {
        String cityName = mSharedPreferenceUtil.getCityName();
        if (cityName != null) {
            cityName = Util.replaceCity(cityName);
        }
        RetrofitSingleton.getInstance().fetchWeather(cityName)
            .subscribe(this::normalStyleNotification);
    }

    private void normalStyleNotification(Weather weather) {
        Intent intent = new Intent(AutoUpdateService.this, MainActivity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent =
            PendingIntent.getActivity(AutoUpdateService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder builder = new Notification.Builder(AutoUpdateService.this);
        Notification notification = builder.setContentIntent(pendingIntent)
            .setContentTitle(weather.basic.city)
            .setContentText(String.format("%s 当前温度: %s℃ ", weather.now.cond.txt, weather.now.tmp))
            // 这里部分 ROM 无法成功
            .setSmallIcon(mSharedPreferenceUtil.getInt(weather.now.cond.txt, R.mipmap.none))
            .build();
        notification.flags = mSharedPreferenceUtil.getNotificationModel();
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // tag和id都是可以拿来区分不同的通知的
        manager.notify(1, notification);
    }
}

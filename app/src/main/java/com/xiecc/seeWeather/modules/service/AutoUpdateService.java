package com.xiecc.seeWeather.modules.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import com.xiecc.seeWeather.R;
import com.xiecc.seeWeather.common.ACache;
import com.xiecc.seeWeather.component.RetrofitSingleton;
import com.xiecc.seeWeather.modules.ui.setting.Setting;
import com.xiecc.seeWeather.modules.domain.Weather;
import com.xiecc.seeWeather.modules.domain.WeatherAPI;
import com.xiecc.seeWeather.modules.ui.MainActivity;
import java.util.concurrent.TimeUnit;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by HugoXie on 16/4/18.
 *
 * Email: Hugo3641@gamil.com
 * GitHub: https://github.com/xcc3641
 */
public class AutoUpdateService extends Service {

    private final String TAG = AutoUpdateService.class.getSimpleName();
    private Setting mSetting;
    private ACache mAcache;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("________________________");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("________________________");
        mSetting = Setting.getInstance();
        mAcache = ACache.get(this);
        Observable.interval(mSetting.getAutoUpdate(), TimeUnit.HOURS)
            .subscribe(new Observer<Long>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onNext(Long aLong) {
                    fetchDataByNetWork();
                }
            });

        return START_REDELIVER_INTENT;
    }

    private void normalStyleNotification(Weather weather) {
        Intent intent = new Intent(AutoUpdateService.this, MainActivity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent =
            PendingIntent.getActivity(AutoUpdateService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder builder = new Notification.Builder(AutoUpdateService.this);
        Notification notification = builder.setContentIntent(pendingIntent)
            .setContentTitle(weather.basic.city)
            .setContentText(weather.now.cond.txt + " 当前温度: " + weather.now.tmp + "℃")
            // 这里部分 ROM 无法成功
            .setSmallIcon(mSetting.getInt(weather.now.cond.txt, R.mipmap.none))
            .build();
        //notification.flags = Notification.FLAG_ONGOING_EVENT; // 常驻
        notification.flags = Notification.FLAG_AUTO_CANCEL; // 普通
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(1, notification);
    }

    @Override
    public boolean stopService(Intent name) {
        return super.stopService(name);
    }

    private void fetchDataByNetWork() {
        String cityName = mSetting.getString(Setting.CITY_NAME, "北京");
        if (cityName != null) {
            cityName = cityName.replace("市", "")
                .replace("省", "")
                .replace("自治区", "")
                .replace("特别行政区", "")
                .replace("地区", "")
                .replace("盟", "");
        }
        RetrofitSingleton.getApiService(this)
            .mWeatherAPI(cityName, Setting.KEY)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .filter(new Func1<WeatherAPI, Boolean>() {
                @Override
                public Boolean call(WeatherAPI weatherAPI) {
                    return weatherAPI.mHeWeatherDataService30s.get(0).status.equals("ok");
                }
            })
            .map(new Func1<WeatherAPI, Weather>() {
                @Override
                public Weather call(WeatherAPI weatherAPI) {
                    return weatherAPI.mHeWeatherDataService30s.get(0);
                }
            })
            .subscribe(new Observer<Weather>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onNext(Weather weather) {
                    mAcache.put("WeatherData", weather);
                    normalStyleNotification(weather);
                }
            });
    }
}

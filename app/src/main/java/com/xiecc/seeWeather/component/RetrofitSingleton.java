package com.xiecc.seeWeather.component;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Toast;
import com.xiecc.seeWeather.base.C;
import com.xiecc.seeWeather.common.PLog;
import com.xiecc.seeWeather.common.utils.RxUtils;
import com.xiecc.seeWeather.modules.about.domain.VersionAPI;
import com.xiecc.seeWeather.modules.main.domain.Weather;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

/**
 * Created by zk on 2015/12/16.
 * update by hugo thanks for brucezz
 */
public class RetrofitSingleton {

    private static ApiInterface apiService = null;
    private static Retrofit retrofit = null;
    private static OkHttpClient okHttpClient = null;
    private static final String TAG = RetrofitSingleton.class.getSimpleName();

    public static void init() {
        initOkHttp();
        initRetrofit();
        apiService = retrofit.create(ApiInterface.class);
    }

    public static RetrofitSingleton getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static final RetrofitSingleton INSTANCE = new RetrofitSingleton();
    }

    private static void initOkHttp() {
        // https://drakeet.me/retrofit-2-0-okhttp-3-0-config
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        okHttpClient = new OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .retryOnConnectionFailure(true)
            .connectTimeout(15, TimeUnit.SECONDS)
            .build();
    }

    private static void initRetrofit() {
        retrofit = new Retrofit.Builder()
            .baseUrl(ApiInterface.HOST)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .build();
    }

    public static void disposeFailureInfo(Throwable t, Context context, View view) {
        if (t.toString().contains("GaiException") || t.toString().contains("SocketTimeoutException") ||
            t.toString().contains("UnknownHostException")) {
            Snackbar.make(view, "网络不好,~( ´•︵•` )~", Snackbar.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, t.getMessage(), Toast.LENGTH_LONG).show();
        }
        PLog.w(t.toString());
    }

    public Observable<Weather> fetchWeather(String city) {
        return apiService.mWeatherAPI(city, C.KEY)
            //.filter(weatherAPI -> weatherAPI.mHeWeatherDataService30s.get(0).status.equals("ok"))
            .flatMap(weatherAPI -> {
                if (weatherAPI.mHeWeatherDataService30s.get(0).status.equals("no more requests")) {
                    return Observable.error(new RuntimeException("/(ㄒoㄒ)/~~,API免费次数已用完"));
                }
                return Observable.just(weatherAPI);
            })
            .map(weatherAPI -> weatherAPI.mHeWeatherDataService30s.get(0))
            .compose(RxUtils.rxSchedulerHelper());
    }

    public Observable<VersionAPI> fetchVersion() {
        return apiService.mVersionAPI(C.API_TOKEN).compose(RxUtils.rxSchedulerHelper());
    }
}

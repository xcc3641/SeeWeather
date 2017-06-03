package com.xiecc.seeWeather.component;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.litesuits.orm.db.assit.WhereBuilder;
import com.xiecc.seeWeather.BuildConfig;
import com.xiecc.seeWeather.base.BaseApplication;
import com.xiecc.seeWeather.common.C;
import com.xiecc.seeWeather.common.utils.RxUtil;
import com.xiecc.seeWeather.common.utils.ToastUtil;
import com.xiecc.seeWeather.common.utils.Util;
import com.xiecc.seeWeather.modules.about.domain.Version;
import com.xiecc.seeWeather.modules.main.domain.CityORM;
import com.xiecc.seeWeather.modules.main.domain.Weather;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import java.io.File;
import java.util.concurrent.TimeUnit;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitSingleton {

    private static ApiInterface sApiService = null;
    private static Retrofit sRetrofit = null;
    private static OkHttpClient sOkHttpClient = null;

    private void init() {
        initOkHttp();
        initRetrofit();
        sApiService = sRetrofit.create(ApiInterface.class);
    }

    private RetrofitSingleton() {
        init();
    }

    public static RetrofitSingleton getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static final RetrofitSingleton INSTANCE = new RetrofitSingleton();
    }

    private static void initOkHttp() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        // 缓存 http://www.jianshu.com/p/93153b34310e
        File cacheFile = new File(C.NET_CACHE);
        Cache cache = new Cache(cacheFile, 1024 * 1024 * 50);
        Interceptor cacheInterceptor = chain -> {
            Request request = chain.request();
            if (!Util.isNetworkConnected(BaseApplication.getAppContext())) {
                request = request.newBuilder()
                    .cacheControl(CacheControl.FORCE_CACHE)
                    .build();
            }
            Response response = chain.proceed(request);
            Response.Builder newBuilder = response.newBuilder();
            if (Util.isNetworkConnected(BaseApplication.getAppContext())) {
                int maxAge = 0;
                // 有网络时 设置缓存超时时间0个小时
                newBuilder.header("Cache-Control", "public, max-age=" + maxAge);
            } else {
                // 无网络时，设置超时为4周
                int maxStale = 60 * 60 * 24 * 28;
                newBuilder.header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale);
            }
            return newBuilder.build();
        };
        builder.cache(cache).addInterceptor(cacheInterceptor);
        if (BuildConfig.DEBUG) {
            builder.addNetworkInterceptor(new StethoInterceptor());
        }
        //设置超时
        builder.connectTimeout(15, TimeUnit.SECONDS);
        builder.readTimeout(20, TimeUnit.SECONDS);
        builder.writeTimeout(20, TimeUnit.SECONDS);
        //错误重连
        builder.retryOnConnectionFailure(true);
        sOkHttpClient = builder.build();
    }

    private static void initRetrofit() {
        sRetrofit = new Retrofit.Builder()
            .baseUrl(ApiInterface.HOST)
            .client(sOkHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build();
    }

    private static Consumer<Throwable> disposeFailureInfo(Throwable t) {
        return throwable -> {
            if (t.toString().contains("GaiException") || t.toString().contains("SocketTimeoutException") ||
                t.toString().contains("UnknownHostException")) {
                ToastUtil.showShort("网络问题");
            } else if (t.toString().contains("API没有")) {
                OrmLite.getInstance()
                    .delete(new WhereBuilder(CityORM.class).where("name=?", Util.replaceInfo(t.getMessage())));
                ToastUtil.showShort("错误: " + t.getMessage());
            }
            PLog.w(t.getMessage());
        };
    }

    public Observable<Weather> fetchWeather(String city) {
        return sApiService.mWeatherAPI(city, C.KEY)
            .flatMap(weather -> {
                String status = weather.mWeathers.get(0).status;
                if ("no more requests".equals(status)) {
                    return Observable.error(new RuntimeException("/(ㄒoㄒ)/~~,API免费次数已用完"));
                } else if ("unknown city".equals(status)) {
                    return Observable.error(new RuntimeException(String.format("API没有%s", city)));
                }
                return Observable.just(weather);
            })
            .map(weather -> weather.mWeathers.get(0))
            .doOnError(RetrofitSingleton::disposeFailureInfo)
            .compose(RxUtil.io());
    }

    public Observable<Version> fetchVersion() {
        return sApiService.mVersionAPI(C.API_TOKEN)
            .doOnError(RetrofitSingleton::disposeFailureInfo)
            .compose(RxUtil.io());
    }
}

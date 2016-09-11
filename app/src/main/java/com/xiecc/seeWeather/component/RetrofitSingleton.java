package com.xiecc.seeWeather.component;

import com.litesuits.orm.db.assit.WhereBuilder;
import com.xiecc.seeWeather.BuildConfig;
import com.xiecc.seeWeather.base.BaseApplication;
import com.xiecc.seeWeather.base.C;
import com.xiecc.seeWeather.common.PLog;
import com.xiecc.seeWeather.common.utils.RxUtils;
import com.xiecc.seeWeather.common.utils.ToastUtil;
import com.xiecc.seeWeather.common.utils.Util;
import com.xiecc.seeWeather.modules.about.domain.VersionAPI;
import com.xiecc.seeWeather.modules.main.domain.CityORM;
import com.xiecc.seeWeather.modules.main.domain.Weather;
import java.io.File;
import java.util.concurrent.TimeUnit;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
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

    private void init() {
        initOkHttp();
        initRetrofit();
        apiService = retrofit.create(ApiInterface.class);
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
        if (BuildConfig.DEBUG) {
            // https://drakeet.me/retrofit-2-0-okhttp-3-0-config
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
            builder.addInterceptor(loggingInterceptor);
        }
        // 缓存 http://www.jianshu.com/p/93153b34310e
        File cacheFile = new File(BaseApplication.cacheDir, "/NetCache");
        Cache cache = new Cache(cacheFile, 1024 * 1024 * 50);
        Interceptor cacheInterceptor = chain -> {
            Request request = chain.request();
            if (!Util.isNetworkConnected(BaseApplication.getmAppContext())) {
                request = request.newBuilder()
                    .cacheControl(CacheControl.FORCE_CACHE)
                    .build();
            }
            Response response = chain.proceed(request);
            if (Util.isNetworkConnected(BaseApplication.getmAppContext())) {
                int maxAge = 0;
                // 有网络时 设置缓存超时时间0个小时
                response.newBuilder()
                    .header("Cache-Control", "public, max-age=" + maxAge)
                    .build();
            } else {
                // 无网络时，设置超时为4周
                int maxStale = 60 * 60 * 24 * 28;
                response.newBuilder()
                    .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                    .build();
            }
            return response;
        };
        builder.cache(cache).addInterceptor(cacheInterceptor);
        //设置超时
        builder.connectTimeout(15, TimeUnit.SECONDS);
        builder.readTimeout(20, TimeUnit.SECONDS);
        builder.writeTimeout(20, TimeUnit.SECONDS);
        //错误重连
        builder.retryOnConnectionFailure(true);
        okHttpClient = builder.build();
    }

    private static void initRetrofit() {
        retrofit = new Retrofit.Builder()
            .baseUrl(ApiInterface.HOST)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .build();
    }

    public static void disposeFailureInfo(Throwable t) {
        if (t.toString().contains("GaiException") || t.toString().contains("SocketTimeoutException") ||
            t.toString().contains("UnknownHostException")) {
            ToastUtil.showShort("网络问题");
        } else if (t.toString().contains("API没有")) {
            OrmLite.getInstance().delete(new WhereBuilder(CityORM.class).where("name=?", Util.replaceInfo(t.getMessage())));
            PLog.w(Util.replaceInfo(t.getMessage()));
            ToastUtil.showShort("错误: "+t.getMessage());
        }
        PLog.w(t.getMessage());
    }

    public ApiInterface getApiService(){
        return apiService;
    }

    public Observable<Weather> fetchWeather(String city) {

        return apiService.mWeatherAPI(city, C.KEY)
            .flatMap(weatherAPI -> {
                String status = weatherAPI.mHeWeatherDataService30s.get(0).status;
                if ("no more requests".equals(status)) {
                    return Observable.error(new RuntimeException("/(ㄒoㄒ)/~~,API免费次数已用完"));
                } else if ("unknown city".equals(status)) {
                    return Observable.error(new RuntimeException(String.format("API没有%s", city)));
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

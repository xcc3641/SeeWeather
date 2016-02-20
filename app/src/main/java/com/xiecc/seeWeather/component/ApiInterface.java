package com.xiecc.seeWeather.component;

import com.xiecc.seeWeather.modules.domain.WeatherAPI;
import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by hugo on 2016/2/16 0016.
 */
public interface ApiInterface {

    String HOST = "https://api.heweather.com/x3/";

    @GET("weather") Observable<WeatherAPI> mWeatherAPI(@Query("city") String city, @Query("key") String key);
}

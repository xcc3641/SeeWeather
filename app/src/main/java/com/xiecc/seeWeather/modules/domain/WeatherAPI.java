package com.xiecc.seeWeather.modules.domain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

public class WeatherAPI {

    @SerializedName("HeWeather data service 3.0") @Expose
    public List<Weather> mHeWeatherDataService30s
            = new ArrayList<>();
}

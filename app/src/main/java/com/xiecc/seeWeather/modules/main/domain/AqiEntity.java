package com.xiecc.seeWeather.modules.main.domain;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class AqiEntity implements Serializable {

    @SerializedName("city")
    public CityEntity city;
}
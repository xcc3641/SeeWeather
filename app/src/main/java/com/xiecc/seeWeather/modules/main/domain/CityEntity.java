package com.xiecc.seeWeather.modules.main.domain;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class CityEntity implements Serializable {
    @SerializedName("aqi")
    public String aqi;
    @SerializedName("co")
    public String co;
    @SerializedName("no2")
    public String no2;
    @SerializedName("o3")
    public String o3;
    @SerializedName("pm10")
    public String pm10;
    @SerializedName("pm25")
    public String pm25;
    @SerializedName("qlty")
    public String qlty;
    @SerializedName("so2")
    public String so2;
}
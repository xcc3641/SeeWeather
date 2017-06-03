package com.xiecc.seeWeather.modules.main.domain;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class HourlyForecastEntity implements Serializable {
    @SerializedName("date")
    public String date;
    @SerializedName("hum")
    public String hum;
    @SerializedName("pop")
    public String pop;
    @SerializedName("pres")
    public String pres;
    @SerializedName("tmp")
    public String tmp;

    @SerializedName("wind")
    public WindEntity wind;
}
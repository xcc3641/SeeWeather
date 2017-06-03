package com.xiecc.seeWeather.modules.main.domain;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class BasicEntity implements Serializable {
    @SerializedName("city")
    public String city;
}
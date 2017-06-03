package com.xiecc.seeWeather.modules.main.domain;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public  class TmpEntity implements Serializable {
    @SerializedName("max")
    public String max;
    @SerializedName("min")
    public String min;
}
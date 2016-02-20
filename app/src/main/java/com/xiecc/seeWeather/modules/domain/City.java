package com.xiecc.seeWeather.modules.domain;

/**
 * Created by hugo on 2015/9/30 0030.
 */
public class City {

    private String CityName;
    private int ProID;


    public String getCityName() {
        return CityName;
    }

    public int getProID() {
        return ProID;
    }

    public void setCityName(String cityName) {
        CityName = cityName;
    }

    public void setProID(int proID) {
        ProID = proID;
    }
}

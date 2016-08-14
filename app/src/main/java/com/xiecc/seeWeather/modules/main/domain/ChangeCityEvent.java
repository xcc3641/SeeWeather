package com.xiecc.seeWeather.modules.main.domain;

/**
 * Created by HugoXie on 16/6/2.
 *
 * Email: Hugo3641@gamil.com
 * GitHub: https://github.com/xcc3641
 * Info:
 */
public class ChangeCityEvent {

    String city;
    boolean isSetting;

    public ChangeCityEvent() {
    }

    public ChangeCityEvent(boolean isSetting) {
        this.isSetting = isSetting;
    }

    public ChangeCityEvent(String city) {
        this.city = city;
    }
}

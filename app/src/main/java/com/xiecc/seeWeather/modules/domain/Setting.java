package com.xiecc.seeWeather.modules.domain;

import android.content.Context;
import android.content.SharedPreferences;
import com.xiecc.seeWeather.base.BaseApplication;

/**
 * 设置相关
 * Created by hugo on 2016/2/19 0019.
 */
public class Setting {

    public static final String CHANGE_ICONS = "change_icons";
    public static final String CLEAR_CACHE = "clear_cache";
    public static final String CITY_NAME = "城市";
    public static final String HOUR ="小时";
    private static Setting sInstance;

    private SharedPreferences mPrefs;


    public static Setting getInstance() {
        if (sInstance == null) {
            sInstance = new Setting(BaseApplication.mAppContext);
        }
        return sInstance;
    }


    private Setting(Context context) {
        mPrefs = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
        mPrefs.edit().putInt(CHANGE_ICONS, 1).apply();
    }


    public Setting putBoolean(String key, boolean value) {
        mPrefs.edit().putBoolean(key, value).apply();
        return this;
    }


    public boolean getBoolean(String key, boolean def) {
        return mPrefs.getBoolean(key, def);
    }


    public Setting putInt(String key, int value) {
        mPrefs.edit().putInt(key, value).apply();
        return this;
    }


    public int getInt(String key, int defValue) {
        return mPrefs.getInt(key, defValue);
    }


    public Setting putString(String key, String value) {
        mPrefs.edit().putString(key, value).apply();
        return this;
    }


    public String getString(String key, String defValue) {
        return mPrefs.getString(key, defValue);
    }
}

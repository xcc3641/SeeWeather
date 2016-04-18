package com.xiecc.seeWeather.modules.ui.setting;

import android.content.Context;
import android.content.SharedPreferences;
import com.xiecc.seeWeather.base.BaseApplication;

/**
 * Created by hugo on 2016/2/19 0019.
 *
 * 设置相关 包括 sp 的写入
 */
public class Setting {

    public static final String CHANGE_ICONS = "change_icons";//切换图标
    public static final String CLEAR_CACHE = "clear_cache";//清空缓存
    public static final String AUTO_UPDATE = "change_update_time"; //自动更新时长
    public static final String CITY_NAME = "城市";//选择城市
    public static final String HOUR = "小时";//当前小时
    public static final String HOUR_SELECT = "hour_select"; //设置更新频率的联动-需要改进

    public static final String API_TOKEN = "7db041d0c3013b63e4bed2a554f02d85";//fir.im api_token
    public static final String KEY = "282f3846df6b41178e4a2218ae083ea7";// 和风天气 key

    public static int ONE_HOUR = 3600;

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
        //mPrefs.edit().putInt(CHANGE_ICONS, 1).apply();
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

    // 图标种类相关
    public void setIconType(int type) {
        mPrefs.edit().putInt(Setting.CHANGE_ICONS, type).apply();
    }

    public int getIconType() {
        return mPrefs.getInt(Setting.CHANGE_ICONS, 0);
    }

    // 自动更新时间 hours
    public void setAutoUpdate(int t) {
        mPrefs.edit().putInt(Setting.AUTO_UPDATE, t).apply();
    }

    public int getAutoUpdate() {
        return mPrefs.getInt(Setting.AUTO_UPDATE, 3);
    }

    //当前城市
    public void setCityName(String name) {
        mPrefs.edit().putString(Setting.CITY_NAME, name).apply();
    }

    public String getCityName() {

        return mPrefs.getString(Setting.CITY_NAME, "北京");
    }



}

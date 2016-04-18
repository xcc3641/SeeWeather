package com.xiecc.seeWeather.modules.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.xiecc.seeWeather.modules.domain.City;
import com.xiecc.seeWeather.modules.domain.Province;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hugo on 2015/9/30 0030.
 * 封装数据库操作
 */
public class WeatherDB {

    private Context context;


    public WeatherDB(Context context) {
        this.context = context;
    }


    public List<Province> loadProvinces(SQLiteDatabase db) {

        List<Province> list = new ArrayList<>();

        Cursor cursor = db.query("T_Province", null, null, null, null, null, null);

        if (cursor.moveToFirst()) {

            do {
                Province province = new Province();
                province.ProSort = cursor.getInt(cursor.getColumnIndex("ProSort"));
                province.ProName = cursor.getString(cursor.getColumnIndex("ProName"));
                list.add(province);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }


    public List<City> loadCities(SQLiteDatabase db, int ProID) {
        List<City> list = new ArrayList<>();
        Cursor cursor = db.query("T_City", null, "ProID = ?", new String[] { String.valueOf(ProID) }, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                City city = new City();

                city.CityName = cursor.getString(cursor.getColumnIndex("CityName"));
                city.ProID = ProID;
                list.add(city);
                //city.setCityName(cursor.getString(cursor.getColumnIndex("CityName")));
                //city.setProID(ProID);
                //list.add(city);
            } while (cursor.moveToNext());
        } cursor.close();

        return list;
    }
}

package com.xiecc.seeWeather.modules.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.xiecc.seeWeather.R;
import com.xiecc.seeWeather.base.BaseActivity;
import com.xiecc.seeWeather.common.PLog;
import com.xiecc.seeWeather.modules.adatper.CityAdapter;
import com.xiecc.seeWeather.modules.db.DBManager;
import com.xiecc.seeWeather.modules.db.WeatherDB;
import com.xiecc.seeWeather.modules.domain.City;
import com.xiecc.seeWeather.modules.domain.Province;
import com.xiecc.seeWeather.modules.domain.Setting;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hugo on 2016/2/19 0019.
 */
public class ChoiceCityActivity extends BaseActivity {
    private static String TAG = ChoiceCityActivity.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private ContentLoadingProgressBar mProgressBar;
    private DBManager mDBManager;
    private WeatherDB mWeatherDB;

    private ArrayList<String> dataList = new ArrayList<>();
    private Province selectedProvince;
    private City selectedCity;
    private List<Province> provincesList;
    private List<City> cityList;
    private CityAdapter mAdapter;

    public static final int LEVEL_PROVINCE = 1;
    public static final int LEVEL_CITY = 2;
    private int currentLevel = 0;


    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice_city);
        mDBManager = new DBManager(this);
        mDBManager.openDatabase();
        mWeatherDB = new WeatherDB(this);

        initView();
        mProgressBar.show();

        initRecyclerView();
        queryProvinces();

        if (mAdapter.getItemCount() != 0) {
            mProgressBar.hide();
            mRecyclerView.setVisibility(View.VISIBLE);
        }
    }


    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("选择城市");
        setSupportActionBar(toolbar);
        ImageView bannner = (ImageView) findViewById(R.id.bannner);
        mProgressBar = (ContentLoadingProgressBar) findViewById(R.id.progressbar);
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        if (mSetting.getInt(Setting.HOUR, 0) < 6 || mSetting.getInt(Setting.HOUR, 0) > 18) {
            collapsingToolbarLayout.setContentScrimColor(ContextCompat.getColor(this, R.color.colorSunset));
            Glide.with(this).load(R.mipmap.city_night).diskCacheStrategy(DiskCacheStrategy.ALL).into(bannner);
        }
    }


    private void initRecyclerView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new CityAdapter(this, dataList);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new CityAdapter.OnRecyclerViewItemClickListener() {
            @Override public void onItemClick(View view, int pos) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provincesList.get(pos);
                    mRecyclerView.scrollTo(0, 0);
                    queryCities();
                }
                else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(pos);
                    Intent intent = new Intent();
                    String cityName = selectedCity.getCityName();
                    if (cityName != null) {
                        cityName = cityName.replace("市", "")
                                           .replace("省", "")
                                           .replace("自治区", "")
                                           .replace("特别行政区", "")
                                           .replace("地区", "")
                                           .replace("盟", "");
                    }
                    intent.putExtra(Setting.CITY_NAME, cityName);
                    setResult(2, intent);
                    finish();
                }
            }
        });
    }


    /**
     * 查询全国所有的省，从数据库查询
     */
    private void queryProvinces() {
        provincesList = mWeatherDB.loadProvinces(mDBManager.getDatabase());
        if (provincesList.size() > 0) {
            dataList.clear();
            for (Province province : provincesList) {
                dataList.add(province.getProName());
            }
            mAdapter.notifyDataSetChanged();
            currentLevel = LEVEL_PROVINCE;
        }
    }


    /**
     * 查询选中省份的所有城市，从数据库查询
     */
    private void queryCities() {
        cityList = mWeatherDB.loadCities(mDBManager.getDatabase(), selectedProvince.getProSort());
        if (cityList.size() > 0) {
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getCityName());
            }
            mAdapter.notifyDataSetChanged();
            //定位到第一个item
            //listView.setSelection(0);
            mRecyclerView.smoothScrollToPosition(0);
            currentLevel = LEVEL_CITY;
        }
    }


    @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (currentLevel == LEVEL_PROVINCE) {
                finish();
            }
            else {
                queryProvinces();
                mRecyclerView.smoothScrollToPosition(0);
            }
        }
        return false;
    }


    @Override protected void onDestroy() {
        super.onDestroy();
        mDBManager.closeDatabase();
    }
}

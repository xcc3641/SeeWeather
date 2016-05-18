package com.xiecc.seeWeather.modules.city.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.xiecc.seeWeather.R;
import com.xiecc.seeWeather.base.BaseActivity;
import com.xiecc.seeWeather.common.PLog;
import com.xiecc.seeWeather.component.ImageLoader;
import com.xiecc.seeWeather.modules.city.adapter.CityAdapter;
import com.xiecc.seeWeather.modules.city.db.DBManager;
import com.xiecc.seeWeather.modules.city.db.WeatherDB;
import com.xiecc.seeWeather.modules.city.domain.City;
import com.xiecc.seeWeather.modules.city.domain.Province;
import com.xiecc.seeWeather.modules.setting.Setting;
import java.util.ArrayList;
import java.util.List;
import jp.wasabeef.recyclerview.animators.FadeInUpAnimator;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by hugo on 2016/2/19 0019.
 */
public class ChoiceCityActivity extends BaseActivity {
    @Bind(R.id.banner)
    ImageView banner;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.toolbar_layout)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @Bind(R.id.app_bar)
    AppBarLayout appBar;
    @Bind(R.id.recyclerview)
    RecyclerView mRecyclerView;
    @Bind(R.id.progressBar)
    ProgressBar mProgressBar;
    @Bind(R.id.iv_erro)
    ImageView ivErro;
    @Bind(R.id.coord)
    CoordinatorLayout coord;

    private DBManager mDBManager;

    private ArrayList<String> dataList = new ArrayList<>();
    private Province selectedProvince;
    private City selectedCity;
    private List<Province> provincesList;
    private List<City> cityList;
    private CityAdapter mAdapter;

    public static final int LEVEL_PROVINCE = 1;
    public static final int LEVEL_CITY = 2;
    private int currentLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice_city);
        ButterKnife.bind(this);

        Observable.defer(() -> {
            mDBManager = new DBManager(ChoiceCityActivity.this);
            mDBManager.openDatabase();
            //mWeatherDB = new WeatherDB(ChoiceCityActivity.this);
            return Observable.just(1);
        }).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(integer -> {
                initView();
                initRecyclerView();
                queryProvinces();
            });
    }

    private void initView() {
        setStatusBarColorForKitkat(R.color.colorSunrise);
        if (banner != null) {
            ImageLoader.loadAndDiskCache(this, R.mipmap.city_day, banner);
            if (mSetting.getCurrentHour() < 6 || mSetting.getCurrentHour() > 18) {
                collapsingToolbarLayout.setContentScrimColor(ContextCompat.getColor(this, R.color.colorSunset));
                ImageLoader.loadAndDiskCache(this, R.mipmap.city_night, banner);
                setStatusBarColorForKitkat(R.color.colorSunset);
            }
        }
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }

    private void initRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new FadeInUpAnimator());
        mAdapter = new CityAdapter(this, dataList);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener((view, pos) -> {
            if (currentLevel == LEVEL_PROVINCE) {
                selectedProvince = provincesList.get(pos);
                mRecyclerView.smoothScrollToPosition(0);
                queryCities();
            } else if (currentLevel == LEVEL_CITY) {
                selectedCity = cityList.get(pos);
                Intent intent = new Intent();
                String cityName = selectedCity.CityName;
                intent.putExtra(Setting.CITY_NAME, cityName);
                setResult(2, intent);
                finish();
            }
        });
    }

    /**
     * 查询全国所有的省，从数据库查询
     */
    private void queryProvinces() {
        collapsingToolbarLayout.setTitle("选择省份");
        Observable.defer(() -> {
            provincesList = WeatherDB.loadProvinces(mDBManager.getDatabase());
            dataList.clear();
            return Observable.from(provincesList);
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
            province -> dataList.add(province.ProName), throwable -> PLog.e(throwable.toString()), () -> {
                currentLevel = LEVEL_PROVINCE;
                mAdapter.notifyDataSetChanged();
                mProgressBar.setVisibility(View.GONE);
            }
        );
    }

    /**
     * 查询选中省份的所有城市，从数据库查询
     */
    private void queryCities() {
        dataList.clear();
        mAdapter.notifyDataSetChanged();
        collapsingToolbarLayout.setTitle(selectedProvince.ProName);
        Observable.defer(() -> {
            cityList = WeatherDB.loadCities(mDBManager.getDatabase(), selectedProvince.ProSort);
            return Observable.from(cityList);
        }).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(city -> dataList.add(city.CityName), throwable -> PLog.e(throwable.toString()), () -> {
                currentLevel = LEVEL_CITY;
                mAdapter.notifyDataSetChanged();
                //定位到第一个item
                mRecyclerView.smoothScrollToPosition(0);
            });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (currentLevel == LEVEL_PROVINCE) {
                finish();
            } else {
                queryProvinces();
                mRecyclerView.smoothScrollToPosition(0);
            }
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDBManager.closeDatabase();
    }
}

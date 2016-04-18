package com.xiecc.seeWeather.modules.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.xiecc.seeWeather.R;
import com.xiecc.seeWeather.base.BaseActivity;
import com.xiecc.seeWeather.modules.adatper.CityAdapter;
import com.xiecc.seeWeather.modules.db.DBManager;
import com.xiecc.seeWeather.modules.db.WeatherDB;
import com.xiecc.seeWeather.modules.domain.City;
import com.xiecc.seeWeather.modules.domain.Province;
import com.xiecc.seeWeather.modules.ui.setting.Setting;
import java.util.ArrayList;
import java.util.List;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func0;
import rx.schedulers.Schedulers;

/**
 * Created by hugo on 2016/2/19 0019.
 */
public class ChoiceCityActivity extends BaseActivity {
    private static String TAG = ChoiceCityActivity.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private DBManager mDBManager;
    private WeatherDB mWeatherDB;
    private CollapsingToolbarLayout collapsingToolbarLayout;

    private ArrayList<String> dataList = new ArrayList<>();
    private Province selectedProvince;
    private City selectedCity;
    private List<Province> provincesList;
    private List<City> cityList;
    private CityAdapter mAdapter;

    public static final int LEVEL_PROVINCE = 1;
    public static final int LEVEL_CITY = 2;
    private int currentLevel;


    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice_city);
        mDBManager = new DBManager(this);
        mDBManager.openDatabase();
        mWeatherDB = new WeatherDB(this);
        initView();
        initRecyclerView();
        queryProvinces();
    }


    private void initView() {
        //toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setTitle("选择城市");
        //setSupportActionBar(toolbar);
        ImageView bannner = (ImageView) findViewById(R.id.bannner);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        setStatusBarColorForKitkat(R.color.colorSunrise);
        if (mSetting.getInt(Setting.HOUR, 0) < 6 || mSetting.getInt(Setting.HOUR, 0) > 18) {
            collapsingToolbarLayout.setContentScrimColor(ContextCompat.getColor(this, R.color.colorSunset));
            Glide.with(this).load(R.mipmap.city_night).diskCacheStrategy(DiskCacheStrategy.ALL).into(bannner);
            setStatusBarColorForKitkat(R.color.colorSunset);
        }
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.VISIBLE);
    }


    private void initRecyclerView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new CityAdapter(this, dataList);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new CityAdapter.OnRecyclerViewItemClickListener() {
            @Override public void onItemClick(View view, final int pos) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provincesList.get(pos);
                    mRecyclerView.scrollTo(0, 0);
                    queryCities();
                }
                else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(pos);
                    Intent intent = new Intent();
                    String cityName = selectedCity.CityName;
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
        collapsingToolbarLayout.setTitle("选择省份");
        Observer<Province> observer = new Observer<Province>() {
            @Override public void onCompleted() {
                currentLevel = LEVEL_PROVINCE;
                mAdapter.notifyDataSetChanged();
                mProgressBar.setVisibility(View.GONE);
            }


            @Override public void onError(Throwable e) {

            }


            @Override public void onNext(Province province) {
                dataList.add(province.ProName);
            }
        };

        Observable.defer(new Func0<Observable<Province>>() {
            @Override public Observable<Province> call() {
                provincesList = mWeatherDB.loadProvinces(mDBManager.getDatabase());
                dataList.clear();
                return Observable.from(provincesList);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(observer);



    }


    /**
     * 查询选中省份的所有城市，从数据库查询
     */
    private void queryCities() {
        dataList.clear();
        collapsingToolbarLayout.setTitle(selectedProvince.ProName);
        Observer<City> observer = new Observer<City>() {
            @Override public void onCompleted() {
                currentLevel = LEVEL_CITY;
                mAdapter.notifyDataSetChanged();
                //定位到第一个item
                mRecyclerView.smoothScrollToPosition(0);
            }


            @Override public void onError(Throwable e) {

            }


            @Override public void onNext(City city) {
                dataList.add(city.CityName);
            }
        };


        Observable.defer(new Func0<Observable<City>>() {
            @Override public Observable<City> call() {
                cityList = mWeatherDB.loadCities(mDBManager.getDatabase(), selectedProvince.ProSort);
                return Observable.from(cityList);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
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

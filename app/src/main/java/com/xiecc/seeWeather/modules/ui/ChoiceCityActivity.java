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
import jp.wasabeef.recyclerview.animators.FadeInUpAnimator;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
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

        //RxPermissions.getInstance(this).requestEach(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE)
        //    .subscribe(permission ->{
        //       if (permission.granted){
        //
        //       }
        //    });
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
        if (mSetting.getCurrentHour()< 6 || mSetting.getCurrentHour() > 18) {
            collapsingToolbarLayout.setContentScrimColor(ContextCompat.getColor(this, R.color.colorSunset));
            Glide.with(this).load(R.mipmap.city_night).diskCacheStrategy(DiskCacheStrategy.ALL).into(bannner);
            setStatusBarColorForKitkat(R.color.colorSunset);
        }
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }


    private void initRecyclerView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new FadeInUpAnimator());
        mAdapter = new CityAdapter(this, dataList);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener((view, pos) -> {
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
                //PLog.i(TAG,"省份");
            }


            @Override public void onError(Throwable e) {

            }


            @Override public void onNext(Province province) {
                //在这里做 RV 的动画效果 使用 Item 的更新
                dataList.add(province.ProName);
                //PLog.i(TAG,province.ProSort+"");
                //mAdapter.notifyItemInserted(province.ProSort-1);

            }
        };

        Observable.defer(() -> {
            provincesList = mWeatherDB.loadProvinces(mDBManager.getDatabase());
            dataList.clear();
            mAdapter.notifyDataSetChanged();
            return Observable.from(provincesList);
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(observer);



    }


    /**
     * 查询选中省份的所有城市，从数据库查询
     */
    private void queryCities() {
        dataList.clear();
        mAdapter.notifyDataSetChanged();
        collapsingToolbarLayout.setTitle(selectedProvince.ProName);
        Observer<City> observer = new Observer<City>() {
            @Override public void onCompleted() {
                currentLevel = LEVEL_CITY;
                mAdapter.notifyDataSetChanged();
                //定位到第一个item
                mRecyclerView.smoothScrollToPosition(0);
                //PLog.i(TAG,"城市");
            }


            @Override public void onError(Throwable e) {

            }


            @Override public void onNext(City city) {
                dataList.add(city.CityName);
                //mAdapter.notifyItemInserted(city.CitySort);
            }
        };


        Observable.defer(() -> {
            cityList = mWeatherDB.loadCities(mDBManager.getDatabase(), selectedProvince.ProSort);
            return Observable.from(cityList);
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

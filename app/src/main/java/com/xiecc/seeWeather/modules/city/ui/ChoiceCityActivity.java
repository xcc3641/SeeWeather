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
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.xiecc.seeWeather.R;
import com.xiecc.seeWeather.base.BaseActivity;
import com.xiecc.seeWeather.base.RxBus;
import com.xiecc.seeWeather.common.PLog;
import com.xiecc.seeWeather.common.utils.RxUtils;
import com.xiecc.seeWeather.component.ImageLoader;
import com.xiecc.seeWeather.modules.city.adapter.CityAdapter;
import com.xiecc.seeWeather.modules.city.db.DBManager;
import com.xiecc.seeWeather.modules.city.db.WeatherDB;
import com.xiecc.seeWeather.modules.city.domain.City;
import com.xiecc.seeWeather.modules.city.domain.Province;
import com.xiecc.seeWeather.modules.main.domain.ChangeCityEvent;
import java.util.ArrayList;
import java.util.List;
import jp.wasabeef.recyclerview.animators.FadeInUpAnimator;
import rx.Observable;

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
    private List<Province> provincesList = new ArrayList<>();
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
        initView();
        Observable.defer(() -> {
            mDBManager = new DBManager(ChoiceCityActivity.this);
            mDBManager.openDatabase();
            return Observable.just(1);
        }).compose(RxUtils.rxSchedulerHelper())
            .subscribe(integer -> {
                initRecyclerView();
                queryProvinces();
            });
    }

    private void initView() {
        setStatusBarColorForKitkat(R.color.colorSunrise);
        if (banner != null) {
            ImageLoader.load(this, R.mipmap.city_day, banner);
            if (mSetting.getCurrentHour() < 6 || mSetting.getCurrentHour() > 18) {
                collapsingToolbarLayout.setContentScrimColor(ContextCompat.getColor(this, R.color.colorSunset));
                ImageLoader.load(this, R.mipmap.city_night, banner);
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
                //intent.putExtra(Setting.CITY_NAME, cityName);
                //setResult(2, intent);
                mSetting.setCityName(cityName);
                RxBus.getDefault().post(new ChangeCityEvent());

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
            if (provincesList.isEmpty()) {
                provincesList.addAll(WeatherDB.loadProvinces(mDBManager.getDatabase()));
            }
            dataList.clear();
            return Observable.from(provincesList);
        })
            .map(province -> province.ProName)
            //.delay(60, TimeUnit.MILLISECONDS, Schedulers.immediate())
            //.onBackpressureBuffer() // 会缓存所有当前无法消费的数据，直到 Observer 可以处理为止
            .toList()
            .compose(RxUtils.rxSchedulerHelper())
            .doOnTerminate(() -> mProgressBar.setVisibility(View.GONE))
            .subscribe(
                province -> dataList.addAll(province)
                , throwable -> PLog.e(throwable.toString()), () -> {
                    currentLevel = LEVEL_PROVINCE;
                    mAdapter.notifyDataSetChanged();
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
        })
            .map(city -> city.CityName)
            .toList()
            .compose(RxUtils.rxSchedulerHelper())
            .subscribe(city -> dataList.addAll(city), throwable -> {
            }, () -> {
                currentLevel = LEVEL_CITY;
                mAdapter.notifyDataSetChanged();
                //定位到第一个item
                mRecyclerView.smoothScrollToPosition(0);
            });
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();  http://www.eoeandroid.com/thread-275312-1-1.html 这里的坑
        if (currentLevel == LEVEL_PROVINCE) {
            finish();
        } else {
            queryProvinces();
            mRecyclerView.smoothScrollToPosition(0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDBManager.closeDatabase();
        ButterKnife.unbind(this);
    }
}

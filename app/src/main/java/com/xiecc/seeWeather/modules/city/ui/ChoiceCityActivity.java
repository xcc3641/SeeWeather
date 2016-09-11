package com.xiecc.seeWeather.modules.city.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import com.xiecc.seeWeather.R;
import com.xiecc.seeWeather.base.C;
import com.xiecc.seeWeather.base.ToolbarActivity;
import com.xiecc.seeWeather.common.PLog;
import com.xiecc.seeWeather.common.utils.RxUtils;
import com.xiecc.seeWeather.common.utils.SharedPreferenceUtil;
import com.xiecc.seeWeather.common.utils.SimpleSubscriber;
import com.xiecc.seeWeather.common.utils.Util;
import com.xiecc.seeWeather.component.OrmLite;
import com.xiecc.seeWeather.component.RxBus;
import com.xiecc.seeWeather.modules.city.adapter.CityAdapter;
import com.xiecc.seeWeather.modules.city.db.DBManager;
import com.xiecc.seeWeather.modules.city.db.WeatherDB;
import com.xiecc.seeWeather.modules.city.domain.City;
import com.xiecc.seeWeather.modules.city.domain.Province;
import com.xiecc.seeWeather.modules.main.domain.ChangeCityEvent;
import com.xiecc.seeWeather.modules.main.domain.CityORM;
import com.xiecc.seeWeather.modules.main.domain.MultiUpdate;
import java.util.ArrayList;
import java.util.List;
import rx.Observable;
import rx.functions.Action0;

/**
 * Created by hugo on 2016/2/19 0019.
 */
public class ChoiceCityActivity extends ToolbarActivity {

    private RecyclerView mRecyclerview;
    private ProgressBar mProgressBar;

    private ArrayList<String> dataList = new ArrayList<>();
    private Province selectedProvince;
    private City selectedCity;
    private List<Province> provincesList = new ArrayList<>();
    private List<City> cityList;
    private CityAdapter mAdapter;

    public static final int LEVEL_PROVINCE = 1;
    public static final int LEVEL_CITY = 2;
    private int currentLevel;

    private boolean isChecked = false;

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_choice_city;
    }

    @Override
    public boolean canBack() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();

        Observable.defer(() -> {
            //mDBManager = new DBManager(ChoiceCityActivity.this);
            DBManager.getInstance().openDatabase();
            return Observable.just(1);
        }).compose(RxUtils.rxSchedulerHelper())
            .compose(this.bindToLifecycle())
            .subscribe(integer -> {
                initRecyclerView();
                queryProvinces();
            });
        Intent intent = getIntent();
        isChecked = intent.getBooleanExtra(C.MULTI_CHECK, false);
        if (isChecked && SharedPreferenceUtil.getInstance().getBoolean("Tips", true)) {
            showTips();
        }
    }

    private void initView() {
        mRecyclerview = (RecyclerView) findViewById(R.id.recyclerview);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }

    private void initRecyclerView() {
        mRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerview.setHasFixedSize(true);
        //mRecyclerview.setItemAnimator(new FadeInUpAnimator());
        mAdapter = new CityAdapter(this, dataList);
        mRecyclerview.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener((view, pos) -> {
            if (currentLevel == LEVEL_PROVINCE) {
                selectedProvince = provincesList.get(pos);
                mRecyclerview.smoothScrollToPosition(0);
                queryCities();
            } else if (currentLevel == LEVEL_CITY) {
                String city = Util.replaceCity(cityList.get(pos).CityName);
                if (isChecked) {
                    OrmLite.getInstance().save(new CityORM(city));
                    RxBus.getDefault().post(new MultiUpdate());
                    PLog.d("是多城市管理模式");
                } else {
                    SharedPreferenceUtil.getInstance().setCityName(city);
                    RxBus.getDefault().post(new ChangeCityEvent());
                }
                finish();
            }
        });
    }

    /**
     * 查询全国所有的省，从数据库查询
     */
    private void queryProvinces() {
        getToolbar().setTitle("选择省份");
        Observable.defer(() -> {
            if (provincesList.isEmpty()) {
                provincesList.addAll(WeatherDB.loadProvinces(DBManager.getInstance().getDatabase()));
            }
            dataList.clear();
            return Observable.from(provincesList);
        })
            .map(province -> province.ProName)
            //.delay(60, TimeUnit.MILLISECONDS, Schedulers.immediate())
            //.onBackpressureBuffer() // 会缓存所有当前无法消费的数据，直到 Observer 可以处理为止
            .toList()
            .compose(RxUtils.rxSchedulerHelper())
            .compose(this.bindToLifecycle())
            .doOnTerminate(() -> mProgressBar.setVisibility(View.GONE))
            .doOnCompleted(new Action0() {
                @Override
                public void call() {
                    currentLevel = LEVEL_PROVINCE;
                    mAdapter.notifyDataSetChanged();
                }
            })
            .subscribe(new SimpleSubscriber<List<String>>() {
                @Override
                public void onNext(List<String> strings) {
                    dataList.addAll(strings);
                }
            });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.multi_city_menu, menu);
        menu.getItem(0).setChecked(isChecked);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.multi_check) {
            if (isChecked) {
                item.setChecked(false);
            } else {
                item.setChecked(true);
            }
            isChecked = item.isChecked();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 查询选中省份的所有城市，从数据库查询
     */
    private void queryCities() {
        getToolbar().setTitle("选择城市");
        dataList.clear();
        mAdapter.notifyDataSetChanged();
        Observable.defer(() -> {
            cityList = WeatherDB.loadCities(DBManager.getInstance().getDatabase(), selectedProvince.ProSort);
            return Observable.from(cityList);
        })

            .map(city -> city.CityName)
            .toList()
            .compose(RxUtils.rxSchedulerHelper())
            .compose(this.bindToLifecycle())
            .doOnCompleted(new Action0() {
                @Override
                public void call() {
                    currentLevel = LEVEL_CITY;
                    mAdapter.notifyDataSetChanged();
                    //定位到第一个item
                    mRecyclerview.smoothScrollToPosition(0);
                }
            })
            .subscribe(new SimpleSubscriber<List<String>>() {
                @Override
                public void onNext(List<String> strings) {
                    dataList.addAll(strings);
                }
            });
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();  http://www.eoeandroid.com/thread-275312-1-1.html 这里的坑
        if (currentLevel == LEVEL_PROVINCE) {
            finish();
        } else {
            queryProvinces();
            mRecyclerview.smoothScrollToPosition(0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DBManager.getInstance().closeDatabase();
    }

    private void showTips() {
        new AlertDialog.Builder(this).setTitle("多城市管理模式").setMessage("您现在是多城市管理模式,直接点击即可新增城市.如果暂时不需要添加,"
            + "在右上选项中关闭即可像往常一样操作.\n因为 api 次数限制的影响,多城市列表最多三个城市.(๑′ᴗ‵๑)"
        ).setPositiveButton("明白", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setNegativeButton("不再提示", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferenceUtil.getInstance().putBoolean("Tips", false);
            }
        }).show();
    }
}

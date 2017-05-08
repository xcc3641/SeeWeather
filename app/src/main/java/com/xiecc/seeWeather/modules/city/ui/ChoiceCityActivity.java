package com.xiecc.seeWeather.modules.city.ui;

import android.content.Context;
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

/**
 * Created by hugo on 2016/2/19 0019.
 */
public class ChoiceCityActivity extends ToolbarActivity {

    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;

    private ArrayList<String> dataList = new ArrayList<>();
    private Province selectedProvince;
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
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }

    private void initRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
        //mRecyclerView.setItemAnimator(new FadeInUpAnimator());
        mAdapter = new CityAdapter(this, dataList);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener((view, pos) -> {
            if (currentLevel == LEVEL_PROVINCE) {
                selectedProvince = provincesList.get(pos);
                mRecyclerView.smoothScrollToPosition(0);
                queryCities();
            } else if (currentLevel == LEVEL_CITY) {
                String city = Util.replaceCity(cityList.get(pos).CityName);
                if (isChecked) {
                    OrmLite.getInstance().save(new CityORM(city));
                    RxBus.getDefault().post(new MultiUpdate());
                } else {
                    SharedPreferenceUtil.getInstance().setCityName(city);
                    RxBus.getDefault().post(new ChangeCityEvent());
                }
                quit();
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

            .toList()
            .compose(RxUtils.rxSchedulerHelper())
            .compose(this.bindToLifecycle())
            .doOnTerminate(() -> mProgressBar.setVisibility(View.GONE))
            .doOnCompleted(() -> {
                currentLevel = LEVEL_PROVINCE;
                mAdapter.notifyDataSetChanged();
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
            .doOnCompleted(() -> {
                currentLevel = LEVEL_CITY;
                mAdapter.notifyDataSetChanged();
                mRecyclerView.smoothScrollToPosition(0);
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
        if (currentLevel == LEVEL_PROVINCE) {
            quit();
        } else {
            queryProvinces();
            mRecyclerView.smoothScrollToPosition(0);
        }
    }

    public static void launch(Context context) {
        context.startActivity(new Intent(context, ChoiceCityActivity.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DBManager.getInstance().closeDatabase();
    }

    private void showTips() {
        new AlertDialog.Builder(this)
            .setTitle("多城市管理模式")
            .setMessage("您现在是多城市管理模式,直接点击即可新增城市.如果暂时不需要添加,"
                + "在右上选项中关闭即可像往常一样操作.\n因为 api 次数限制的影响,多城市列表最多三个城市.(๑′ᴗ‵๑)")
            .setPositiveButton("明白", (dialog, which) -> dialog.dismiss())
            .setNegativeButton("不再提示", (dialog, which) -> SharedPreferenceUtil.getInstance().putBoolean("Tips", false))
            .show();
    }

    private void quit() {
        ChoiceCityActivity.this.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}

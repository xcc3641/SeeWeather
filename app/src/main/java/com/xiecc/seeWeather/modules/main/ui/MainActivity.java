package com.xiecc.seeWeather.modules.main.ui;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.xiecc.seeWeather.R;
import com.xiecc.seeWeather.base.BaseActivity;
import com.xiecc.seeWeather.common.C;
import com.xiecc.seeWeather.common.utils.CircularAnimUtil;
import com.xiecc.seeWeather.common.utils.DoubleClickExit;
import com.xiecc.seeWeather.common.utils.RxDrawer;
import com.xiecc.seeWeather.common.utils.SharedPreferenceUtil;
import com.xiecc.seeWeather.common.utils.ToastUtil;
import com.xiecc.seeWeather.modules.about.ui.AboutActivity;
import com.xiecc.seeWeather.modules.city.ui.ChoiceCityActivity;
import com.xiecc.seeWeather.modules.main.adapter.HomePagerAdapter;
import com.xiecc.seeWeather.modules.service.AutoUpdateService;
import com.xiecc.seeWeather.modules.setting.ui.SettingActivity;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.viewPager)
    ViewPager mViewPager;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.tabLayout)
    TabLayout mTabLayout;
    @BindView(R.id.fab)
    FloatingActionButton mFab;
    @BindView(R.id.nav_view)
    NavigationView mNavView;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    private MainFragment mMainFragment;
    private MultiCityFragment mMultiCityFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        initView();
        initDrawer();
        initIcon();
        startService(new Intent(this, AutoUpdateService.class));
    }

    public static void launch(Context context) {
        context.startActivity(new Intent(context, MainActivity.class));
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initIcon();
    }

    /**
     * 初始化基础View
     */
    private void initView() {
        setSupportActionBar(mToolbar);
        mFab.setOnClickListener(v -> showShareDialog());
        HomePagerAdapter mAdapter = new HomePagerAdapter(getSupportFragmentManager());
        mMainFragment = new MainFragment();
        mMultiCityFragment = new MultiCityFragment();
        mAdapter.addTab(mMainFragment, "主页面");
        mAdapter.addTab(mMultiCityFragment, "多城市");
        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager, false);
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mFab.post(() -> mFab.hide(new FloatingActionButton.OnVisibilityChangedListener() {
                    @Override
                    public void onHidden(FloatingActionButton fab) {
                        if (position == 1) {
                            mFab.setImageResource(R.drawable.ic_add_24dp);
                            mFab.setBackgroundTintList(
                                ColorStateList.valueOf(ContextCompat.getColor(MainActivity.this, R.color.colorPrimary)));
                            mFab.setOnClickListener(v -> {
                                Intent intent = new Intent(MainActivity.this, ChoiceCityActivity.class);
                                intent.putExtra(C.MULTI_CHECK, true);
                                CircularAnimUtil.startActivity(MainActivity.this, intent, mFab,
                                    R.color.colorPrimary);
                            });
                        } else {
                            mFab.setImageResource(R.drawable.ic_favorite);
                            mFab.setBackgroundTintList(
                                ColorStateList.valueOf(ContextCompat.getColor(MainActivity.this, R.color.colorAccent)));
                            mFab.setOnClickListener(v -> showShareDialog());
                        }
                        fab.show();
                    }
                }));
                if (!mFab.isShown()) {
                    mFab.show();
                }
            }
        });
    }

    /**
     * 初始化抽屉
     */
    private void initDrawer() {
        if (mNavView != null) {
            mNavView.setNavigationItemSelectedListener(this);
            mNavView.inflateHeaderView(R.layout.nav_header_main);
            ActionBarDrawerToggle toggle =
                new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open,
                    R.string.navigation_drawer_close);
            mDrawerLayout.addDrawerListener(toggle);
            toggle.syncState();
        }
    }

    /**
     * 初始化 Icons
     */
    private void initIcon() {
        if (SharedPreferenceUtil.getInstance().getIconType() == 0) {
            SharedPreferenceUtil.getInstance().putInt("未知", R.mipmap.none);
            SharedPreferenceUtil.getInstance().putInt("晴", R.mipmap.type_one_sunny);
            SharedPreferenceUtil.getInstance().putInt("阴", R.mipmap.type_one_cloudy);
            SharedPreferenceUtil.getInstance().putInt("多云", R.mipmap.type_one_cloudy);
            SharedPreferenceUtil.getInstance().putInt("少云", R.mipmap.type_one_cloudy);
            SharedPreferenceUtil.getInstance().putInt("晴间多云", R.mipmap.type_one_cloudytosunny);
            SharedPreferenceUtil.getInstance().putInt("小雨", R.mipmap.type_one_light_rain);
            SharedPreferenceUtil.getInstance().putInt("中雨", R.mipmap.type_one_light_rain);
            SharedPreferenceUtil.getInstance().putInt("大雨", R.mipmap.type_one_heavy_rain);
            SharedPreferenceUtil.getInstance().putInt("阵雨", R.mipmap.type_one_thunderstorm);
            SharedPreferenceUtil.getInstance().putInt("雷阵雨", R.mipmap.type_one_thunder_rain);
            SharedPreferenceUtil.getInstance().putInt("霾", R.mipmap.type_one_fog);
            SharedPreferenceUtil.getInstance().putInt("雾", R.mipmap.type_one_fog);
        } else {
            SharedPreferenceUtil.getInstance().putInt("未知", R.mipmap.none);
            SharedPreferenceUtil.getInstance().putInt("晴", R.mipmap.type_two_sunny);
            SharedPreferenceUtil.getInstance().putInt("阴", R.mipmap.type_two_cloudy);
            SharedPreferenceUtil.getInstance().putInt("多云", R.mipmap.type_two_cloudy);
            SharedPreferenceUtil.getInstance().putInt("少云", R.mipmap.type_two_cloudy);
            SharedPreferenceUtil.getInstance().putInt("晴间多云", R.mipmap.type_two_cloudytosunny);
            SharedPreferenceUtil.getInstance().putInt("小雨", R.mipmap.type_two_light_rain);
            SharedPreferenceUtil.getInstance().putInt("中雨", R.mipmap.type_two_rain);
            SharedPreferenceUtil.getInstance().putInt("大雨", R.mipmap.type_two_rain);
            SharedPreferenceUtil.getInstance().putInt("阵雨", R.mipmap.type_two_rain);
            SharedPreferenceUtil.getInstance().putInt("雷阵雨", R.mipmap.type_two_thunderstorm);
            SharedPreferenceUtil.getInstance().putInt("霾", R.mipmap.type_two_haze);
            SharedPreferenceUtil.getInstance().putInt("雾", R.mipmap.type_two_fog);
            SharedPreferenceUtil.getInstance().putInt("雨夹雪", R.mipmap.type_two_snowrain);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        RxDrawer.close(mDrawerLayout)
            .doOnNext(o -> {
                switch (item.getItemId()) {
                    case R.id.nav_set:
                        SettingActivity.launch(MainActivity.this);
                        break;
                    case R.id.nav_about:
                        AboutActivity.launch(MainActivity.this);
                        break;
                    case R.id.nav_city:
                        ChoiceCityActivity.launch(MainActivity.this);
                        break;
                    case R.id.nav_multi_cities:
                        mViewPager.setCurrentItem(1);
                        break;
                }
            })
            .subscribe();
        return false;
    }

    private void showShareDialog() {
        // wait to do
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (!DoubleClickExit.check()) {
                ToastUtil.showShort(getString(R.string.double_exit));
            } else {
                finish();
            }
        }
    }
}

package com.xiecc.seeWeather.modules.main.ui;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.xiecc.seeWeather.R;
import com.xiecc.seeWeather.base.BaseActivity;
import com.xiecc.seeWeather.base.C;
import com.xiecc.seeWeather.common.PLog;
import com.xiecc.seeWeather.common.utils.CheckVersion;
import com.xiecc.seeWeather.common.utils.CircularAnimUtil;
import com.xiecc.seeWeather.common.utils.DoubleClickExit;
import com.xiecc.seeWeather.common.utils.RxDrawer;
import com.xiecc.seeWeather.common.utils.RxUtils;
import com.xiecc.seeWeather.common.utils.ToastUtil;
import com.xiecc.seeWeather.modules.about.ui.AboutActivity;
import com.xiecc.seeWeather.modules.city.ui.ChoiceCityActivity;
import com.xiecc.seeWeather.modules.main.adapter.HomePagerAdapter;
import com.xiecc.seeWeather.modules.main.domain.Weather;
import com.xiecc.seeWeather.modules.service.AutoUpdateService;
import com.xiecc.seeWeather.modules.setting.ui.SettingActivity;
import rx.android.schedulers.AndroidSchedulers;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    @Bind(R.id.viewPager)
    ViewPager viewPager;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.tabLayout)
    TabLayout tabLayout;
    @Bind(R.id.fab)
    FloatingActionButton fab;
    @Bind(R.id.nav_view)
    NavigationView navView;
    @Bind(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        PLog.i("onCreate");
        initView();
        initDrawer();
        initIcon();
        startService(new Intent(this, AutoUpdateService.class));
        CheckVersion.checkVersion(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        PLog.i("onStart");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        PLog.i("onRestart");
        //为了实现 Intent 重启使图标生效
        initIcon();
    }

    @Override
    protected void onResume() {
        super.onResume();
        PLog.i("onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        PLog.i("onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        PLog.i("onStop");
    }

    /**
     * 初始化基础View
     */
    private void initView() {
        setSupportActionBar(toolbar);
        HomePagerAdapter mHomePagerAdapter = new HomePagerAdapter(getSupportFragmentManager());
        mHomePagerAdapter.addTab(new MainFragment(), "主页面");
        mHomePagerAdapter.addTab(new MultiCityFragment(), "多城市");
        viewPager.setAdapter(mHomePagerAdapter);
        tabLayout.setupWithViewPager(viewPager, false);
        //viewPager.setOffscreenPageLimit(2);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 1) {
                    fab.setImageResource(R.drawable.ic_add_24dp);
                    fab.setBackgroundTintList(
                        ColorStateList.valueOf(ContextCompat.getColor(MainActivity.this, R.color.colorPrimary)));
                    fab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent(MainActivity.this, ChoiceCityActivity.class);
                            intent.putExtra(C.MULTI_CHECK, true);
                            CircularAnimUtil.startActivity(MainActivity.this, intent, fab,
                                R.color.colorPrimary);
                        }
                    });
                } else {
                    fab.setImageResource(R.drawable.ic_favorite_24dp);
                    fab.setBackgroundTintList(
                        ColorStateList.valueOf(ContextCompat.getColor(MainActivity.this, R.color.colorAccent)));
                    fab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showFabDialog();
                        }
                    });
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        // Glide 加载本地 GIF 图的方法
        //GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(mErroImageView);
        //Glide.with(this).load(R.raw.loading).diskCacheStrategy(DiskCacheStrategy.ALL).into(imageViewTarget);

        //fab
        //fab.setOnClickListener(v -> showFabDialog());
    }

    /**
     * 初始化抽屉
     */
    private void initDrawer() {
        //https://segmentfault.com/a/1190000004151222
        if (navView != null) {
            navView.setNavigationItemSelectedListener(this);
            //navigationView.setItemIconTintList(null);
            View headerLayout = navView.inflateHeaderView(R.layout.nav_header_main);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
            drawerLayout.addDrawerListener(toggle);
            toggle.syncState();
        }
    }

    /**
     * 初始化Icon
     */
    private void initIcon() {
        if (mSharedPreferenceUtil.getIconType() == 0) {
            mSharedPreferenceUtil.putInt("未知", R.mipmap.none);
            mSharedPreferenceUtil.putInt("晴", R.mipmap.type_one_sunny);
            mSharedPreferenceUtil.putInt("阴", R.mipmap.type_one_cloudy);
            mSharedPreferenceUtil.putInt("多云", R.mipmap.type_one_cloudy);
            mSharedPreferenceUtil.putInt("少云", R.mipmap.type_one_cloudy);
            mSharedPreferenceUtil.putInt("晴间多云", R.mipmap.type_one_cloudytosunny);
            mSharedPreferenceUtil.putInt("小雨", R.mipmap.type_one_light_rain);
            mSharedPreferenceUtil.putInt("中雨", R.mipmap.type_one_light_rain);
            mSharedPreferenceUtil.putInt("大雨", R.mipmap.type_one_heavy_rain);
            mSharedPreferenceUtil.putInt("阵雨", R.mipmap.type_one_thunderstorm);
            mSharedPreferenceUtil.putInt("雷阵雨", R.mipmap.type_one_thunder_rain);
            mSharedPreferenceUtil.putInt("霾", R.mipmap.type_one_fog);
            mSharedPreferenceUtil.putInt("雾", R.mipmap.type_one_fog);
        } else {
            mSharedPreferenceUtil.putInt("未知", R.mipmap.none);
            mSharedPreferenceUtil.putInt("晴", R.mipmap.type_two_sunny);
            mSharedPreferenceUtil.putInt("阴", R.mipmap.type_two_cloudy);
            mSharedPreferenceUtil.putInt("多云", R.mipmap.type_two_cloudy);
            mSharedPreferenceUtil.putInt("少云", R.mipmap.type_two_cloudy);
            mSharedPreferenceUtil.putInt("晴间多云", R.mipmap.type_two_cloudytosunny);
            mSharedPreferenceUtil.putInt("小雨", R.mipmap.type_two_light_rain);
            mSharedPreferenceUtil.putInt("中雨", R.mipmap.type_two_rain);
            mSharedPreferenceUtil.putInt("大雨", R.mipmap.type_two_rain);
            mSharedPreferenceUtil.putInt("阵雨", R.mipmap.type_two_rain);
            mSharedPreferenceUtil.putInt("雷阵雨", R.mipmap.type_two_thunderstorm);
            mSharedPreferenceUtil.putInt("霾", R.mipmap.type_two_haze);
            mSharedPreferenceUtil.putInt("雾", R.mipmap.type_two_fog);
            mSharedPreferenceUtil.putInt("雨夹雪", R.mipmap.type_two_snowrain);
        }
    }

    private void showFabDialog() {
        new AlertDialog.Builder(MainActivity.this).setTitle("点赞")
            .setMessage("去项目地址给作者个Star，鼓励下作者୧(๑•̀⌄•́๑)૭✧")
            .setPositiveButton("好叻", (dialog, which) -> {
                Uri uri = Uri.parse(getString(R.string.app_html));   //指定网址
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);           //指定Action
                intent.setData(uri);                            //设置Uri
                MainActivity.this.startActivity(intent);        //启动Activity
            })
            .show();
    }

    /**
     * Called when an item in the navigation menu is selected.
     *
     * @param item The selected item
     * @return true to display the item as the selected item
     */
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        RxDrawer.close(drawerLayout).compose(RxUtils.rxSchedulerHelper(AndroidSchedulers.mainThread())).subscribe(aVoid -> {
            switch (item.getItemId()) {
                case R.id.nav_set:
                    Intent intentSetting = new Intent(MainActivity.this, SettingActivity.class);
                    startActivity(intentSetting);
                    break;
                case R.id.nav_about:
                    startActivity(new Intent(MainActivity.this, AboutActivity.class));
                    break;
                case R.id.nav_city:
                    Intent intentCity = new Intent(MainActivity.this, ChoiceCityActivity.class);
                    startActivity(intentCity);
                    break;
                case R.id.nav_multi_cities:
                    viewPager.setCurrentItem(1);
                    break;
            }
        });
        return false;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (!DoubleClickExit.check()) {
                ToastUtil.showShort(getString(R.string.double_exit));
            } else {
                finish();
            }
        }
    }

    protected boolean mIsHidden = false;

    public void hideOrShowToolbar() {
        tabLayout.animate()
            .translationY(mIsHidden ? 0 : -tabLayout.getHeight())
            .setInterpolator(new DecelerateInterpolator(2))
            .start();
        mIsHidden = !mIsHidden;
    }

    private void normalStyleNotification(Weather weather) {
        Intent intent = new Intent(MainActivity.this, MainActivity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent =
            PendingIntent.getActivity(MainActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder builder = new Notification.Builder(MainActivity.this);
        Notification notification = builder.setContentIntent(pendingIntent)
            .setContentTitle(weather.basic.city)
            .setContentText(String.format("%s 当前温度: %s℃ ", weather.now.cond.txt, weather.now.tmp))
            // 这里部分 ROM 无法成功
            .setSmallIcon(mSharedPreferenceUtil.getInt(weather.now.cond.txt, R.mipmap.none))
            .build();
        notification.flags = mSharedPreferenceUtil.getNotificationModel();
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // tag和id都是可以拿来区分不同的通知的
        manager.notify(1, notification);
    }
}

package com.xiecc.seeWeather.modules.main.ui;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.xiecc.seeWeather.R;
import com.xiecc.seeWeather.base.BaseActivity;
import com.xiecc.seeWeather.common.CheckVersion;
import com.xiecc.seeWeather.common.ImageLoader;
import com.xiecc.seeWeather.common.PLog;
import com.xiecc.seeWeather.common.Util;
import com.xiecc.seeWeather.component.RetrofitSingleton;
import com.xiecc.seeWeather.modules.main.adapter.WeatherAdapter;
import com.xiecc.seeWeather.modules.main.domain.Weather;
import com.xiecc.seeWeather.modules.main.listener.HidingScrollListener;
import com.xiecc.seeWeather.modules.service.AutoUpdateService;
import com.xiecc.seeWeather.modules.city.ui.ChoiceCityActivity;
import com.xiecc.seeWeather.modules.about.ui.AboutActivity;
import com.xiecc.seeWeather.modules.setting.Setting;
import com.xiecc.seeWeather.modules.setting.ui.SettingActivity;
import java.util.Calendar;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener,
    AMapLocationListener {
    private final String TAG = MainActivity.class.getSimpleName();

    private CollapsingToolbarLayout collapsingToolbarLayout;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private FloatingActionButton fab;
    private SwipeRefreshLayout mRefreshLayout;
    private ImageView bannner;
    private ProgressBar mProgressBar;
    private ImageView mErroImageView;
    private RelativeLayout headerBackground;

    private RecyclerView mRecyclerView;
    private Weather mWeather = new Weather();
    private WeatherAdapter mAdapter;
    private Observer<Weather> observer;
    private long exitTime = 0; ////记录第一次点击的时间

    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    public AMapLocationClientOption mLocationOption = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PLog.i(TAG, "onCreate");
        //ButterKnife.bind(this);
        initView();
        initDrawer();
        initDataObserver();
        initIcon();
        startService(new Intent(this, AutoUpdateService.class));

        if (Util.isNetworkConnected(this)) {
            CheckVersion.checkVersion(this, fab);
            // https://github.com/tbruyelle/RxPermissions
            RxPermissions.getInstance(this).request(Manifest.permission.ACCESS_COARSE_LOCATION)
                .subscribe(granted -> {
                    if (granted) {
                        location();
                    } else {
                        fetchDataByNetWork(observer);
                    }
                });
        } else {
            fetchDataByCache(observer);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //为了实现 Intent 重启使图标生效
        initIcon();
        // 修改 adapter 的初始化
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
        super.onPause();
        PLog.i(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        PLog.i(TAG, "onStop");
    }

    /**
     * 初始化基础View
     */
    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        bannner = (ImageView) findViewById(R.id.banner);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.VISIBLE);
        }
        mErroImageView = (ImageView) findViewById(R.id.iv_erro);
        // Glide 加载本地 GIF 图的方法
        //GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(mErroImageView);
        //Glide.with(this).load(R.raw.loading).diskCacheStrategy(DiskCacheStrategy.ALL).into(imageViewTarget);

        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiprefresh);
        if (mRefreshLayout != null) {
            mRefreshLayout.setOnRefreshListener(() -> {
                mRefreshLayout.postDelayed(() -> fetchDataByNetWork(observer), 1000);
            });
        }

        //标题
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        if (collapsingToolbarLayout != null) {
            collapsingToolbarLayout.setTitle(" ");
        }

        //彩蛋-夜间模式
        Calendar calendar = Calendar.getInstance();

        //mSetting.putInt(Setting.HOUR, calendar.get(Calendar.HOUR_OF_DAY));
        mSetting.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
        setStatusBarColorForKitkat(R.color.colorSunrise);
        ImageLoader.loadAndDiskCache(this,R.mipmap.sunrise,bannner);
        if (mSetting.getCurrentHour() < 6 || mSetting.getCurrentHour() > 18) {
            ImageLoader.loadAndDiskCache(this,R.mipmap.sunset,bannner);
            collapsingToolbarLayout.setContentScrimColor(ContextCompat.getColor(this, R.color.colorSunset));
            setStatusBarColorForKitkat(R.color.colorSunset);
        }

        //fab
        fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(v -> showFabDialog());
            CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
            final int fabBottomMargin = lp.bottomMargin;

            //recclerview
            mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.addOnScrollListener(new HidingScrollListener() {
                @Override
                public void onHide() {
                    fab.animate()
                        .translationY(fab.getHeight() + fabBottomMargin)
                        .setInterpolator(new AccelerateInterpolator(2))
                        .start();
                }

                @Override
                public void onShow() {
                    fab.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
                }
            });
            mAdapter = new WeatherAdapter(MainActivity.this, mWeather);
            mRecyclerView.setAdapter(mAdapter);
        }
    }

    /**
     * 初始化抽屉
     */
    private void initDrawer() {
        //https://segmentfault.com/a/1190000004151222
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
            View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_main);
            headerBackground = (RelativeLayout) headerLayout.findViewById(R.id.header_background);
            if (mSetting.getInt(Setting.HOUR, 0) < 6 || mSetting.getInt(Setting.HOUR, 0) > 18) {
                //headerBackground.setBackground(this.getResources().getDrawable(R.mipmap.header_back_night)); 过时
                headerBackground.setBackground(ContextCompat.getDrawable(this, R.mipmap.header_back_night));
            }
            drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();
        }
    }

    /**
     * 初始化Icon
     */
    private void initIcon() {
        if (mSetting.getIconType() == 0) {
            mSetting.putInt("未知", R.mipmap.none);
            mSetting.putInt("晴", R.mipmap.type_one_sunny);
            mSetting.putInt("阴", R.mipmap.type_one_cloudy);
            mSetting.putInt("多云", R.mipmap.type_one_cloudy);
            mSetting.putInt("少云", R.mipmap.type_one_cloudy);
            mSetting.putInt("晴间多云", R.mipmap.type_one_cloudytosunny);
            mSetting.putInt("小雨", R.mipmap.type_one_light_rain);
            mSetting.putInt("中雨", R.mipmap.type_one_light_rain);
            mSetting.putInt("大雨", R.mipmap.type_one_heavy_rain);
            mSetting.putInt("阵雨", R.mipmap.type_one_thunderstorm);
            mSetting.putInt("雷阵雨", R.mipmap.type_one_thunder_rain);
            mSetting.putInt("霾", R.mipmap.type_one_fog);
            mSetting.putInt("雾", R.mipmap.type_one_fog);
        } else {
            mSetting.putInt("未知", R.mipmap.none);
            mSetting.putInt("晴", R.mipmap.type_two_sunny);
            mSetting.putInt("阴", R.mipmap.type_two_cloudy);
            mSetting.putInt("多云", R.mipmap.type_two_cloudy);
            mSetting.putInt("少云", R.mipmap.type_two_cloudy);
            mSetting.putInt("晴间多云", R.mipmap.type_two_cloudytosunny);
            mSetting.putInt("小雨", R.mipmap.type_two_light_rain);
            mSetting.putInt("中雨", R.mipmap.type_two_rain);
            mSetting.putInt("大雨", R.mipmap.type_two_rain);
            mSetting.putInt("阵雨", R.mipmap.type_two_rain);
            mSetting.putInt("雷阵雨", R.mipmap.type_two_thunderstorm);
            mSetting.putInt("霾", R.mipmap.type_two_haze);
            mSetting.putInt("雾", R.mipmap.type_two_fog);
            mSetting.putInt("雨夹雪", R.mipmap.type_two_snowrain);
        }
    }

    /**
     * 初始化 observer (观察者)
     * 拿到数据后的操作
     */
    private void initDataObserver() {
        observer = new Observer<Weather>() {
            @Override
            public void onCompleted() {
                mRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onError(Throwable e) {
                errorNetSnackbar(observer);
                mRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onNext(Weather weather) {
                mProgressBar.setVisibility(View.GONE);
                mErroImageView.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);

                aCache.put("WeatherData", weather,
                    (mSetting.getAutoUpdate() * Setting.ONE_HOUR));//默认一小时后缓存失效

                mWeather.status = weather.status;
                mWeather.aqi = weather.aqi;
                mWeather.basic = weather.basic;
                mWeather.suggestion = weather.suggestion;
                mWeather.now = weather.now;
                mWeather.dailyForecast = weather.dailyForecast;
                mWeather.hourlyForecast = weather.hourlyForecast;
                collapsingToolbarLayout.setTitle(mWeather.basic.city);
                //mAdapter = new WeatherAdapter(MainActivity.this, weather);
                //mRecyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
                normalStyleNotification(mWeather);
                showSnackbar(fab, "加载完毕，✺◟(∗❛ัᴗ❛ั∗)◞✺,");

                mRecyclerView.smoothScrollToPosition(2);
            }
        };
    }

    /**
     * 从本地获取
     */
    public void fetchDataByCache(final Observer<Weather> observer) {

        Weather weather = null;
        try {
            weather = (Weather) aCache.getAsObject("WeatherData");
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

        if (weather != null) {
            Observable.just(weather).distinct().subscribe(observer);
        } else {
            errorNetSnackbar(observer);
        }
    }

    private void errorNetSnackbar(final Observer<Weather> observer) {
        mProgressBar.setVisibility(View.GONE);
        mErroImageView.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
        Snackbar.make(fab, "网络不好,~( ´•︵•` )~", Snackbar.LENGTH_INDEFINITE).setAction("重试", v -> {
            fetchDataByNetWork(observer);
        }).show();
    }

    /**
     * 从网络获取
     */
    public void fetchDataByNetWork(Observer<Weather> observer) {
        String cityName = mSetting.getString(Setting.CITY_NAME, "北京");
        if (cityName != null) {
            cityName = cityName.replace("市", "")
                .replace("省", "")
                .replace("自治区", "")
                .replace("特别行政区", "")
                .replace("地区", "")
                .replace("盟", "");
        }
        RetrofitSingleton.getApiService(this)
            .mWeatherAPI(cityName, Setting.KEY)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .filter(weatherAPI -> weatherAPI.mHeWeatherDataService30s.get(0).status.equals("ok"))
            .map(weatherAPI -> weatherAPI.mHeWeatherDataService30s.get(0))
            .subscribe(observer);
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
        switch (item.getItemId()) {
            case R.id.nav_set:
                Intent intentSetting = new Intent(MainActivity.this, SettingActivity.class);
                intentSetting.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentSetting);
                break;
            case R.id.nav_about:
                startActivity(new Intent(this, AboutActivity.class));
                break;
            case R.id.nav_city:
                startActivityForResult(new Intent(this, ChoiceCityActivity.class), 1);
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                showSnackbar(fab, "再按一次退出程序");
                exitTime = System.currentTimeMillis();
            } else {
                finish();
            }


        }
    }

    /**
     * 高德定位
     */
    private void location() {
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(this);
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(false);
        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔 单位毫秒
        mLocationOption.setInterval(mSetting.getAutoUpdate() * Setting.ONE_HOUR);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLocationClient != null) {
            mLocationClient.unRegisterLocationListener(this);
        }
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息
                aMapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                mSetting.setCityName(aMapLocation.getCity());
                //PLog.i(TAG, aMapLocation.getProvince() + aMapLocation.getCity() + aMapLocation.getDistrict() +
                //    aMapLocation.getAdCode() + aMapLocation.getCityCode());
            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                //PLog.e("AmapError", "location Error, ErrCode:" + aMapLocation.getErrorCode() + ", errInfo:" +
                //    aMapLocation.getErrorInfo());
                //Snackbar.make(fab, "定位失败,请尝试手动更新", Snackbar.LENGTH_LONG).show();
                showSnackbar(fab, "定位失败,加载默认城市", true);
            }
            fetchDataByNetWork(observer);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //requestCode标示请求的标示   resultCode表示有数据
        if (requestCode == 1 && resultCode == 2) {
            mRefreshLayout.setRefreshing(true);
            mSetting.putString(Setting.CITY_NAME, data.getStringExtra(Setting.CITY_NAME));
            fetchDataByNetWork(observer);
        }
    }

    private void normalStyleNotification(Weather weather) {
        Intent intent = new Intent(MainActivity.this, MainActivity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent =
            PendingIntent.getActivity(MainActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder builder = new Notification.Builder(MainActivity.this);
        Notification notification = builder.setContentIntent(pendingIntent)
            .setContentTitle(weather.basic.city)
            .setContentText(weather.now.cond.txt + " 当前温度: " + weather.now.tmp + "℃")
            // 这里部分 ROM 无法成功
            .setSmallIcon(mSetting.getInt(weather.now.cond.txt, R.mipmap.none))
            .build();
        notification.flags = mSetting.getNotificationModel();
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // tag和id都是可以拿来区分不同的通知的
        manager.notify(1, notification);
    }
}

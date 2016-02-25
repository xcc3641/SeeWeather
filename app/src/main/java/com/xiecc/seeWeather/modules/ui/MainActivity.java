package com.xiecc.seeWeather.modules.ui;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import android.widget.RelativeLayout;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import com.xiecc.seeWeather.R;
import com.xiecc.seeWeather.base.BaseActivity;
import com.xiecc.seeWeather.common.CheckVersion;
import com.xiecc.seeWeather.common.PLog;
import com.xiecc.seeWeather.common.Util;
import com.xiecc.seeWeather.component.RetrofitSingleton;
import com.xiecc.seeWeather.modules.adatper.WeatherAdapter;
import com.xiecc.seeWeather.modules.domain.Setting;
import com.xiecc.seeWeather.modules.domain.Weather;
import com.xiecc.seeWeather.modules.domain.WeatherAPI;
import com.xiecc.seeWeather.modules.listener.HidingScrollListener;
import com.xiecc.seeWeather.modules.ui.about.AboutActivity;
import com.xiecc.seeWeather.modules.ui.setting.SettingActivity;
import java.util.Calendar;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener,
        SwipeRefreshLayout.OnRefreshListener,
        AMapLocationListener {
    private final String TAG = MainActivity.class.getSimpleName();

    private CollapsingToolbarLayout collapsingToolbarLayout;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private FloatingActionButton fab;
    private SwipeRefreshLayout mRefreshLayout;
    private ImageView bannner;

    private RelativeLayout headerBackground;

    private RecyclerView mRecyclerView;
    //private Weather mWeatherData = null;
    private WeatherAdapter mAdapter;
    private Observer<Weather> observer;

    private long exitTime = 0; ////记录第一次点击的时间

    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    public AMapLocationClientOption mLocationOption = null;

    private boolean isLoaction = false;


    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initDrawer();
        initIcon();
        new RefreshHandler().sendEmptyMessage(1);
        fetchData();
        if (Util.isNetworkConnected(this)) {
            CheckVersion.checkVersion(this, fab);
            location();
            if (isLoaction) {
                onRefresh();
            }
        }
    }


    /**
     * 初始化基础View
     */
    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        bannner = (ImageView) findViewById(R.id.bannner);

        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiprefresh);
        mRefreshLayout.setOnRefreshListener(this);

        //标题
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        collapsingToolbarLayout.setTitle(" ");

        //彩蛋-夜间模式
        Calendar calendar = Calendar.getInstance();
        mSetting.putInt(Setting.HOUR, calendar.get(Calendar.HOUR_OF_DAY));
        if (mSetting.getInt(Setting.HOUR, 0) < 6 || mSetting.getInt(Setting.HOUR, 0) > 18) {
            Glide.with(this).load(R.mipmap.sunset).diskCacheStrategy(DiskCacheStrategy.ALL).into(bannner);
            collapsingToolbarLayout.setContentScrimColor(ContextCompat.getColor(this, R.color.colorSunset));
            //setStatusBarColor(R.color.colorSunset);
        }

        //fab
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                showFabDialog();
            }
        });
        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
        final int fabBottomMargin = lp.bottomMargin;
        //recclerview
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addOnScrollListener(new HidingScrollListener() {
            @Override public void onHide() {
                fab.animate()
                   .translationY(fab.getHeight() + fabBottomMargin)
                   .setInterpolator(new AccelerateInterpolator(2))
                   .start();
            }


            @Override public void onShow() {
                fab.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
            }
        });
    }


    /**
     * 初始化抽屉
     */
    private void initDrawer() {
        //https://segmentfault.com/a/1190000004151222
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
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
        drawer.setDrawerListener(toggle);
        toggle.syncState();
    }


    /**
     * 初始化Icon
     */
    private void initIcon() {
        if (mSetting.getInt(Setting.CHANGE_ICONS, 0) == 0) {
            mSetting.putInt("未知", R.mipmap.none);
            mSetting.putInt("晴", R.mipmap.type_one_sunny);
            mSetting.putInt("阴", R.mipmap.type_one_cloudy);
            mSetting.putInt("多云", R.mipmap.type_one_cloudy);
            mSetting.putInt("少云", R.mipmap.type_one_cloudy);
            mSetting.putInt("晴间多云", R.mipmap.type_one_cloudytosunny);
            mSetting.putInt("小雨", R.mipmap.type_one_light_rain);
            mSetting.putInt("中雨", R.mipmap.type_one_middle_rain);
            mSetting.putInt("大雨", R.mipmap.type_one_heavy_rain);
            mSetting.putInt("阵雨", R.mipmap.type_one_thunderstorm);
            mSetting.putInt("雷阵雨", R.mipmap.type_one_thunderstorm);
            mSetting.putInt("霾", R.mipmap.type_one_fog);
            mSetting.putInt("雾", R.mipmap.type_one_fog);
        }
        else {
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
     * <p/>
     * 首先从本地缓存获取数据
     * if 有
     * 更新UI
     * else
     * 直接进行网络请求，更新UI并保存在本地
     */
    private void fetchData() {
        observer = new Observer<Weather>() {
            @Override public void onCompleted() {
                new RefreshHandler().sendEmptyMessage(2);
            }


            @Override public void onError(Throwable e) {
                RetrofitSingleton.disposeFailureInfo(e, MainActivity.this, fab);
                new RefreshHandler().sendEmptyMessage(2);
            }


            @Override public void onNext(Weather weather) {
                new RefreshHandler().sendEmptyMessage(2);
                collapsingToolbarLayout.setTitle(weather.basic.city);
                mAdapter = new WeatherAdapter(MainActivity.this, weather);
                mRecyclerView.setAdapter(mAdapter);
            }
        };

        fetchDataByCache(observer);
    }


    /**
     * 从本地获取
     */
    private void fetchDataByCache(Observer<Weather> observer) {

        Weather weather = null;
        try {
            weather = (Weather) aCache.getAsObject("WeatherData");
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

        if (weather != null) {
            Observable.just(weather).distinct().subscribe(observer);
        }
        else {
            fetchDataByNetWork(observer);
        }
    }


    /**
     * 从网络获取
     */
    private void fetchDataByNetWork(Observer<Weather> observer) {
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
                         .filter(new Func1<WeatherAPI, Boolean>() {
                             @Override public Boolean call(WeatherAPI weatherAPI) {
                                 return weatherAPI.mHeWeatherDataService30s.get(0).status.equals("ok");
                             }
                         })
                         .map(new Func1<WeatherAPI, Weather>() {
                             @Override public Weather call(WeatherAPI weatherAPI) {
                                 return weatherAPI.mHeWeatherDataService30s.get(0);
                             }
                         })
                         .doOnNext(new Action1<Weather>() {
                             @Override public void call(Weather weather) {
                                 aCache.put("WeatherData", weather,
                                         (mSetting.getInt(Setting.AUTO_UPDATE, 0) + 1) * Setting.ONE_HOUR);//默认一小时后缓存失效
                             }
                         })
                         .subscribe(observer);
    }


    private void showFabDialog() {
        new AlertDialog.Builder(MainActivity.this).setTitle("点赞")
                                                  .setMessage("去项目地址给作者个Star，鼓励下作者୧(๑•̀⌄•́๑)૭✧")
                                                  .setPositiveButton("好叻", new DialogInterface.OnClickListener() {
                                                      @Override public void onClick(DialogInterface dialog, int which) {
                                                          Uri uri = Uri.parse(getString(R.string.app_html));   //指定网址
                                                          Intent intent = new Intent();
                                                          intent.setAction(Intent.ACTION_VIEW);           //指定Action
                                                          intent.setData(uri);                            //设置Uri
                                                          MainActivity.this.startActivity(intent);        //启动Activity
                                                      }
                                                  })
                                                  .show();
    }


    /**
     * Called when an item in the navigation menu is selected.
     *
     * @param item The selected item
     * @return true to display the item as the selected item
     */
    @Override public boolean onNavigationItemSelected(MenuItem item) {
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
                //Intent intentCity = new Intent(MainActivity.this, ChoiceCityActivity.class);
                //intentCity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //startActivityForResult(intentCity, 1);
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }


    @Override public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            //super.onBackPressed();
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Snackbar.make(fab, "再按一次退出程序", Snackbar.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            }
            else {
                finish();
            }
        }
    }


    @Override public void onRefresh() {
        fetchDataByNetWork(observer);
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
        mLocationOption.setInterval((mSetting.getInt(Setting.AUTO_UPDATE, 0) * Setting.ONE_HOUR * 1000));
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }


    @Override public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息
                aMapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                //aMapLocation.getLatitude();//获取纬度
                //aMapLocation.getLongitude();//获取经度
                //aMapLocation.getAccuracy();//获取精度信息
                //SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                //Date date = new Date(aMapLocation.getTime());
                //df.format(date);//定位时间
                //aMapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                //aMapLocation.getCountry();//国家信息
                //aMapLocation.getProvince();//省信息
                //aMapLocation.getCity();//城市信息
                //aMapLocation.getDistrict();//城区信息
                //aMapLocation.getStreet();//街道信息
                //aMapLocation.getStreetNum();//街道门牌号信息
                //aMapLocation.getCityCode();//城市编码
                //aMapLocation.getAdCode();//地区编码
                mSetting.putString(Setting.CITY_NAME, aMapLocation.getCity());
                isLoaction = true;
                PLog.i(TAG, aMapLocation.getProvince() + aMapLocation.getCity() + aMapLocation.getDistrict() +
                        aMapLocation.getAdCode() + aMapLocation.getCityCode());
            }
            else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                PLog.e("AmapError", "location Error, ErrCode:" + aMapLocation.getErrorCode() + ", errInfo:" +
                        aMapLocation.getErrorInfo());
            }
        }
    }


    @SuppressLint("HandlerLeak") class RefreshHandler extends Handler {
        @Override public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    mRefreshLayout.setRefreshing(true);
                    break;
                case 2:
                    if (mRefreshLayout.isRefreshing()) {
                        mRefreshLayout.setRefreshing(false);
                        Snackbar.make(fab, "加载完毕，✺◟(∗❛ัᴗ❛ั∗)◞✺", Snackbar.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    }


    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //requestCode标示请求的标示   resultCode表示有数据
        if (requestCode == 1 && resultCode == 2) {
            new RefreshHandler().sendEmptyMessage(1);
            mSetting.putString(Setting.CITY_NAME, data.getStringExtra(Setting.CITY_NAME));
            fetchDataByNetWork(observer);
        }
    }

    //private void showNotificatison(Weather weather) {
    //    Intent intent = new Intent(this, MainActivity.class);
    //    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
    //    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
    //
    //    Notification.Builder builder = new Notification.Builder(this);
    //    builder.setSmallIcon(mSetting.getInt(weather.now.cond.txt, R.mipmap.none));
    //    builder.setWhen(System.currentTimeMillis());
    //    builder.setContentTitle(weather.basic.city);
    //    builder.setContentText(weather.now.cond.txt + weather.now.fl + "℃");
    //    builder.setContentIntent(pendingIntent);
    //}
}

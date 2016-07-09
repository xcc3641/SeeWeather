package com.xiecc.seeWeather.modules.main.ui;

import android.Manifest;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.xiecc.seeWeather.R;
import com.xiecc.seeWeather.base.BaseFragment;
import com.xiecc.seeWeather.base.RxBus;
import com.xiecc.seeWeather.common.PLog;
import com.xiecc.seeWeather.common.utils.ToastUtil;
import com.xiecc.seeWeather.common.utils.Util;
import com.xiecc.seeWeather.component.RetrofitSingleton;
import com.xiecc.seeWeather.modules.main.adapter.WeatherAdapter;
import com.xiecc.seeWeather.modules.main.domain.ChangeCityEvent;
import com.xiecc.seeWeather.modules.main.domain.Weather;
import com.xiecc.seeWeather.modules.setting.Setting;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by HugoXie on 16/7/9.
 *
 * Email: Hugo3641@gamil.com
 * GitHub: https://github.com/xcc3641
 * Info:
 */
public class MainFragment extends BaseFragment implements
    AMapLocationListener {

    @Bind(R.id.recyclerview)
    RecyclerView mRecyclerview;
    @Bind(R.id.swiprefresh)
    SwipeRefreshLayout mSwiprefresh;
    @Bind(R.id.progressBar)
    ProgressBar mProgressBar;
    @Bind(R.id.iv_erro)
    ImageView mIvErro;

    private static Weather mWeather = new Weather();
    private WeatherAdapter mAdapter;
    private Observer<Weather> observer;
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    public AMapLocationClientOption mLocationOption = null;

    private MainActivity mActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (MainActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.content_main, null, false);
        ButterKnife.bind(this, mView);
        initView();
        isCreateView = true;
        return mView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RxBus.getDefault().toObserverable(ChangeCityEvent.class).observeOn(AndroidSchedulers.mainThread()).subscribe(
            changeCityEvent -> {
                mSwiprefresh.setRefreshing(true);
                load();
            }, throwable -> {
                PLog.e(throwable.getMessage())
                ;
            });
    }

    private void initView() {
        if (mSwiprefresh != null) {
            mSwiprefresh.setOnRefreshListener(
                () -> mSwiprefresh.postDelayed(this::load, 1000));
        }

        mRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerview.setHasFixedSize(true);
        mAdapter = new WeatherAdapter(getActivity(), mWeather);
        mRecyclerview.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(mWeather1 -> {
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View dialogLayout = inflater.inflate(R.layout.weather_dialog, (ViewGroup) getActivity().findViewById(
                R.id.weather_dialog_root));
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setView(dialogLayout);
            final AlertDialog alertDialog = builder.create();

            RelativeLayout root = (RelativeLayout) dialogLayout.findViewById(R.id.weather_dialog_root);
            switch (Util.getWeatherType(Integer.parseInt(mWeather1.now.cond.code))) {
                case "晴":
                    root.setBackgroundResource(R.mipmap.dialog_bg_sunny);
                    break;
                case "阴":
                    root.setBackgroundResource(R.mipmap.dialog_bg_cloudy);
                    break;
                case "雨":
                    root.setBackgroundResource(R.mipmap.dialog_bg_rainy);
                    break;
                default:
                    break;
            }

            TextView city = (TextView) dialogLayout.findViewById(R.id.dialog_city);
            city.setText(mWeather1.basic.city);
            TextView temp = (TextView) dialogLayout.findViewById(R.id.dialog_temp);
            temp.setText(String.format("%s°", mWeather1.now.tmp));
            ImageView icon = (ImageView) dialogLayout.findViewById(R.id.dialog_icon);

            Glide.with(this)
                .load(mActivity.mSetting.getInt(mWeather1.now.cond.txt, R.mipmap.none))
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        icon.setImageBitmap(resource);
                        icon.setColorFilter(Color.WHITE);
                    }
                });

            alertDialog.show();
        });
    }

    /**
     * 初始化 observer (观察者)
     * 拿到数据后的操作
     */
    private void initDataObserver() {

        observer = new Observer<Weather>() {

            @Override
            public void onCompleted() {
                ToastUtil.showShort(getString(R.string.complete));
            }

            @Override
            public void onError(Throwable e) {
                PLog.e(e.toString());
                Snackbar.make(getView(), "网络不好,~( ´•︵•` )~", Snackbar.LENGTH_INDEFINITE).setAction("重试", v -> {
                    load();
                }).show();
            }

            @Override
            public void onNext(Weather weather) {
                //if (mSetting.getAutoUpdate() == 0) {
                //    aCache.put(C.WEATHER_CACHE, weather);
                //} else {
                //    aCache.put(C.WEATHER_CACHE, weather,
                //        (mSetting.getAutoUpdate() * Setting.ONE_HOUR));//默认3小时后缓存失效
                //}
                mWeather.status = weather.status;
                mWeather.aqi = weather.aqi;
                mWeather.basic = weather.basic;
                mWeather.suggestion = weather.suggestion;
                mWeather.now = weather.now;
                mWeather.dailyForecast = weather.dailyForecast;
                mWeather.hourlyForecast = weather.hourlyForecast;
                mActivity.getToolbar().setTitle(weather.basic.city);
                mAdapter.notifyDataSetChanged();
                //normalStyleNotification(mWeather);
            }
        };
    }

    /**
     * 优化网络+缓存逻辑
     * 优先网络
     */
    private void load() {
        fetchDataByNetWork()
            .doOnError(throwable -> {
                mIvErro.setVisibility(View.VISIBLE);
                mRecyclerview.setVisibility(View.GONE);
            })
            .doOnNext(weather -> {
                mIvErro.setVisibility(View.GONE);
                mRecyclerview.setVisibility(View.VISIBLE);
            })
            .doOnTerminate(() -> {
                mSwiprefresh.setRefreshing(false);
                mProgressBar.setVisibility(View.GONE);
            }).subscribe(observer);
    }

    /**
     * 从本地获取
     */
    //private Observable<Weather> fetchDataByCache() {
    //    return Observable.defer(() -> {
    //        Weather weather = (Weather) aCache.getAsObject(C.WEATHER_CACHE);
    //        return Observable.just(weather);
    //    });
    //}

    /**
     * 从网络获取
     */
    private Observable<Weather> fetchDataByNetWork() {
        String cityName = Util.replaceCity(mActivity.mSetting.getCityName());
        return RetrofitSingleton.getInstance()
            .fetchWeather(cityName)
            .onErrorReturn(throwable -> {
                PLog.e(throwable.getMessage());
                ToastUtil.showLong(throwable.getMessage());
                return null;
            });
    }

    /**
     * 高德定位
     */
    private void location() {
        //初始化定位
        mLocationClient = new AMapLocationClient(mActivity.getApplicationContext());
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
        int tempTime = mActivity.mSetting.getAutoUpdate();
        if (tempTime == 0) {
            tempTime = 100;
        }
        mLocationOption.setInterval(tempTime * Setting.ONE_HOUR);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息
                aMapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                mActivity.mSetting.setCityName(aMapLocation.getCity());
            } else {
                ToastUtil.showShort(getString(R.string.errorLocation));
            }
            load();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLocationClient = null;
        mLocationOption =null;
    }

    /**
     * 加载数据操作,在视图创建之前初始化
     */
    @Override
    protected void lazyLoad() {
        // https://github.com/tbruyelle/RxPermissions
        RxPermissions.getInstance(getActivity()).request(Manifest.permission.ACCESS_COARSE_LOCATION)
            .subscribe(granted -> {
                if (granted) {
                    location();
                } else {
                    load();
                }
            });

        initDataObserver();
    }
}

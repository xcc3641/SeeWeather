package com.xiecc.seeWeather.modules.main.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.xiecc.seeWeather.R;
import com.xiecc.seeWeather.base.BaseFragment;
import com.xiecc.seeWeather.common.OrmLite;
import com.xiecc.seeWeather.common.PLog;
import com.xiecc.seeWeather.common.utils.Util;
import com.xiecc.seeWeather.component.RetrofitSingleton;
import com.xiecc.seeWeather.modules.main.adapter.MultiCityAdapter;
import com.xiecc.seeWeather.modules.main.domain.CityORM;
import com.xiecc.seeWeather.modules.main.domain.Weather;
import java.util.ArrayList;
import java.util.List;
import rx.Observable;
import rx.Observer;
import rx.functions.Func1;

/**
 * Created by HugoXie on 16/7/9.
 *
 * Email: Hugo3641@gamil.com
 * GitHub: https://github.com/xcc3641
 * Info:
 */
public class MultiCityFragment extends BaseFragment {

    @Bind(R.id.recyclerview)
    RecyclerView mRecyclerview;
    @Bind(R.id.swiprefresh)
    SwipeRefreshLayout mSwiprefresh;

    private MultiCityAdapter mAdatper;
    private List<Weather> weatherArrayList;

    private View view;

    private MainActivity activity;
    /**
     * 加载数据操作,在视图创建之前初始化
     */
    @Override
    protected void lazyLoad() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_multicity, container, false);
            ButterKnife.bind(this, view);
            initView();
        }
        Observable.from(OrmLite.getInstance().query(CityORM.class))
            .flatMap(new Func1<CityORM, Observable<Weather>>() {
                @Override
                public Observable<Weather> call(CityORM cityORM) {
                    String city = Util.replaceCity(cityORM.getName());
                    return RetrofitSingleton.getInstance().fetchWeather(city);
                }
            }).subscribe(new Observer<Weather>() {
            @Override
            public void onCompleted() {
                mAdatper.notifyDataSetChanged();
                PLog.w(weatherArrayList.size()+"");
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Weather weather) {
                weatherArrayList.add(weather);
                PLog.d(weather.basic.city);

            }
        });
        return view;
    }



    private void initView() {
        weatherArrayList =  new ArrayList<>();
        mAdatper = new MultiCityAdapter(getActivity(), weatherArrayList);
        mRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerview.setAdapter(mAdatper);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}

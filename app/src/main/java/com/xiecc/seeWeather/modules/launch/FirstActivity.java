package com.xiecc.seeWeather.modules.launch;

import android.app.Activity;
import android.os.Bundle;

import com.xiecc.seeWeather.modules.main.ui.MainActivity;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by hugo on 2015/10/25 0025.
 * 闪屏页
 */
public class FirstActivity extends Activity {
//    private static final String TAG = FirstActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //activity切换的淡入淡出效果
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        super.onCreate(savedInstanceState);
        //RxJava 中文(译文)GitBook:https://mcxiaoke.gitbooks.io/rxdocs/content/
        Observable.timer(1, TimeUnit.SECONDS)//延时一秒后进入MainActivity
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    MainActivity.launch(this);
                    finish();
                });

        //随便创建个Observable然后delay也行
        //通过empty()创建的Observable需要在onComplete()中实现业务逻辑
//        Observable.just(1)
//                .delay(1, TimeUnit.SECONDS)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(integer -> {
//                    MainActivity.launch(this);
//                    finish();
//                });

//        new Handler(msg -> {
//            MainActivity.launch(this);
//            finish();
//            return true;
//        }).sendEmptyMessageDelayed(0, 1000);
    }
}
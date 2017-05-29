package com.xiecc.seeWeather.modules.launch;

import android.app.Activity;
import android.os.Bundle;
import com.xiecc.seeWeather.modules.main.ui.MainActivity;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import java.util.concurrent.TimeUnit;

public class FirstActivity extends Activity {
    private static final String TAG = FirstActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        super.onCreate(savedInstanceState);
        Observable.timer(1, TimeUnit.SECONDS) // thanks to XieEDeHeiShou
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(aLong -> {
                MainActivity.launch(this);
                finish();
            });
    }
}
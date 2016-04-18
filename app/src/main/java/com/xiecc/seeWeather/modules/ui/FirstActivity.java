package com.xiecc.seeWeather.modules.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * Created by hugo on 2015/10/25 0025.
 * 闪屏页
 */
public class FirstActivity extends Activity {
    private static final String TAG = "FirstActivity:";


    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new switchHandler().sendEmptyMessageDelayed(1, 1000);
    }


    @SuppressLint("HandlerLeak") class switchHandler extends Handler {
        @Override public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Intent i = new Intent(FirstActivity.this, MainActivity.class);
            FirstActivity.this.startActivity(i);
            //activity切换的淡入淡出效果
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            FirstActivity.this.finish();
        }
    }
}
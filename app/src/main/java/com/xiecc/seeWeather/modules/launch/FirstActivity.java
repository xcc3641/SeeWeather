package com.xiecc.seeWeather.modules.launch;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.xiecc.seeWeather.modules.main.ui.MainActivity;
import java.lang.ref.WeakReference;

/**
 * Created by hugo on 2015/10/25 0025.
 * 闪屏页
 */
public class FirstActivity extends Activity {
    private static final String TAG = FirstActivity.class.getSimpleName();
    private SwitchHandler mHandler = new SwitchHandler(Looper.getMainLooper(), this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler.sendEmptyMessageDelayed(1, 1000);
    }

    class SwitchHandler extends Handler {
        private WeakReference<FirstActivity> mWeakReference;

        public SwitchHandler(Looper mLooper, FirstActivity activity) {
            super(mLooper);
            mWeakReference = new WeakReference<FirstActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            Intent i = new Intent(FirstActivity.this, MainActivity.class);
            FirstActivity.this.startActivity(i);
            //activity切换的淡入淡出效果
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            FirstActivity.this.finish();
        }
    }
}
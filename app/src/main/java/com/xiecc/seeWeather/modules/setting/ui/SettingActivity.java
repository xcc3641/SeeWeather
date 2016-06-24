package com.xiecc.seeWeather.modules.setting.ui;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import com.xiecc.seeWeather.R;
import com.xiecc.seeWeather.base.BaseActivity;

/**
 * Created by hugo on 2016/2/19 0019.
 */
public class SettingActivity extends BaseActivity {

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //beforeInitView();
        setContentView(R.layout.activity_setting);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("设置");
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_keyboard_arrow_left_32dpdp));
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        setStatusBarColor(R.color.colorPrimary);
        if (mSetting.getCurrentHour()< 6 || mSetting.getCurrentHour() > 18) {
            toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.colorSunset));
            setStatusBarColor(R.color.colorSunset);
        }
        getFragmentManager().beginTransaction().replace(R.id.framelayout, new SettingFragment()).commit();
    }

    //@TargetApi(Build.VERSION_CODES.LOLLIPOP)
    //public void beforeInitView(){
    //    getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
    //    getWindow().setEnterTransition(new Fade());
    //    getWindow().setExitTransition(new Fade());
    //}
}

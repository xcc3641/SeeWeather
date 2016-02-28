package com.xiecc.seeWeather.modules.ui.setting;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import com.xiecc.seeWeather.R;
import com.xiecc.seeWeather.base.BaseActivity;
import com.xiecc.seeWeather.modules.domain.Setting;

/**
 * Created by hugo on 2016/2/19 0019.
 */
public class SettingActivity extends BaseActivity {

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("设置");
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_keyboard_arrow_left_32dpdp));
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                finish();
            }
        });

        setStatusBarColor(R.color.colorPrimary);
        if (mSetting.getInt(Setting.HOUR, 0) < 6 || mSetting.getInt(Setting.HOUR, 0) > 18) {
            toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.colorSunset));
            setStatusBarColor(R.color.colorSunset);
        }
        getFragmentManager().beginTransaction().replace(R.id.framelayout, new SettingFragment()).commit();
    }
}

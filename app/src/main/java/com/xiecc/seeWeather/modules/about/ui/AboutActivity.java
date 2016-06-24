package com.xiecc.seeWeather.modules.about.ui;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import com.xiecc.seeWeather.R;
import com.xiecc.seeWeather.base.ToolbarActivity;

/**
 * Created by hugo on 2016/2/20 0020.
 */
public class AboutActivity extends ToolbarActivity {

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_about;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("关于");
        setStatusBarColor(R.color.colorPrimary);
        if (mSetting.getCurrentHour() < 6 || mSetting.getCurrentHour() > 18) {
            mToolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.colorSunset));
            setStatusBarColor(R.color.colorSunset);
        }
        getFragmentManager().beginTransaction().replace(R.id.framelayout, new AboutFragment()).commit();
    }

    @Override
    public boolean canBack() {
        return true;
    }
}

package com.xiecc.seeWeather.modules.about.ui;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import com.xiecc.seeWeather.R;
import com.xiecc.seeWeather.base.ToolbarActivity;

/**
 * Created by hugo on 2016/2/20 0020.
 */
public class AboutActivity extends ToolbarActivity {
    FragmentTransaction mFragmentTransaction;
    AboutFragment mAboutFragment = new AboutFragment();

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
        mFragmentTransaction = getFragmentManager().beginTransaction();
        mFragmentTransaction.replace(R.id.framelayout, mAboutFragment);
        mFragmentTransaction.commit();
    }

    @Override
    public boolean canBack() {
        return true;
    }

    @Override
    public void onBackPressed() {
        Fragment webview = getFragmentManager().findFragmentByTag("webview");
        if (webview != null) {
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.framelayout, mAboutFragment);
            fragmentTransaction.commit();
        }else {
            finish();
        }
    }
}

package com.xiecc.seeWeather.modules.setting.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.xiecc.seeWeather.R;
import com.xiecc.seeWeather.base.ToolbarActivity;

public class SettingActivity extends ToolbarActivity {

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_setting;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getToolbar().setTitle("设置");
        getFragmentManager().beginTransaction().replace(R.id.frameLayout, new SettingFragment()).commit();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void beforeSetContent() {
        super.beforeSetContent();
    }

    @Override
    public boolean canBack() {
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public static void launch(Context context) {
        context.startActivity(new Intent(context, SettingActivity.class));
    }
}

package com.xiecc.seeWeather.modules.about.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.xiecc.seeWeather.R;
import com.xiecc.seeWeather.base.BaseActivity;
import com.xiecc.seeWeather.common.utils.StatusBarUtil;
import com.xiecc.seeWeather.common.utils.Util;
import com.xiecc.seeWeather.common.utils.VersionUtil;

public class AboutActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout mToolbarLayout;
    @BindView(R.id.tv_version)
    TextView mTvVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        StatusBarUtil.setImmersiveStatusBar(this);
        StatusBarUtil.setImmersiveStatusBarToolbar(mToolbar,this);
        initView();
    }

    private void initView() {
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);
        mTvVersion.setText(String.format("当前版本: %s (Build %s)", VersionUtil.getVersion(this), VersionUtil.getVersionCode(this)));
        mToolbarLayout.setTitleEnabled(false);
        mToolbar.setTitle(getString(R.string.app_name));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @OnClick({ R.id.bt_code, R.id.bt_blog, R.id.bt_pay, R.id.bt_share, R.id.bt_bug, R.id.bt_update })
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_code:
                goToHtml(getString(R.string.app_html));
                break;
            case R.id.bt_blog:
                goToHtml("http://imxie.cc");
                break;
            case R.id.bt_pay:
                Util.copyToClipboard(getString(R.string.alipay), this);
                break;
            case R.id.bt_share:
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_txt));
                startActivity(Intent.createChooser(sharingIntent, getString(R.string.share_app)));
                break;
            case R.id.bt_bug:
                goToHtml(getString(R.string.bugTableUrl));
                break;
            case R.id.bt_update:
                VersionUtil.checkVersion(this, true);
                break;
        }
    }

    private void goToHtml(String url) {
        Uri uri = Uri.parse(url);   //指定网址
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);           //指定Action
        intent.setData(uri);                            //设置Uri
        startActivity(intent);        //启动Activity
    }

    public static void launch(Context context) {
        context.startActivity(new Intent(context, AboutActivity.class));
    }
}

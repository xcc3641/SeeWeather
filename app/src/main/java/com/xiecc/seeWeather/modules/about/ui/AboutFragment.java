package com.xiecc.seeWeather.modules.about.ui;

import android.app.FragmentTransaction;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AlertDialog;
import com.xiecc.seeWeather.R;
import com.xiecc.seeWeather.common.utils.CheckVersion;
import com.xiecc.seeWeather.common.utils.ToastUtil;
import com.xiecc.seeWeather.common.utils.Util;

/**
 * Created by hugo on 2016/2/20 0020.
 * 
 */
// TODO: 16/7/26 需要一个可爱迷人的关于页面 
public class AboutFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {

    //private AboutActivity mActivity;

    private final String INTRODUCTION = "introduction";
    private final String CURRENT_VERSION = "current_version";
    private final String SHARE = "share";
    private final String STAR = "Star";
    private final String ENCOURAGE = "encourage";
    private final String BLOG = "blog";
    private final String GITHUB = "github";
    private final String EMAIL = "email";
    private final String CHECK = "check_version";

    private Preference mIntroduction;
    private Preference mVersion;
    private Preference mCheckVersion;
    private Preference mShare;
    private Preference mStar;
    private Preference mEncounrage;
    private Preference mBolg;
    private Preference mGithub;
    private Preference mEmail;

    //@Override public void onAttach(Context context) {
    //    super.onAttach(context);
    //    mActivity = (AboutActivity) context;
    //}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.about);

        mIntroduction = findPreference(INTRODUCTION);
        mVersion = findPreference(CURRENT_VERSION);
        mCheckVersion = findPreference(CHECK);
        mShare = findPreference(SHARE);
        mStar = findPreference(STAR);
        mEncounrage = findPreference(ENCOURAGE);
        mBolg = findPreference(BLOG);
        mGithub = findPreference(GITHUB);
        mEmail = findPreference(EMAIL);

        mIntroduction.setOnPreferenceClickListener(this);
        mVersion.setOnPreferenceClickListener(this);
        mCheckVersion.setOnPreferenceClickListener(this);
        mShare.setOnPreferenceClickListener(this);
        mStar.setOnPreferenceClickListener(this);
        mEncounrage.setOnPreferenceClickListener(this);
        mBolg.setOnPreferenceClickListener(this);
        mGithub.setOnPreferenceClickListener(this);
        mEmail.setOnPreferenceClickListener(this);

        mVersion.setSummary(getActivity().getString(R.string.version_name) + Util.getVersion(getActivity()));
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (mVersion == preference) {
            new AlertDialog.Builder(getActivity()).setTitle("就看天气的完成离不开开源项目的支持，向以下致谢：")
                .setMessage("Google Support Design,Gson,Rxjava,RxAndroid,Retrofit," +
                    "Glide,systembartint")
                .setPositiveButton("关闭", null)
                .show();
        } else if (mShare == preference) {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");
            sharingIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_txt));
            startActivity(Intent.createChooser(sharingIntent, getString(R.string.share_app)));
        } else if (mStar == preference) {
            new AlertDialog.Builder(getActivity()).setTitle("点赞")
                .setMessage("去项目地址给作者个Star，鼓励下作者୧(๑•̀⌄•́๑)૭✧")
                .setNegativeButton("复制", (dialog, which) -> {
                    copyToClipboard(getActivity().getResources()
                        .getString(
                            R.string.app_html));
                })
                .setPositiveButton("打开", (dialog, which) -> {
                    Uri uri = Uri.parse(getString(R.string.app_html));   //指定网址
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);           //指定Action
                    intent.setData(uri);                            //设置Uri
                    getActivity().startActivity(intent);        //启动Activity
                })
                .show();
        } else if (mIntroduction == preference) {
            //Uri uri = Uri.parse(getString(R.string.readme));   //指定网址
            //Intent intent = new Intent();
            //intent.setAction(Intent.ACTION_VIEW);           //指定Action
            //intent.setData(uri);                            //设置Uri
            //getActivity().startActivity(intent);        //启动Activity
            goToWebFragment(getString(R.string.readme));
        } else if (mEncounrage == preference) {
            new AlertDialog.Builder(getActivity()).setTitle("请作者喝杯咖啡？(〃ω〃)")
                .setMessage("点击按钮后，作者支付宝账号将会复制到剪切板，" + "你就可以使用支付宝转账给作者啦( •̀ .̫ •́ )✧")
                .setPositiveButton("好叻", (dialog, which) -> {
                    copyToClipboard(getActivity().getResources()
                        .getString(
                            R.string.alipay));
                    try {
                        Intent intent = getActivity().getPackageManager().getLaunchIntentForPackage("com.alipay.android.app");
                        startActivity(intent);
                    } catch (Exception e) {
                        ToastUtil.showShort("未找到应用");
                    }
                })
                .show();
        } else if (mBolg == preference) {
            goToWebFragment(mBolg.getSummary().toString());
        } else if (mGithub == preference) {
            goToWebFragment(mGithub.getSummary().toString());
        } else if (mEmail == preference) {
            copyToClipboard(mEmail.getSummary().toString());
        } else if (mCheckVersion == preference) {
            CheckVersion.checkVersion(getActivity(), true);
        }
        return false;
    }

    //复制黏贴板
    private void copyToClipboard(String info) {
        ClipboardManager manager = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("msg", info);
        manager.setPrimaryClip(clipData);
        ToastUtil.showShort("已经复制到剪切板啦( •̀ .̫ •́ )✧");
    }

    private void goToWebFragment(String url) {
        WebviewFragment webviewFragment = new WebviewFragment();
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        webviewFragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getActivity().getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.framelayout, webviewFragment, "webview");
        fragmentTransaction.commit();
    }
}

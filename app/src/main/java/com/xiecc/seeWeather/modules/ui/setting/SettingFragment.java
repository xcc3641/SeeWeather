package com.xiecc.seeWeather.modules.ui.setting;

import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import com.xiecc.seeWeather.R;
import com.xiecc.seeWeather.base.BaseApplication;
import com.xiecc.seeWeather.common.ACache;
import com.xiecc.seeWeather.common.FileSizeUtil;
import com.xiecc.seeWeather.modules.domain.Setting;

/**
 * Created by hugo on 2016/2/19 0019.
 */
public class SettingFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {
    private static String TAG = SettingFragment.class.getSimpleName();
    //private SettingActivity mActivity;
    private Setting mSetting;
    private Preference mChangeIcons;
    private Preference mChangeUpdate;
    private Preference mClearCache;

    private ACache mACache;

    //public SettingFragment() {}
    //@Override public void onAttach(Context context) {
    //    super.onAttach(context);
    //    mActivity = (SettingActivity) context;
    //}


    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.setting);
        mSetting = Setting.getInstance();
        mACache = ACache.get(getActivity());

        mChangeIcons = findPreference(Setting.CHANGE_ICONS);
        mChangeUpdate = findPreference(Setting.AUTO_UPDATE);
        mClearCache = findPreference(Setting.CLEAR_CACHE);

        mChangeIcons.setSummary(getResources().getStringArray(R.array.icons)[mSetting.getInt(Setting.CHANGE_ICONS, 0)]);
        mChangeUpdate.setSummary(
                getResources().getStringArray(R.array.cache_time)[mSetting.getInt(Setting.HOUR_SELECT, 0)]);
        mClearCache.setSummary(FileSizeUtil.getAutoFileOrFilesSize(BaseApplication.cacheDir + "/Data"));

        mChangeIcons.setOnPreferenceClickListener(this);
        mChangeUpdate.setOnPreferenceClickListener(this);
        mClearCache.setOnPreferenceClickListener(this);
    }


    @Override public boolean onPreferenceClick(Preference preference) {
        if (mChangeIcons == preference) {
            showIconDialog();
        }
        else if (mClearCache == preference) {
            mACache.clear();
            mClearCache.setSummary(FileSizeUtil.getAutoFileOrFilesSize(BaseApplication.cacheDir + "/Data"));
            Snackbar.make(getView(), "缓存已清除", Snackbar.LENGTH_SHORT).show();
        }
        else if (mChangeUpdate == preference) {
            showUpdateDialog();
        }

        return false;
    }


    private void showIconDialog() {
        new AlertDialog.Builder(getActivity()).setTitle("更换图标")
                                              .setSingleChoiceItems(getResources().getStringArray(R.array.icons),
                                                      mSetting.getInt(Setting.CHANGE_ICONS, 0),
                                                      new DialogInterface.OnClickListener() {
                                                          @Override
                                                          public void onClick(DialogInterface dialog, int which) {
                                                              if (which != mSetting.getInt(Setting.CHANGE_ICONS, 0)) {
                                                                  mSetting.putInt(Setting.CHANGE_ICONS, which);
                                                              }
                                                              dialog.dismiss();
                                                              mChangeIcons.setSummary(getResources().getStringArray(
                                                                      R.array.icons)[mSetting.getInt(
                                                                      Setting.CHANGE_ICONS, 0)]);
                                                              Snackbar.make(getView(), "切换成功,重启应用生效",
                                                                      Snackbar.LENGTH_SHORT).show();
                                                          }
                                                      })
                                              .show();
    }


    private void showUpdateDialog() {
        new AlertDialog.Builder(getActivity()).setTitle("更换频率")
                                              .setSingleChoiceItems(getResources().getStringArray(R.array.cache_time),
                                                      mSetting.getInt(Setting.HOUR_SELECT, 0),
                                                      new DialogInterface.OnClickListener() {
                                                          @Override
                                                          public void onClick(DialogInterface dialog, int which) {
                                                              switch (which) {
                                                                  case 0:
                                                                      mSetting.putInt(Setting.AUTO_UPDATE, 0);
                                                                      mSetting.putInt(Setting.HOUR_SELECT, which);
                                                                      break;
                                                                  case 1:
                                                                      mSetting.putInt(Setting.AUTO_UPDATE, 1);
                                                                      mSetting.putInt(Setting.HOUR_SELECT, which);
                                                                      break;
                                                                  case 2:
                                                                      mSetting.putInt(Setting.AUTO_UPDATE, 3);
                                                                      mSetting.putInt(Setting.HOUR_SELECT, which);
                                                                      break;
                                                                  case 3:
                                                                      mSetting.putInt(Setting.AUTO_UPDATE, 6);
                                                                      mSetting.putInt(Setting.HOUR_SELECT, which);
                                                                      break;
                                                              }
                                                              mChangeUpdate.setSummary(getResources().getStringArray(
                                                                      R.array.cache_time)[which]);
                                                              dialog.dismiss();
                                                              Snackbar.make(getView(), "设置成功", Snackbar.LENGTH_SHORT)
                                                                      .show();
                                                          }
                                                      })
                                              .show();
    }
}

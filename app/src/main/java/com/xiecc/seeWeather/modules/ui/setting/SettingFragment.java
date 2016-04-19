package com.xiecc.seeWeather.modules.ui.setting;

import android.app.Notification;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.xiecc.seeWeather.R;
import com.xiecc.seeWeather.base.BaseApplication;
import com.xiecc.seeWeather.common.ACache;
import com.xiecc.seeWeather.common.FileSizeUtil;
import com.xiecc.seeWeather.common.PLog;
import com.xiecc.seeWeather.modules.service.AutoUpdateService;

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
    private SwitchPreference mNotificationType;

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

        mNotificationType = (SwitchPreference) findPreference(Setting.NOTIFICATION_MODEL);
        mNotificationType.setChecked(true);

        mChangeIcons.setSummary(getResources().getStringArray(R.array.icons)[mSetting.getInt(Setting.CHANGE_ICONS, 0)]);

        //mChangeUpdate.setSummary(
        //        getResources().getStringArray(R.array.cache_time)[mSetting.getInt(Setting.HOUR_SELECT, 0)]);
        mChangeUpdate.setSummary(mSetting.getAutoUpdate()==0?"禁止刷新":"每"+mSetting.getAutoUpdate()+"小时更新");
        mClearCache.setSummary(FileSizeUtil.getAutoFileOrFilesSize(BaseApplication.cacheDir + "/Data"));

        mChangeIcons.setOnPreferenceClickListener(this);
        mChangeUpdate.setOnPreferenceClickListener(this);
        mClearCache.setOnPreferenceClickListener(this);
        mNotificationType.setOnPreferenceClickListener(this);
    }


    @Override public boolean onPreferenceClick(Preference preference) {
        if (mChangeIcons == preference) {
            showIconDialog();
        }
        else if (mClearCache == preference) {
            mACache.clear();
            Glide.get(getActivity().getApplicationContext()).clearMemory();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Glide.get(getActivity().getApplicationContext()).clearDiskCache();
                }
            });
            mClearCache.setSummary(FileSizeUtil.getAutoFileOrFilesSize(BaseApplication.cacheDir + "/Data"));
            Snackbar.make(getView(), "缓存已清除", Snackbar.LENGTH_SHORT).show();
        }
        else if (mChangeUpdate == preference) {
            showUpdateDialog();
        }else if (mNotificationType ==preference){
                //if (mNotificationType.isChecked()){
                //    mNotificationType.setChecked(false);
                //}else {
                //    mNotificationType.setChecked(true);
                //}
            mNotificationType.setChecked(mNotificationType.isChecked());
            mSetting.setNotificationModel(mNotificationType.isChecked()? Notification.FLAG_ONGOING_EVENT:Notification.FLAG_AUTO_CANCEL);
            PLog.i(TAG,mSetting.getAutoUpdate()+"");
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
                                                                  mSetting.setIconType(which);
                                                              }
                                                              dialog.dismiss();
                                                              mChangeIcons.setSummary(getResources().getStringArray(
                                                                      R.array.icons)[mSetting.getIconType()]);
                                                              Snackbar.make(getView(), "切换成功,重启应用生效",
                                                                      Snackbar.LENGTH_SHORT).show();
                                                          }
                                                      })
                                              .show();


    }


    private void showUpdateDialog() {
        //将 SeekBar 放入 Dialog 的方案 http://stackoverflow.com/questions/7184104/how-do-i-put-a-seek-bar-in-an-alert-dialog
        LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogLayout = inflater.inflate(R.layout.update_dialog, (ViewGroup) getActivity().findViewById(
            R.id.dialog_root));
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
            .setView(dialogLayout);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        final SeekBar mSeekBar = (SeekBar) dialogLayout.findViewById(R.id.time_seekbar);
        final TextView   tvShowHour = (TextView) dialogLayout.findViewById(R.id.tv_showhour);
        TextView tvDone = (TextView) dialogLayout.findViewById(R.id.done);

        mSeekBar.setMax(24);
        mSeekBar.setProgress(mSetting.getAutoUpdate());
        tvShowHour.setText("每"+mSeekBar.getProgress()+"小时");
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    tvShowHour.setText("每"+progress+"小时");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        tvDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSetting.setAutoUpdate(mSeekBar.getProgress());
                mChangeUpdate.setSummary(mSetting.getAutoUpdate()==0?"禁止刷新":"每"+mSetting.getAutoUpdate()+"小时更新");
                //需要再调用一次才能生效设置 不会重复的执行onCreate()， 而是会调用onStart()和onStartCommand()。
                getActivity().startService(new Intent(getActivity(), AutoUpdateService.class));
                alertDialog.dismiss();
            }
        });
    }
}

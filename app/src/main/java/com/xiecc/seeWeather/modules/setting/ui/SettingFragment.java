package com.xiecc.seeWeather.modules.setting.ui;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import com.hugo.watcher.Watcher;
import com.xiecc.seeWeather.R;
import com.xiecc.seeWeather.base.BaseApplication;
import com.xiecc.seeWeather.common.C;
import com.xiecc.seeWeather.common.utils.FileSizeUtil;
import com.xiecc.seeWeather.common.utils.FileUtil;
import com.xiecc.seeWeather.common.utils.RxUtil;
import com.xiecc.seeWeather.common.utils.SharedPreferenceUtil;
import com.xiecc.seeWeather.component.ImageLoader;
import com.xiecc.seeWeather.component.RxBus;
import com.xiecc.seeWeather.modules.main.domain.ChangeCityEvent;
import com.xiecc.seeWeather.modules.main.ui.MainActivity;
import com.xiecc.seeWeather.modules.service.AutoUpdateService;
import io.reactivex.Observable;
import java.io.File;
import java.util.Locale;

public class SettingFragment extends PreferenceFragment
    implements Preference.OnPreferenceClickListener,
    Preference.OnPreferenceChangeListener {
    private static String TAG = SettingFragment.class.getSimpleName();
    private SharedPreferenceUtil mSharedPreferenceUtil;
    private Preference mChangeIcons;
    private Preference mChangeUpdate;
    private Preference mClearCache;
    private CheckBoxPreference mNotificationType;
    private CheckBoxPreference mAnimationOnOff;
    private CheckBoxPreference mWatcherSwitch;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.setting);
        mSharedPreferenceUtil = SharedPreferenceUtil.getInstance();

        mChangeIcons = findPreference(SharedPreferenceUtil.CHANGE_ICONS);
        mChangeUpdate = findPreference(SharedPreferenceUtil.AUTO_UPDATE);
        mClearCache = findPreference(SharedPreferenceUtil.CLEAR_CACHE);

        mAnimationOnOff = (CheckBoxPreference) findPreference(SharedPreferenceUtil.ANIM_START);
        mNotificationType = (CheckBoxPreference) findPreference(SharedPreferenceUtil.NOTIFICATION_MODEL);
        mWatcherSwitch = (CheckBoxPreference) findPreference(SharedPreferenceUtil.WATCHER);

        mNotificationType.setChecked(
            SharedPreferenceUtil.getInstance().getNotificationModel() == Notification.FLAG_ONGOING_EVENT);
        mAnimationOnOff.setChecked(SharedPreferenceUtil.getInstance().getMainAnim());
        mWatcherSwitch.setChecked(SharedPreferenceUtil.getInstance().getWatcherSwitch());
        if (Build.VERSION.SDK_INT >= 23 && !Settings.canDrawOverlays(getContext())) {
            mWatcherSwitch.setEnabled(false);
        }
        mChangeIcons.setSummary(getResources().getStringArray(R.array.icons)[mSharedPreferenceUtil.getIconType()]);

        mChangeUpdate.setSummary(
            mSharedPreferenceUtil.getAutoUpdate() == 0 ? "禁止刷新" : "每" + mSharedPreferenceUtil.getAutoUpdate() + "小时更新");
        mClearCache.setSummary(FileSizeUtil.getAutoFileOrFilesSize(C.NET_CACHE));

        mChangeIcons.setOnPreferenceClickListener(this);
        mChangeUpdate.setOnPreferenceClickListener(this);
        mClearCache.setOnPreferenceClickListener(this);
        mNotificationType.setOnPreferenceChangeListener(this);
        mAnimationOnOff.setOnPreferenceChangeListener(this);
        mWatcherSwitch.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (mChangeIcons == preference) {
            showIconDialog();
        } else if (mClearCache == preference) {
            ImageLoader.clear(getActivity());
            Observable.just(FileUtil.delete(new File(C.NET_CACHE)))
                .filter(aBoolean -> aBoolean)
                .compose(RxUtil.io())
                .doOnNext(aBoolean -> {
                    mClearCache.setSummary(FileSizeUtil.getAutoFileOrFilesSize(C.NET_CACHE));
                    Snackbar.make(getView(), "缓存已清除", Snackbar.LENGTH_SHORT).show();
                })
                .subscribe();
        } else if (mChangeUpdate == preference) {
            showUpdateDialog();
        } else if (mWatcherSwitch == preference) {
            if (mWatcherSwitch.isChecked()) {
                Watcher.getInstance().start(BaseApplication.getAppContext());
            }
        }
        return true;
    }

    private void showIconDialog() {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogLayout = inflater.inflate(R.layout.icon_dialog, (ViewGroup) getActivity().findViewById(R.id.dialog_root));
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setView(dialogLayout);
        final AlertDialog alertDialog = builder.create();

        LinearLayout layoutTypeOne = (LinearLayout) dialogLayout.findViewById(R.id.layout_one);
        layoutTypeOne.setClickable(true);
        RadioButton radioTypeOne = (RadioButton) dialogLayout.findViewById(R.id.radio_one);
        LinearLayout layoutTypeTwo = (LinearLayout) dialogLayout.findViewById(R.id.layout_two);
        layoutTypeTwo.setClickable(true);
        RadioButton radioTypeTwo = (RadioButton) dialogLayout.findViewById(R.id.radio_two);
        TextView done = (TextView) dialogLayout.findViewById(R.id.done);

        radioTypeOne.setClickable(false);
        radioTypeTwo.setClickable(false);

        alertDialog.show();

        switch (mSharedPreferenceUtil.getIconType()) {
            case 0:
                radioTypeOne.setChecked(true);
                radioTypeTwo.setChecked(false);
                break;
            case 1:
                radioTypeOne.setChecked(false);
                radioTypeTwo.setChecked(true);
                break;
        }

        layoutTypeOne.setOnClickListener(v -> {
            radioTypeOne.setChecked(true);
            radioTypeTwo.setChecked(false);
        });

        layoutTypeTwo.setOnClickListener(v -> {
            radioTypeOne.setChecked(false);
            radioTypeTwo.setChecked(true);
        });

        done.setOnClickListener(v -> {
            mSharedPreferenceUtil.setIconType(radioTypeOne.isChecked() ? 0 : 1);
            String[] iconsText = getResources().getStringArray(R.array.icons);
            mChangeIcons.setSummary(radioTypeOne.isChecked() ? iconsText[0] :
                iconsText[1]);
            alertDialog.dismiss();
            Snackbar.make(getView(), "切换成功,重启应用生效",
                Snackbar.LENGTH_INDEFINITE).setAction("重启",
                v1 -> {
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    getActivity().startActivity(intent);
                    getActivity().finish();
                    RxBus.getDefault().post(new ChangeCityEvent());
                }).show();
        });
    }

    private void showUpdateDialog() {
        //将 SeekBar 放入 Dialog 的方案 http://stackoverflow.com/questions/7184104/how-do-i-put-a-seek-bar-in-an-alert-dialog
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogLayout = inflater.inflate(R.layout.update_dialog, (ViewGroup) getActivity().findViewById(
            R.id.dialog_root));
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
            .setView(dialogLayout);
        final AlertDialog alertDialog = builder.create();

        final SeekBar mSeekBar = (SeekBar) dialogLayout.findViewById(R.id.time_seekbar);
        final TextView tvShowHour = (TextView) dialogLayout.findViewById(R.id.tv_showhour);
        TextView tvDone = (TextView) dialogLayout.findViewById(R.id.done);

        mSeekBar.setMax(24);
        mSeekBar.setProgress(mSharedPreferenceUtil.getAutoUpdate());
        tvShowHour.setText(String.format("每%s小时", mSeekBar.getProgress()));
        alertDialog.show();

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvShowHour.setText(String.format("每%s小时", mSeekBar.getProgress()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        tvDone.setOnClickListener(v -> {
            mSharedPreferenceUtil.setAutoUpdate(mSeekBar.getProgress());
            mChangeUpdate.setSummary(
                mSharedPreferenceUtil.getAutoUpdate() == 0 ? "禁止刷新"
                    : String.format(Locale.CHINA, "每%d小时更新", mSharedPreferenceUtil.getAutoUpdate()));
            getActivity().startService(new Intent(getActivity(), AutoUpdateService.class));
            alertDialog.dismiss();
        });
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mAnimationOnOff) {
            SharedPreferenceUtil.getInstance().setMainAnim((Boolean) newValue);
        } else if (mNotificationType == preference) {
            SharedPreferenceUtil.getInstance().setNotificationModel(
                (boolean) newValue ? Notification.FLAG_ONGOING_EVENT : Notification.FLAG_AUTO_CANCEL);
        }

        return true;
    }
}

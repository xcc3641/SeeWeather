package com.xiecc.seeWeather.modules.main.ui;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.xiecc.seeWeather.R;
import com.xiecc.seeWeather.common.utils.WeChatShareUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by HugoXie on 2017/5/21.
 * <p>
 * Email: Hugo3641@gmail.com
 * GitHub: https://github.com/xcc3641
 * Info:
 */

class SharePresenter {

    @BindView(R.id.lay_friends)
    LinearLayout mLayFriends;
    @BindView(R.id.lay_time_line)
    LinearLayout mLayTimeLine;
    private View mRootView;

    @SuppressLint("InflateParams")
    SharePresenter(Context context, String content, ShareFailedListener listener) {
        mRootView = LayoutInflater.from(context).inflate(R.layout.dialog_share, null, false);
        ButterKnife.bind(this, mRootView);
        initListener(content,  listener);
    }

    private void initListener(String content, ShareFailedListener listener) {
        mLayFriends.setOnClickListener(click -> {
            try {
                WeChatShareUtil.toFriends(mLayFriends.getContext(), content);
            } catch (ActivityNotFoundException e) {
                listener.onActivityNotFound(e);
            }
        });
        mLayTimeLine.setOnClickListener(click -> {
            try {
                WeChatShareUtil.toTimeLine(mLayFriends.getContext(), content);
            } catch (ActivityNotFoundException e) {
                listener.onActivityNotFound(e);
            }
        });
    }

    View getRootView() {
        return mRootView;
    }

    /**
     * 分享失败监听器
     */
    interface ShareFailedListener{
        void onActivityNotFound(Throwable throwable);
    }
}

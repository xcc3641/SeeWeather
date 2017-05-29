package com.xiecc.seeWeather.modules.main.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.xiecc.seeWeather.R;
import com.xiecc.seeWeather.common.utils.WeChatShareUtil;

/**
 * Created by HugoXie on 2017/5/21.
 *
 * Email: Hugo3641@gmail.com
 * GitHub: https://github.com/xcc3641
 * Info:
 */
@Deprecated
public class SharePresenter {

    private View mRootView;

    @BindView(R.id.lay_friends)
    LinearLayout mLayFriends;

    @BindView(R.id.lay_time_line)
    LinearLayout mLayTimeLine;

    public SharePresenter(Context context, String content) {
        mRootView = LayoutInflater.from(context).inflate(R.layout.dialog_share, null, false);
        ButterKnife.bind(this, mRootView);
        initListener(content);
    }

    private void initListener(String content) {
        mLayFriends.setOnClickListener(click -> WeChatShareUtil.toFriends(mLayFriends.getContext(), content));
        mLayTimeLine.setOnClickListener(click -> WeChatShareUtil.toTimeLine(mLayTimeLine.getContext(), content));
    }

    public View getRootView() {
        return mRootView;
    }
}

package com.xiecc.seeWeather.modules.main.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.xiecc.seeWeather.R;

/**
 * Created by HugoXie on 16/7/9.
 *
 * Email: Hugo3641@gamil.com
 * GitHub: https://github.com/xcc3641
 * Info:
 */
public class MultiCityAdapter extends RecyclerView.Adapter<MultiCityAdapter.MultiCityViewHolder> {
    private Context mContext;

    public MultiCityAdapter(Context context) {
        mContext = context;
    }

    @Override
    public MultiCityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MultiCityViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_multi_city, parent, false));
    }

    @Override
    public void onBindViewHolder(MultiCityViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 3;
    }

    class MultiCityViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.dialog_city)
        TextView mDialogCity;
        @Bind(R.id.dialog_icon)
        ImageView mDialogIcon;
        @Bind(R.id.dialog_temp)
        TextView mDialogTemp;
        @Bind(R.id.weather_dialog_root)
        RelativeLayout mWeatherDialogRoot;
        @Bind(R.id.cardView)
        CardView mCardView;

        public MultiCityViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void inVoke(){


        }
    }
}

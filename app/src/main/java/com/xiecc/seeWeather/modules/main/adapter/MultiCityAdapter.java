package com.xiecc.seeWeather.modules.main.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.xiecc.seeWeather.R;
import com.xiecc.seeWeather.base.BaseViewHolder;
import com.xiecc.seeWeather.component.PLog;
import com.xiecc.seeWeather.common.utils.SharedPreferenceUtil;
import com.xiecc.seeWeather.common.utils.Util;
import com.xiecc.seeWeather.modules.main.domain.Weather;
import java.util.List;

public class MultiCityAdapter extends RecyclerView.Adapter<MultiCityAdapter.MultiCityViewHolder> {
    private Context mContext;
    private List<Weather> mWeatherList;
    private onMultiCityLongClick onMultiCityLongClick = null;

    public void setOnMultiCityLongClick(onMultiCityLongClick onMultiCityLongClick) {
        this.onMultiCityLongClick = onMultiCityLongClick;
    }

    public MultiCityAdapter(List<Weather> weatherList) {
        mWeatherList = weatherList;
    }

    @Override
    public MultiCityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        return new MultiCityViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_multi_city, parent, false));
    }

    @Override
    public void onBindViewHolder(MultiCityViewHolder holder, int position) {

        holder.bind(mWeatherList.get(position));
        holder.itemView.setOnLongClickListener(v -> {
            onMultiCityLongClick.longClick(mWeatherList.get(holder.getAdapterPosition()).basic.city);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return mWeatherList.size();
    }

    public boolean isEmpty() {
        return 0 == mWeatherList.size();
    }

    class MultiCityViewHolder extends BaseViewHolder<Weather> {

        @BindView(R.id.dialog_city)
        TextView mDialogCity;
        @BindView(R.id.dialog_icon)
        ImageView mDialogIcon;
        @BindView(R.id.dialog_temp)
        TextView mDialogTemp;
        @BindView(R.id.cardView)
        CardView mCardView;

        public MultiCityViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void bind(Weather weather) {

            try {
                mDialogCity.setText(Util.safeText(weather.basic.city));
                mDialogTemp.setText(String.format("%s℃", weather.now.tmp));
            } catch (NullPointerException e) {
                PLog.e(e.getMessage());
            }

            Glide.with(mContext)
                .load(SharedPreferenceUtil.getInstance().getInt(weather.now.cond.txt, R.mipmap.none))
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        mDialogIcon.setImageBitmap(resource);
                        mDialogIcon.setColorFilter(Color.WHITE);
                    }
                });

            int code = Integer.valueOf(weather.now.cond.code);
            new CardCityHelper().applyStatus(code, weather.basic.city, mCardView);
        }
    }

    public interface onMultiCityLongClick {
        void longClick(String city);
    }
}

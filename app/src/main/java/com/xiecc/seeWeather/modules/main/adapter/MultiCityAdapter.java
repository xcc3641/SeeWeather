package com.xiecc.seeWeather.modules.main.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.xiecc.seeWeather.R;
import com.xiecc.seeWeather.common.PLog;
import com.xiecc.seeWeather.common.utils.SharedPreferenceUtil;
import com.xiecc.seeWeather.common.utils.Util;
import com.xiecc.seeWeather.modules.main.domain.Weather;
import java.util.List;

/**
 * Created by HugoXie on 16/7/9.
 *
 * Email: Hugo3641@gamil.com
 * GitHub: https://github.com/xcc3641
 * Info:
 */
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

        holder.invoke(mWeatherList.get(position));
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onMultiCityLongClick.longClick(mWeatherList.get(holder.getAdapterPosition()).basic.city);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mWeatherList.size();
    }

    public boolean isEmpty() {
        return 0 == mWeatherList.size();
    }

    class MultiCityViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.dialog_city)
        TextView mDialogCity;
        @Bind(R.id.dialog_icon)
        ImageView mDialogIcon;
        @Bind(R.id.dialog_temp)
        TextView mDialogTemp;
        @Bind(R.id.cardView)
        CardView mCardView;

        public MultiCityViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void invoke(Weather mWeather) {


            try {
                mDialogCity.setText(Util.safeText(mWeather.basic.city));
                mDialogTemp.setText(String.format("%s℃", mWeather.now.tmp));
            }catch (NullPointerException e){
                PLog.e(e.getMessage());
            }


            Glide.with(mContext).load(SharedPreferenceUtil.getInstance().getInt(mWeather.now.cond.txt, R.mipmap.none
            )).asBitmap().into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    mDialogIcon.setImageBitmap(resource);
                    mDialogIcon.setColorFilter(Color.WHITE);
                }
            });

            int code = Integer.valueOf(mWeather.now.cond.code);
            if (code == 100) {
                mCardView.setBackground(ContextCompat.getDrawable(mContext, R.mipmap.dialog_bg_sunny));
            } else if (code >= 300 && code < 408) {
                mCardView.setBackground(ContextCompat.getDrawable(mContext, R.mipmap.dialog_bg_rainy));
            } else {
                mCardView.setBackground(ContextCompat.getDrawable(mContext, R.mipmap.dialog_bg_cloudy));
            }

            PLog.d(mWeather.now.cond.txt + " " + mWeather.now.cond.code);
        }
    }

    public interface onMultiCityLongClick {
        void longClick(String city);
    }
}

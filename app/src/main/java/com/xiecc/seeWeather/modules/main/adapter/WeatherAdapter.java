package com.xiecc.seeWeather.modules.main.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.xiecc.seeWeather.R;
import com.xiecc.seeWeather.common.PLog;
import com.xiecc.seeWeather.common.utils.Util;
import com.xiecc.seeWeather.component.AnimRecyclerViewAdapter;
import com.xiecc.seeWeather.component.ImageLoader;
import com.xiecc.seeWeather.modules.main.domain.Weather;
import com.xiecc.seeWeather.modules.setting.Setting;

/**
 * Created by hugo on 2016/1/31 0031.
 */
public class WeatherAdapter extends AnimRecyclerViewAdapter<RecyclerView.ViewHolder> {
    private static String TAG = WeatherAdapter.class.getSimpleName();

    private Context mContext;
    private final int TYPE_ONE = 0;

    private final int TYPE_TWO = 1;
    private final int TYPE_THREE = 2;
    private final int TYPE_FORE = 3;

    private Weather mWeatherData;
    private Setting mSetting;

    public WeatherAdapter(Context context, Weather weatherData) {
        mContext = context;
        this.mWeatherData = weatherData;
        mSetting = Setting.getInstance();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == TYPE_ONE) {
            return TYPE_ONE;
        }
        if (position == TYPE_TWO) {
            return TYPE_TWO;
        }
        if (position == TYPE_THREE) {
            return TYPE_THREE;
        }
        if (position == TYPE_FORE) {
            return TYPE_FORE;
        }
        return super.getItemViewType(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ONE) {
            return new NowWeatherViewHolder(
                LayoutInflater.from(mContext).inflate(R.layout.item_temperature, parent, false));
        }
        if (viewType == TYPE_TWO) {
            return new HoursWeatherViewHolder(
                LayoutInflater.from(mContext).inflate(R.layout.item_hour_info, parent, false));
        }
        if (viewType == TYPE_THREE) {
            return new SuggestionViewHolder(
                LayoutInflater.from(mContext).inflate(R.layout.item_suggestion, parent, false));
        }
        if (viewType == TYPE_FORE) {
            return new ForecastViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_forecast, parent, false));
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof NowWeatherViewHolder) {
            try {
                ((NowWeatherViewHolder) holder).tempFlu.setText(String.format("%s℃", mWeatherData.now.tmp));
                ((NowWeatherViewHolder) holder).tempMax.setText(
                    String.format("↑ %s °", mWeatherData.dailyForecast.get(0).tmp.max));
                ((NowWeatherViewHolder) holder).tempMin.setText(
                    String.format("↓ %s °", mWeatherData.dailyForecast.get(0).tmp.min));
                ((NowWeatherViewHolder) holder).tempPm.setText(Util.safeText("PM25： ", mWeatherData.aqi.city.pm25));
                ((NowWeatherViewHolder) holder).tempQuality.setText(Util.safeText("空气质量： ", mWeatherData.aqi.city.qlty));
                ImageLoader.load(mContext, mSetting.getInt(mWeatherData.now.cond.txt, R.mipmap.none),
                    ((NowWeatherViewHolder) holder).weatherIcon);
            } catch (Exception e) {
                PLog.e(TAG, e.toString());
            }

            holder.itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(mWeatherData);
                }
            });
        }
        if (holder instanceof HoursWeatherViewHolder) {
            try {
                for (int i = 0; i < mWeatherData.hourlyForecast.size(); i++) {
                    //s.subString(s.length-3,s.length);
                    //第一个参数是开始截取的位置，第二个是结束位置。
                    String mDate = mWeatherData.hourlyForecast.get(i).date;
                    ((HoursWeatherViewHolder) holder).mClock[i].setText(
                        mDate.substring(mDate.length() - 5, mDate.length()));
                    ((HoursWeatherViewHolder) holder).mTemp[i].setText(
                        String.format("%s°", mWeatherData.hourlyForecast.get(i).tmp));
                    ((HoursWeatherViewHolder) holder).mHumidity[i].setText(
                        String.format("%s%%", mWeatherData.hourlyForecast.get(i).hum)
                    );
                    ((HoursWeatherViewHolder) holder).mWind[i].setText(
                        String.format("%sKm", mWeatherData.hourlyForecast.get(i).wind.spd)
                    );
                }
            } catch (Exception e) {
                //Snackbar.make(holder.itemView, R.string.api_error, Snackbar.LENGTH_SHORT).show();
                PLog.e(e.toString());
            }
        }
        if (holder instanceof SuggestionViewHolder) {
            try {

                ((SuggestionViewHolder) holder).clothBrief.setText(String.format("穿衣指数---%s", mWeatherData.suggestion.drsg.brf));
                ((SuggestionViewHolder) holder).clothTxt.setText(mWeatherData.suggestion.drsg.txt);

                ((SuggestionViewHolder) holder).sportBrief.setText(String.format("运动指数---%s", mWeatherData.suggestion.sport.brf));
                ((SuggestionViewHolder) holder).sportTxt.setText(mWeatherData.suggestion.sport.txt);

                ((SuggestionViewHolder) holder).travelBrief.setText(String.format("旅游指数---%s", mWeatherData.suggestion.trav.brf));
                ((SuggestionViewHolder) holder).travelTxt.setText(mWeatherData.suggestion.trav.txt);

                ((SuggestionViewHolder) holder).fluBrief.setText(String.format("感冒指数---%s", mWeatherData.suggestion.flu.brf));

                ((SuggestionViewHolder) holder).fluTxt.setText(mWeatherData.suggestion.flu.txt);
            } catch (Exception e) {
                PLog.e(e.toString());
            }
        }

        if (holder instanceof ForecastViewHolder) {
            try {
                //今日 明日
                ((ForecastViewHolder) holder).forecastDate[0].setText("今日");
                ((ForecastViewHolder) holder).forecastDate[1].setText("明日");
                for (int i = 0; i < mWeatherData.dailyForecast.size(); i++) {
                    if (i > 1) {
                        try {
                            ((ForecastViewHolder) holder).forecastDate[i].setText(
                                Util.dayForWeek(mWeatherData.dailyForecast.get(i).date));
                        } catch (Exception e) {
                            PLog.e(TAG, e.toString());
                        }
                    }
                    ImageLoader.load(mContext, mSetting.getInt(mWeatherData.dailyForecast.get(i).cond.txtD, R.mipmap.none),
                        ((ForecastViewHolder) holder).forecastIcon[i]);
                    ((ForecastViewHolder) holder).forecastTemp[i].setText(
                        String.format("%s° %s°",
                            mWeatherData.dailyForecast.get(i).tmp.min,
                            mWeatherData.dailyForecast.get(i).tmp.max));
                    ((ForecastViewHolder) holder).forecastTxt[i].setText(
                        String.format("%s。 最高%s℃。 %s %s %s km/h。 降水几率 %s%%。",
                            mWeatherData.dailyForecast.get(i).cond.txtD,
                            mWeatherData.dailyForecast.get(i).tmp.max,
                            mWeatherData.dailyForecast.get(i).wind.sc,
                            mWeatherData.dailyForecast.get(i).wind.dir,
                            mWeatherData.dailyForecast.get(i).wind.spd,
                            mWeatherData.dailyForecast.get(i).pop));
                }
            } catch (Exception e) {
                PLog.e(e.toString());
            }
        }

        showItemAnim(holder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return mWeatherData.status != null ? 4 : 0;
    }

    /**
     * 当前天气情况
     */
    class NowWeatherViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        private ImageView weatherIcon;
        private TextView tempFlu;
        private TextView tempMax;
        private TextView tempMin;

        private TextView tempPm;
        private TextView tempQuality;

        public NowWeatherViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.cardView);
            weatherIcon = (ImageView) itemView.findViewById(R.id.weather_icon);
            tempFlu = (TextView) itemView.findViewById(R.id.temp_flu);
            tempMax = (TextView) itemView.findViewById(R.id.temp_max);
            tempMin = (TextView) itemView.findViewById(R.id.temp_min);

            tempPm = (TextView) itemView.findViewById(R.id.temp_pm);
            tempQuality = (TextView) itemView.findViewById(R.id.temp_quality);
        }
    }

    /**
     * 当日小时预告
     */
    class HoursWeatherViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout itemHourInfoLinearlayout;
        private TextView[] mClock = new TextView[mWeatherData.hourlyForecast.size()];
        private TextView[] mTemp = new TextView[mWeatherData.hourlyForecast.size()];
        private TextView[] mHumidity = new TextView[mWeatherData.hourlyForecast.size()];
        private TextView[] mWind = new TextView[mWeatherData.hourlyForecast.size()];

        public HoursWeatherViewHolder(View itemView) {
            super(itemView);
            itemHourInfoLinearlayout = (LinearLayout) itemView.findViewById(R.id.item_hour_info_linearlayout);

            for (int i = 0; i < mWeatherData.hourlyForecast.size(); i++) {
                View view = View.inflate(mContext, R.layout.item_hour_info_line, null);
                mClock[i] = (TextView) view.findViewById(R.id.one_clock);
                mTemp[i] = (TextView) view.findViewById(R.id.one_temp);
                mHumidity[i] = (TextView) view.findViewById(R.id.one_humidity);
                mWind[i] = (TextView) view.findViewById(R.id.one_wind);
                itemHourInfoLinearlayout.addView(view);
            }
        }
    }

    /**
     * 当日建议
     */
    class SuggestionViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        private TextView clothBrief;
        private TextView clothTxt;
        private TextView sportBrief;
        private TextView sportTxt;
        private TextView travelBrief;
        private TextView travelTxt;
        private TextView fluBrief;
        private TextView fluTxt;

        public SuggestionViewHolder(View itemView) {
            super(itemView);

            cardView = (CardView) itemView.findViewById(R.id.cardView);
            clothBrief = (TextView) itemView.findViewById(R.id.cloth_brief);
            clothTxt = (TextView) itemView.findViewById(R.id.cloth_txt);
            sportBrief = (TextView) itemView.findViewById(R.id.sport_brief);
            sportTxt = (TextView) itemView.findViewById(R.id.sport_txt);
            travelBrief = (TextView) itemView.findViewById(R.id.travel_brief);
            travelTxt = (TextView) itemView.findViewById(R.id.travel_txt);
            fluBrief = (TextView) itemView.findViewById(R.id.flu_brief);
            fluTxt = (TextView) itemView.findViewById(R.id.flu_txt);
        }
    }

    /**
     * 未来天气
     */
    class ForecastViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout forecastLinear;
        private TextView[] forecastDate = new TextView[mWeatherData.dailyForecast.size()];
        private TextView[] forecastTemp = new TextView[mWeatherData.dailyForecast.size()];
        private TextView[] forecastTxt = new TextView[mWeatherData.dailyForecast.size()];
        private ImageView[] forecastIcon = new ImageView[mWeatherData.dailyForecast.size()];

        public ForecastViewHolder(View itemView) {
            super(itemView);
            forecastLinear = (LinearLayout) itemView.findViewById(R.id.forecast_linear);
            for (int i = 0; i < mWeatherData.dailyForecast.size(); i++) {
                View view = View.inflate(mContext, R.layout.item_forecast_line, null);
                forecastDate[i] = (TextView) view.findViewById(R.id.forecast_date);
                forecastTemp[i] = (TextView) view.findViewById(R.id.forecast_temp);
                forecastTxt[i] = (TextView) view.findViewById(R.id.forecast_txt);
                forecastIcon[i] = (ImageView) view.findViewById(R.id.forecast_icon);
                forecastLinear.addView(view);
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Weather mWeather);
    }

    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}

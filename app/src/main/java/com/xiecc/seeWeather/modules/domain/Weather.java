package com.xiecc.seeWeather.modules.domain;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class Weather implements Serializable {

    /**
     * city : {"aqi":"99","co":"1","no2":"87","o3":"36","pm10":"106","pm25":"74","qlty":"良","so2":"16"}
     */

    @SerializedName("aqi") public AqiEntity aqi;
    /**
     * city : 重庆
     * cnty : 中国
     * id : CN101040100
     * lat : 29.581000
     * lon : 106.549000
     * update : {"loc":"2016-02-18 21:04","utc":"2016-02-18 13:04"}
     */

    @SerializedName("basic") public BasicEntity basic;
    /**
     * cond : {"code":"101","txt":"多云"}
     * fl : 10
     * hum : 57
     * pcpn : 0
     * pres : 1019
     * tmp : 11
     * vis : 10
     * wind : {"deg":"20","dir":"西北风","sc":"4-5","spd":"17"}
     */

    @SerializedName("now") public NowEntity now;
    /**
     * aqi : {"city":{"aqi":"99","co":"1","no2":"87","o3":"36","pm10":"106","pm25":"74","qlty":"良","so2":"16"}}
     * basic : {"city":"重庆","cnty":"中国","id":"CN101040100","lat":"29.581000","lon":"106.549000","update":{"loc":"2016-02-18
     * 21:04","utc":"2016-02-18 13:04"}}
     * daily_forecast : [{"astro":{"sr":"07:30","ss":"18:44"},"cond":{"code_d":"100","code_n":"104","txt_d":"晴","txt_n":"阴"},"date":"2016-02-18","hum":"38","pcpn":"0.0","pop":"0","pres":"1019","tmp":{"max":"19","min":"7"},"vis":"10","wind":{"deg":"54","dir":"无持续风向","sc":"微风","spd":"6"}},{"astro":{"sr":"07:29","ss":"18:45"},"cond":{"code_d":"104","code_n":"104","txt_d":"阴","txt_n":"阴"},"date":"2016-02-19","hum":"54","pcpn":"0.7","pop":"31","pres":"1027","tmp":{"max":"13","min":"7"},"vis":"2","wind":{"deg":"204","dir":"无持续风向","sc":"微风","spd":"1"}},{"astro":{"sr":"07:29","ss":"18:46"},"cond":{"code_d":"104","code_n":"104","txt_d":"阴","txt_n":"阴"},"date":"2016-02-20","hum":"56","pcpn":"1.6","pop":"61","pres":"1027","tmp":{"max":"12","min":"7"},"vis":"10","wind":{"deg":"141","dir":"无持续风向","sc":"微风","spd":"7"}},{"astro":{"sr":"07:28","ss":"18:46"},"cond":{"code_d":"305","code_n":"305","txt_d":"小雨","txt_n":"小雨"},"date":"2016-02-21","hum":"73","pcpn":"5.9","pop":"72","pres":"1020","tmp":{"max":"10","min":"6"},"vis":"2","wind":{"deg":"48","dir":"无持续风向","sc":"微风","spd":"3"}},{"astro":{"sr":"07:27","ss":"18:47"},"cond":{"code_d":"104","code_n":"104","txt_d":"阴","txt_n":"阴"},"date":"2016-02-22","hum":"60","pcpn":"1.0","pop":"67","pres":"1025","tmp":{"max":"10","min":"7"},"vis":"10","wind":{"deg":"95","dir":"无持续风向","sc":"微风","spd":"10"}},{"astro":{"sr":"07:26","ss":"18:48"},"cond":{"code_d":"305","code_n":"104","txt_d":"小雨","txt_n":"阴"},"date":"2016-02-23","hum":"74","pcpn":"6.5","pop":"58","pres":"1027","tmp":{"max":"10","min":"7"},"vis":"9","wind":{"deg":"45","dir":"无持续风向","sc":"微风","spd":"10"}},{"astro":{"sr":"07:25","ss":"18:49"},"cond":{"code_d":"104","code_n":"104","txt_d":"阴","txt_n":"阴"},"date":"2016-02-24","hum":"56","pcpn":"3.0","pop":"50","pres":"1028","tmp":{"max":"11","min":"8"},"vis":"10","wind":{"deg":"47","dir":"无持续风向","sc":"微风","spd":"7"}}]
     * hourly_forecast : [{"date":"2016-02-18 22:00","hum":"53","pop":"0","pres":"1021","tmp":"14","wind":{"deg":"13","dir":"东北风","sc":"微风","spd":"16"}}]
     * now : {"cond":{"code":"101","txt":"多云"},"fl":"10","hum":"57","pcpn":"0","pres":"1019","tmp":"11","vis":"10","wind":{"deg":"20","dir":"西北风","sc":"4-5","spd":"17"}}
     * status : ok
     * suggestion : {"comf":{"brf":"较舒适","txt":"白天天气阴沉，会感到有点儿凉，但大部分人完全可以接受。"},"cw":{"brf":"较适宜","txt":"较适宜洗车，未来一天无雨，风力较小，擦洗一新的汽车至少能保持一天。"},"drsg":{"brf":"较冷","txt":"建议着厚外套加毛衣等服装。年老体弱者宜着大衣、呢外套加羊毛衫。"},"flu":{"brf":"较易发","txt":"昼夜温差较大，较易发生感冒，请适当增减衣服。体质较弱的朋友请注意防护。"},"sport":{"brf":"较适宜","txt":"阴天，较适宜进行各种户内外运动。"},"trav":{"brf":"适宜","txt":"天气较好，温度适宜，总体来说还是好天气哦，这样的天气适宜旅游，您可以尽情地享受大自然的风光。"},"uv":{"brf":"最弱","txt":"属弱紫外线辐射天气，无需特别防护。若长期在户外，建议涂擦SPF在8-12之间的防晒护肤品。"}}
     */

    @SerializedName("status") public String status;
    /**
     * comf : {"brf":"较舒适","txt":"白天天气阴沉，会感到有点儿凉，但大部分人完全可以接受。"}
     * cw : {"brf":"较适宜","txt":"较适宜洗车，未来一天无雨，风力较小，擦洗一新的汽车至少能保持一天。"}
     * drsg : {"brf":"较冷","txt":"建议着厚外套加毛衣等服装。年老体弱者宜着大衣、呢外套加羊毛衫。"}
     * flu : {"brf":"较易发","txt":"昼夜温差较大，较易发生感冒，请适当增减衣服。体质较弱的朋友请注意防护。"}
     * sport : {"brf":"较适宜","txt":"阴天，较适宜进行各种户内外运动。"}
     * trav : {"brf":"适宜","txt":"天气较好，温度适宜，总体来说还是好天气哦，这样的天气适宜旅游，您可以尽情地享受大自然的风光。"}
     * uv : {"brf":"最弱","txt":"属弱紫外线辐射天气，无需特别防护。若长期在户外，建议涂擦SPF在8-12之间的防晒护肤品。"}
     */

    @SerializedName("suggestion") public SuggestionEntity suggestion;
    /**
     * astro : {"sr":"07:30","ss":"18:44"}
     * cond : {"code_d":"100","code_n":"104","txt_d":"晴","txt_n":"阴"}
     * date : 2016-02-18
     * hum : 38
     * pcpn : 0.0
     * pop : 0
     * pres : 1019
     * tmp : {"max":"19","min":"7"}
     * vis : 10
     * wind : {"deg":"54","dir":"无持续风向","sc":"微风","spd":"6"}
     */

    @SerializedName("daily_forecast") public List<DailyForecastEntity> dailyForecast;
    /**
     * date : 2016-02-18 22:00
     * hum : 53
     * pop : 0
     * pres : 1021
     * tmp : 14
     * wind : {"deg":"13","dir":"东北风","sc":"微风","spd":"16"}
     */

    @SerializedName("hourly_forecast") public List<HourlyForecastEntity> hourlyForecast;

    public static class AqiEntity implements Serializable {
        /**
         * aqi : 99
         * co : 1
         * no2 : 87
         * o3 : 36
         * pm10 : 106
         * pm25 : 74
         * qlty : 良
         * so2 : 16
         */

        @SerializedName("city") public CityEntity city;

        public static class CityEntity implements Serializable {
            @SerializedName("aqi") public String aqi;
            @SerializedName("co") public String co;
            @SerializedName("no2") public String no2;
            @SerializedName("o3") public String o3;
            @SerializedName("pm10") public String pm10;
            @SerializedName("pm25") public String pm25;
            @SerializedName("qlty") public String qlty;
            @SerializedName("so2") public String so2;
        }
    }

    public static class BasicEntity implements Serializable {
        @SerializedName("city") public String city;
        @SerializedName("cnty") public String cnty;
        @SerializedName("id") public String id;
        @SerializedName("lat") public String lat;
        @SerializedName("lon") public String lon;
        /**
         * loc : 2016-02-18 21:04
         * utc : 2016-02-18 13:04
         */

        @SerializedName("update") public UpdateEntity update;

        public static class UpdateEntity implements Serializable {
            @SerializedName("loc") public String loc;
            @SerializedName("utc") public String utc;
        }
    }

    public static class NowEntity implements Serializable {
        /**
         * code : 101
         * txt : 多云
         */

        @SerializedName("cond") public CondEntity cond;
        @SerializedName("fl") public String fl;
        @SerializedName("hum") public String hum;
        @SerializedName("pcpn") public String pcpn;
        @SerializedName("pres") public String pres;
        @SerializedName("tmp") public String tmp;
        @SerializedName("vis") public String vis;
        /**
         * deg : 20
         * dir : 西北风
         * sc : 4-5
         * spd : 17
         */

        @SerializedName("wind") public WindEntity wind;

        public static class CondEntity implements Serializable {
            @SerializedName("code") public String code;
            @SerializedName("txt") public String txt;
        }

        public static class WindEntity implements Serializable {
            @SerializedName("deg") public String deg;
            @SerializedName("dir") public String dir;
            @SerializedName("sc") public String sc;
            @SerializedName("spd") public String spd;
        }
    }

    public static class SuggestionEntity implements Serializable {
        /**
         * brf : 较舒适
         * txt : 白天天气阴沉，会感到有点儿凉，但大部分人完全可以接受。
         */

        @SerializedName("comf") public ComfEntity comf;
        /**
         * brf : 较适宜
         * txt : 较适宜洗车，未来一天无雨，风力较小，擦洗一新的汽车至少能保持一天。
         */

        @SerializedName("cw") public CwEntity cw;
        /**
         * brf : 较冷
         * txt : 建议着厚外套加毛衣等服装。年老体弱者宜着大衣、呢外套加羊毛衫。
         */

        @SerializedName("drsg") public DrsgEntity drsg;
        /**
         * brf : 较易发
         * txt : 昼夜温差较大，较易发生感冒，请适当增减衣服。体质较弱的朋友请注意防护。
         */

        @SerializedName("flu") public FluEntity flu;
        /**
         * brf : 较适宜
         * txt : 阴天，较适宜进行各种户内外运动。
         */

        @SerializedName("sport") public SportEntity sport;
        /**
         * brf : 适宜
         * txt : 天气较好，温度适宜，总体来说还是好天气哦，这样的天气适宜旅游，您可以尽情地享受大自然的风光。
         */

        @SerializedName("trav") public TravEntity trav;
        /**
         * brf : 最弱
         * txt : 属弱紫外线辐射天气，无需特别防护。若长期在户外，建议涂擦SPF在8-12之间的防晒护肤品。
         */

        @SerializedName("uv") public UvEntity uv;

        public static class ComfEntity implements Serializable {
            @SerializedName("brf") public String brf;
            @SerializedName("txt") public String txt;
        }

        public static class CwEntity implements Serializable {
            @SerializedName("brf") public String brf;
            @SerializedName("txt") public String txt;
        }

        public static class DrsgEntity implements Serializable {
            @SerializedName("brf") public String brf;
            @SerializedName("txt") public String txt;
        }

        public static class FluEntity implements Serializable {
            @SerializedName("brf") public String brf;
            @SerializedName("txt") public String txt;
        }

        public static class SportEntity implements Serializable {
            @SerializedName("brf") public String brf;
            @SerializedName("txt") public String txt;
        }

        public static class TravEntity implements Serializable {
            @SerializedName("brf") public String brf;
            @SerializedName("txt") public String txt;
        }

        public static class UvEntity implements Serializable {
            @SerializedName("brf") public String brf;
            @SerializedName("txt") public String txt;
        }
    }

    public static class DailyForecastEntity implements Serializable {
        /**
         * sr : 07:30
         * ss : 18:44
         */

        @SerializedName("astro") public AstroEntity astro;
        /**
         * code_d : 100
         * code_n : 104
         * txt_d : 晴
         * txt_n : 阴
         */

        @SerializedName("cond") public CondEntity cond;
        @SerializedName("date") public String date;
        @SerializedName("hum") public String hum;
        @SerializedName("pcpn") public String pcpn;
        @SerializedName("pop") public String pop;
        @SerializedName("pres") public String pres;
        /**
         * max : 19
         * min : 7
         */

        @SerializedName("tmp") public TmpEntity tmp;
        @SerializedName("vis") public String vis;
        /**
         * deg : 54
         * dir : 无持续风向
         * sc : 微风
         * spd : 6
         */

        @SerializedName("wind") public WindEntity wind;

        public static class AstroEntity implements Serializable {
            @SerializedName("sr") public String sr;
            @SerializedName("ss") public String ss;
        }

        public static class CondEntity implements Serializable {
            @SerializedName("code_d") public String codeD;
            @SerializedName("code_n") public String codeN;
            @SerializedName("txt_d") public String txtD;
            @SerializedName("txt_n") public String txtN;
        }

        public static class TmpEntity implements Serializable {
            @SerializedName("max") public String max;
            @SerializedName("min") public String min;
        }

        public static class WindEntity implements Serializable {
            @SerializedName("deg") public String deg;
            @SerializedName("dir") public String dir;
            @SerializedName("sc") public String sc;
            @SerializedName("spd") public String spd;
        }
    }

    public static class HourlyForecastEntity implements Serializable {
        @SerializedName("date") public String date;
        @SerializedName("hum") public String hum;
        @SerializedName("pop") public String pop;
        @SerializedName("pres") public String pres;
        @SerializedName("tmp") public String tmp;
        /**
         * deg : 13
         * dir : 东北风
         * sc : 微风
         * spd : 16
         */

        @SerializedName("wind") public WindEntity wind;

        public static class WindEntity implements Serializable {
            @SerializedName("deg") public String deg;
            @SerializedName("dir") public String dir;
            @SerializedName("sc") public String sc;
            @SerializedName("spd") public String spd;
        }
    }
}

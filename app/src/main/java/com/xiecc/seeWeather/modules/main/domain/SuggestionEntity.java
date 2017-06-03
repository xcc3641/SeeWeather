package com.xiecc.seeWeather.modules.main.domain;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public  class SuggestionEntity implements Serializable {
        @SerializedName("comf")
        public ComfEntity comf;

        @SerializedName("cw")
        public CwEntity cw;
        /**
         * brf : 较冷
         * txt : 建议着厚外套加毛衣等服装。年老体弱者宜着大衣、呢外套加羊毛衫。
         */

        @SerializedName("drsg")
        public DrsgEntity drsg;
        /**
         * brf : 较易发
         * txt : 昼夜温差较大，较易发生感冒，请适当增减衣服。体质较弱的朋友请注意防护。
         */

        @SerializedName("flu")
        public FluEntity flu;
        /**
         * brf : 较适宜
         * txt : 阴天，较适宜进行各种户内外运动。
         */

        @SerializedName("sport")
        public SportEntity sport;
        /**
         * brf : 适宜
         * txt : 天气较好，温度适宜，总体来说还是好天气哦，这样的天气适宜旅游，您可以尽情地享受大自然的风光。
         */

        @SerializedName("trav")
        public TravEntity trav;
        /**
         * brf : 最弱
         * txt : 属弱紫外线辐射天气，无需特别防护。若长期在户外，建议涂擦SPF在8-12之间的防晒护肤品。
         */

        @SerializedName("uv")
        public UvEntity uv;

        public static class ComfEntity implements Serializable {
            @SerializedName("brf")
            public String brf;
            @SerializedName("txt")
            public String txt;
        }

        public static class CwEntity implements Serializable {
            @SerializedName("brf")
            public String brf;
            @SerializedName("txt")
            public String txt;
        }

        public static class DrsgEntity implements Serializable {
            @SerializedName("brf")
            public String brf;
            @SerializedName("txt")
            public String txt;
        }

        public static class FluEntity implements Serializable {
            @SerializedName("brf")
            public String brf;
            @SerializedName("txt")
            public String txt;
        }

        public static class SportEntity implements Serializable {
            @SerializedName("brf")
            public String brf;
            @SerializedName("txt")
            public String txt;
        }

        public static class TravEntity implements Serializable {
            @SerializedName("brf")
            public String brf;
            @SerializedName("txt")
            public String txt;
        }

        public static class UvEntity implements Serializable {
            @SerializedName("brf")
            public String brf;
            @SerializedName("txt")
            public String txt;
        }
    }
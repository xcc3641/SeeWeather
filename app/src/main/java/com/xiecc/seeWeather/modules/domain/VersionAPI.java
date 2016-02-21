package com.xiecc.seeWeather.modules.domain;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2016/2/21 0021.
 */
public class VersionAPI {

    @SerializedName("name") public String name;
    @SerializedName("version") public String version;
    @SerializedName("changelog") public String changelog;
    @SerializedName("updated_at") public int updatedAt;
    @SerializedName("versionShort") public String versionShort;
    @SerializedName("build") public String build;
    @SerializedName("install_url") public String installUrl;
    @SerializedName("direct_install_url") public String directInstallUrl;
    @SerializedName("update_url") public String updateUrl;
    /**
     * fsize : 2626655
     */
    @SerializedName("binary") public BinaryEntity binary;

    public static class BinaryEntity {
        @SerializedName("fsize") public int fsize;
    }
}

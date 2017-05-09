package com.xiecc.seeWeather.modules.city.domain;

import android.os.Parcel;
import android.os.Parcelable;

public class Province implements Parcelable {

    public String mProName;
    public int mProSort;

    @Override
    public String toString() {
        return "[mProName] = " + mProName + "\n" +
            "[mProSort] = " + mProSort;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mProName);
        dest.writeInt(this.mProSort);
    }

    public Province() {
    }

    protected Province(Parcel in) {
        this.mProName = in.readString();
        this.mProSort = in.readInt();
    }

    public static final Creator<Province> CREATOR = new Creator<Province>() {
        @Override
        public Province createFromParcel(Parcel source) {
            return new Province(source);
        }

        @Override
        public Province[] newArray(int size) {
            return new Province[size];
        }
    };
}

package com.tensynchina.hook.wechat;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by llx on 2018/3/21.
 */

public class ReplaceUrlEvent implements Parcelable {
    private final String activityName;
    private final String url;

    public ReplaceUrlEvent(String activityName,String url) {
        this.activityName = activityName;
        this.url = url;
    }

    protected ReplaceUrlEvent(Parcel in) {
        activityName = in.readString();
        url = in.readString();
    }

    public static final Creator<ReplaceUrlEvent> CREATOR = new Creator<ReplaceUrlEvent>() {
        @Override
        public ReplaceUrlEvent createFromParcel(Parcel in) {
            return new ReplaceUrlEvent(in);
        }

        @Override
        public ReplaceUrlEvent[] newArray(int size) {
            return new ReplaceUrlEvent[size];
        }
    };

    public String getActivityName() {
        return activityName;
    }

    public String getUrl() {
        return url;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(activityName);
        dest.writeString(url);
    }
}

package com.tensynchina.hook.task;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by llx on 2018/3/16.
 */

public class Error implements Parcelable {

    public static final int NO_MORE_TEXT = 3;
    public static final String  NO_MORE_TEXT_STR = "没有更多的搜索结果";

    public static final int WE_CHAT_SERVER_BLOCK = 4;
    public static final String WE_CHAT_SERVER_BLOCK_STR = "查询接口被封";

    public static final int NET_WORK_ERROR = 5;
    public static final String NET_WORK_ERROR_STR = "网络错误";

    public static final int OFFICE_ACCOUNT_NOT_MATCH = 6;

    // 当前的微信公众号没有被关注
    public static final int OFFICE_ACCOUNT_IS_NOT_ATTENTION = 7;

    // 待搜索的关键词为空字符串
    public static final int KEY_EMPTY = 8;

    // 重复关注公众号
    public static final int DUPLICATE_ATTENTION_OFFICE_ACCOUNT = 9;

    public static final int PAGE_ERROR = 10;

    public static final int NO_HISTORY_PAGE = 11;

    public static final int PARSE_JSON_FAILED = 12;

    public static final int OTHER = 999;
    // 弹出历史消息界面失败
    public static final int OPEN_WECHAT_WEBVIEW_FAILED = 13;

    private int code;
    private String des;

    public Error() {
    }

    public Error(int code, String des) {
        this.code = code;
        this.des = des;
    }

    protected Error(Parcel in) {
        code = in.readInt();
        des = in.readString();
    }

    public static final Creator<Error> CREATOR = new Creator<Error>() {
        @Override
        public Error createFromParcel(Parcel in) {
            return new Error(in);
        }

        @Override
        public Error[] newArray(int size) {
            return new Error[size];
        }
    };

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    @Override
    public String toString() {
        return "Error{" +
                "code=" + code +
                ", des='" + des + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(code);
        dest.writeString(des);
    }
}

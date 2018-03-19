package com.tensynchina.hook.task;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 一个特定的任务
 * Created by llx on 2018/3/16.
 */

public class Param implements Parcelable {
    private String packageName;
    private int taskTag;
    private String taskId;
    private String json;
    /**
     * 这个字段标志了此任务是从哪个client发来的
     */
    private String addressUuid;

    public Param() {
    }

    public Param(String packageName, int taskTag, String taskId, String json) {
        this.packageName = packageName;
        this.taskTag = taskTag;
        this.taskId = taskId;
        this.json = json;
    }

    protected Param(Parcel in) {
        packageName = in.readString();
        taskTag = in.readInt();
        taskId = in.readString();
        json = in.readString();
        addressUuid = in.readString();
    }

    public static final Creator<Param> CREATOR = new Creator<Param>() {
        @Override
        public Param createFromParcel(Parcel in) {
            return new Param(in);
        }

        @Override
        public Param[] newArray(int size) {
            return new Param[size];
        }
    };

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public int getTaskTag() {
        return taskTag;
    }

    public void setTaskTag(int taskTag) {
        this.taskTag = taskTag;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public String getAddressUuid() {
        return addressUuid;
    }

    public void setAddressUuid(String addressUuid) {
        this.addressUuid = addressUuid;
    }

    @Override
    public String toString() {
        return "Param{" +
                "packageName='" + packageName + '\'' +
                ", taskTag=" + taskTag +
                ", taskId='" + taskId + '\'' +
                ", json='" + json + '\'' +
                ", addressUuid='" + addressUuid + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(packageName);
        dest.writeInt(taskTag);
        dest.writeString(taskId);
        dest.writeString(json);
        dest.writeString(addressUuid);
    }
}

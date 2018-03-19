package com.tensynchina.hook.task;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 一个任务执行结束后的返回结果
 * Created by llx on 2018/3/16.
 */

public class Result implements Parcelable {

    private String packageName;
    private int taskTag;
    private String taskId;
    private String data;
    private Error error;
    /**
     * 这个uuid是发送过去的task的id，返回的result应该把这个uuid在返回来
     */
    private String uuid;

    public Result() {
    }

    public Result(String packageName, int taskTag, String taskId, String data,Error error) {
        this.packageName = packageName;
        this.taskTag = taskTag;
        this.taskId = taskId;
        this.data = data;
        this.error = error;
    }

    protected Result(Parcel in) {
        packageName = in.readString();
        taskTag = in.readInt();
        taskId = in.readString();
        data = in.readString();
        uuid = in.readString();
        error = in.readParcelable(getClass().getClassLoader());
    }

    public static final Creator<Result> CREATOR = new Creator<Result>() {
        @Override
        public Result createFromParcel(Parcel in) {
            return new Result(in);
        }

        @Override
        public Result[] newArray(int size) {
            return new Result[size];
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

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "Result{" +
                "packageName='" + packageName + '\'' +
                ", taskTag=" + taskTag +
                ", taskId='" + taskId + '\'' +
                ", data='" + data + '\'' +
                ", error=" + error +
                ", uuid='" + uuid + '\'' +
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
        dest.writeString(data);
        dest.writeString(uuid);
        dest.writeParcelable(error,flags);
    }
}

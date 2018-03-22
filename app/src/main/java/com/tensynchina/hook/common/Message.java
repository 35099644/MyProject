package com.tensynchina.hook.common;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 服务端收发消息的封装
 * Message是进程间通信的载体，push包和task包都需要这个类，因此只能放到common包里
 * Created by llx on 2018/3/15.
 */

public class Message implements Parcelable {
    /**
     * 消息体
     */
    private final String message;
    /**
     * 发送消息的客户端的uuid
     */
    private final String uuid;

    public Message(String message, String uuid) {
        this.message = message;
        this.uuid = uuid;
    }

    protected Message(Parcel in) {
        message = in.readString();
        uuid = in.readString();
    }

    public static final Creator<Message> CREATOR = new Creator<Message>() {
        @Override
        public Message createFromParcel(Parcel in) {
            return new Message(in);
        }

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
        }
    };

    public String getMessage() {
        return message;
    }

    public String getUuid() {
        return uuid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Message message1 = (Message) o;

        if (message != null ? !message.equals(message1.message) : message1.message != null)
            return false;
        return uuid != null ? uuid.equals(message1.uuid) : message1.uuid == null;
    }

    @Override
    public int hashCode() {
        int result = message != null ? message.hashCode() : 0;
        result = 31 * result + (uuid != null ? uuid.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Message{" +
                "message='" + message + '\'' +
                ", uuid='" + uuid + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(message);
        dest.writeString(uuid);
    }
}

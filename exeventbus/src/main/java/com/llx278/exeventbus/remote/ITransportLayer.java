package com.llx278.exeventbus.remote;

import android.os.Bundle;

import com.llx278.exeventbus.exception.TimeoutException;

/**
 * 此接口实现进程间可靠的数据通讯
 * Created by llx on 2018/2/28.
 */

public interface ITransportLayer {

    void init();

    void destroy();

    /**
     * 向一个进程发送消息，此方法会阻塞当前的线程，直到超时返回
     * @param address 进程地址
     * @param message 消息体
     * @param timeout 超时时间 ms
     * @return true 消息已经发送到了相应的进程，false 发送失败
     */
    void send(String address,Bundle message,long timeout) throws TimeoutException;

    /**
     * 发送一条广播消息
     */
    void sendBroadcast(Bundle message);

    void setOnReceiveListener(Receiver listener);

}

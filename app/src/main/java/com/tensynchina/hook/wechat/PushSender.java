package com.tensynchina.hook.wechat;

import com.alibaba.fastjson.JSON;
import com.llx278.exeventbus.ExEventBus;
import com.llx278.exeventbus.exception.TimeoutException;
import com.tensynchina.hook.common.Constant;
import com.tensynchina.hook.task.Result;
import com.tensynchina.hook.utils.XLogger;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * 这主要是对消息做一下缓冲，因为ExEventBus的进程间发布底层用的广播，并发量太大的话消息容易挂掉。
 * Created by llx on 2018/3/22.
 */

public class PushSender extends Thread {
    private final LinkedBlockingQueue<Result> sPushTask = new LinkedBlockingQueue<>();

    PushSender() {
    }

    void addPushMessage(Result result) {
        sPushTask.add(result);
    }

    @Override
    public void run() {
        super.run();
        while (true) {
            try {
                Thread.sleep(500);
                Result result = sPushTask.take();
                String tag = Constant.PUSH_MESSAGE_TAG;
                String returnClassName = void.class.getName();
                long timeout = 1000 * 10;
                XLogger.d("准备向messageservice发送消息");
                ExEventBus.getDefault().remotePublish(JSON.toJSONString(result), tag, returnClassName, timeout);
            } catch (InterruptedException e) {
                XLogger.e(e);
                break;
            } catch (Exception e) {
                XLogger.e(e);
            }
        }
    }
}

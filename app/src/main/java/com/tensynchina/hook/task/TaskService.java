package com.tensynchina.hook.task;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.llx278.exeventbus.ExEventBus;
import com.llx278.exeventbus.Subscriber;
import com.llx278.exeventbus.ThreadModel;
import com.orhanobut.logger.Logger;
import com.tensynchina.hook.common.Constant;
import com.tensynchina.hook.common.Message;
import com.tensynchina.hook.utils.XLogger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 *
 * Created by llx on 2018/3/15.
 */

public class TaskService extends Service {

    private Executor mTaskExecutor;
    private ExecutorService mThreadExecutor;
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("main","taskService启动");
        mThreadExecutor = Executors.newFixedThreadPool(5);
        mThreadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                ExEventBus.create(TaskService.this);
                ExEventBus.getDefault().register(TaskService.this);
            }
        });
        mTaskExecutor = new Executor(this,mThreadExecutor);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Subscriber(tag = Constant.RECEIVE_MESSAGE_TAG,model = ThreadModel.HANDLER,remote = true)
    public void receiveMessage(Message message) {

        try {
            String uuid = message.getUuid();
            String msg = message.getMessage();
            Logger.d("接收到了一个message : uuid : " + uuid + " : " + msg.toString());
            Task task = JSON.parseObject(msg,Task.class);
            // 每一个任务都要保存这个uuid，因为当这个任务执行结束需要这个uuid把任务发送回去
            task.getParam().setAddressUuid(uuid);
            mTaskExecutor.execute(task);
        } catch (Exception e) {
            Logger.e(e,"");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mThreadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                ExEventBus.destroy();
            }
        });
        mTaskExecutor.stop();
        mThreadExecutor.shutdown();
        try {
            mThreadExecutor.awaitTermination(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            XLogger.e(e);
        }
    }
}

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
import com.tensynchina.hook.Message;

/**
 *
 * Created by llx on 2018/3/15.
 */

public class TaskService extends Service {

    public static final String RECEIVE_MESSAGE_TAG = "com_tensynchina_hook_task_TaskService_receiveMessage";

    private Executor mExecutor;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("main","taskService启动");
        new Thread(){
            @Override
            public void run() {
                ExEventBus.create(TaskService.this);
                ExEventBus.getDefault().register(TaskService.this);
            }
        }.start();
        mExecutor = new Executor(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Subscriber(tag = RECEIVE_MESSAGE_TAG,model = ThreadModel.HANDLER,remote = true)
    public void receiveMessage(Message message) {

        String uuid = message.getUuid();
        String msg = message.getMessage();
        Logger.d("接收到了一个message : uuid : " + uuid + " : " + msg.toString());
        Task task = JSON.parseObject(msg,Task.class);
        // 每一个任务都要保存这个uuid，因为当这个任务执行结束需要这个uuid把任务发送回去
        task.getParam().setAddressUuid(uuid);
        mExecutor.execute(task);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        new Thread(){
            @Override
            public void run() {
                ExEventBus.destroy();
            }
        }.start();
    }
}

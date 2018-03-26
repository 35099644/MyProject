package com.tensynchina.hook.task;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.alibaba.fastjson.JSON;
import com.llx278.exeventbus.ExEventBus;
import com.llx278.exeventbus.exception.TimeoutException;
import com.orhanobut.logger.Logger;
import com.tensynchina.hook.common.Constant;
import com.tensynchina.hook.common.Message;
import com.tensynchina.hook.push.MessageService;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * Created by llx on 2018/3/16.
 */

public class Executor {

    private final BlockingQueue<Task> mUITaskQueue;
    private Context mContext;
    private UITask mUiTask;
    private KillerSchedule mSchedule;

    Executor(Context context,ExecutorService poolExecutor) {
        mUITaskQueue = new LinkedBlockingQueue<>();
        mContext = context;
        mUiTask = new UITask();
        mSchedule = new KillerSchedule();
        poolExecutor.execute(mUiTask);
        poolExecutor.execute(mSchedule);
    }

    private void handleTask(Task task) {
        TaskHandler taskHandler = TaskHandlerFactory.parse(task);
        if (taskHandler != null) {
            taskHandler.handle(mContext);
        }
    }

    void execute(Task task) {
        mUITaskQueue.add(task);
    }

    void stop() {
        mUiTask.stop = true;
        mSchedule.stop = true;
    }

    /**
     * 处理UI的任务
     */
    private class UITask implements Runnable {
        private boolean stop = false;
        @Override
        public void run() {
            while (!stop) {
                try {
                    Task task = mUITaskQueue.take();
                    // 对任务进行处理
                    handleTask(task);
                    // 每10s一个任务
                    Thread.sleep(10 * 1000);
                } catch (InterruptedException e) {
                    Logger.e(e,"");
                    break;
                }
            }
            Logger.d("UITask 退出");
        }
    }

    private class KillerSchedule implements Runnable{

        private boolean stop = false;

        @Override
        public void run() {
            while (!stop) {
                try {
                    Thread.sleep(1000 * 60 * 60);
                    // 每个固定事件都要添加一个任务
                    Logger.d("添加一个杀死其他进程的任务");
                    String taskJson = "{\"code\":1,\"param\":{\"taskTag\":1}}";
                    Task task = JSON.parseObject(taskJson,Task.class);
                    mUITaskQueue.add(task);
                    Logger.d("添加一个Task5的任务");
                    String task5Json = "{\"code\":0,\"param\":{\"packageName\":\"com.tencent.mm\"," +
                            "\"taskTag\":5,\"taskId\":\"111\",\"json\":\"{}\"}}";
                    Task task5 = JSON.parseObject(task5Json,Task.class);
                    mUITaskQueue.add(task5);
                } catch (InterruptedException e) {
                    Logger.e(e,"");
                    break;
                }
            }
            Logger.d("KillerSchedule 退出");
        }
    }
}

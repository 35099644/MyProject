package com.tensynchina.hook.task;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.alibaba.fastjson.JSON;
import com.llx278.exeventbus.Event;
import com.llx278.exeventbus.ExEventBus;
import com.llx278.exeventbus.exception.TimeoutException;
import com.orhanobut.logger.Logger;
import com.tensynchina.hook.Message;
import com.tensynchina.hook.push.MessageService;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * Created by llx on 2018/3/16.
 */

public class Executor {

    private final BlockingQueue<Task> mUITaskQueue;
    /**
     * 缓存正在处理的任务
     */
    private Task mCurrentTask;
    private Context mContext;

    Executor(Context context) {
        mUITaskQueue = new LinkedBlockingQueue<>();
        mContext = context;
        UITaskHandler uiHandler = new UITaskHandler();
        uiHandler.start();
    }

    private void handleTask(Task task) {
        Param param = task.getParam();
        String packageName = param.getPackageName();
        PackageManager packageManager = mContext.getPackageManager();
        Intent launchIntentForPackage = packageManager.getLaunchIntentForPackage(packageName);
        mContext.startActivity(launchIntentForPackage);
        try {
            // 等待1s，等待对应包名的app已经启动
            Thread.sleep(1000);
            String tag = packageName + "_TAG";
            Logger.d("tag  : " + tag);
            String resultClassName = Result.class.getName();
            long timeout = 1000 * 60 * 5;
            Result result = (Result) ExEventBus.getDefault().remotePublish(param,tag,resultClassName,timeout);
            Logger.d("获得了微信的返回结果 : " + result);
            if (result != null) {
                ResultWrapper rw = new ResultWrapper(0,result);
                String uuid = result.getUuid();
                String resultMsg = JSON.toJSONString(rw);
                Message msg = new Message(resultMsg,uuid);
                ExEventBus.getDefault().remotePublish(msg, MessageService.MESSAGE_SEND_TAG,
                        void.class.getName(),1000*5);
            }
        } catch (InterruptedException ignore) {
        } catch (TimeoutException e) {
            Logger.e(e,"");
        }
    }

    void execute(Task task) {
        if (Category.isUITask(task)) {

            mUITaskQueue.add(task);

        } else if (Category.isNoUITask(task)) {

            if (mUITaskQueue.isEmpty() || mCurrentTask == null) {
                // UI队列为空，或者自执行以来队列没有加入任何任务
                mUITaskQueue.add(task);

                // 这代表待执行的任务与当前运行的任务是同一个app，这样的话就可以把这个任务直接发送了，
                // 因为当前app的进程已经启动了
            } else if (mCurrentTask.getParam().getPackageName().
                    equals(task.getParam().getPackageName())) {
                // 这样做可能会导致偶发性的bug
                handleTask(task);
                //mUITaskQueue.add(task);
            } else {
                // 这需要将当前的app启动，因此一次只能执行一个app的任务，因此把此任务加入到ui的队列中去执行
                mUITaskQueue.add(task);
            }

        }
    }


    /**
     * 处理UI的任务
     */
    private class UITaskHandler extends Thread {

        @Override
        public void run() {
            while (true) {
                try {


                    mCurrentTask = mUITaskQueue.take();
                    // 对任务进行处理
                    handleTask(mCurrentTask);
                    // 每10s一个任务
                    Thread.sleep(10 * 1000);
                } catch (InterruptedException e) {
                    Logger.e(e,"");
                    break;
                }
            }
        }
    }
}

package com.tensynchina.hook.wechat;

import android.content.Context;
import android.os.Process;

import com.alibaba.fastjson.JSON;
import com.llx278.exeventbus.ExEventBus;
import com.llx278.exeventbus.Subscriber;
import com.llx278.exeventbus.ThreadModel;
import com.llx278.exeventbus.Type;
import com.llx278.exeventbus.exception.TimeoutException;
import com.llx278.uimocker2.ISolo;
import com.llx278.uimocker2.Solo;
import com.orhanobut.logger.Logger;
import com.tensynchina.hook.common.Constant;
import com.tensynchina.hook.task.Error;
import com.tensynchina.hook.task.Param;
import com.tensynchina.hook.task.Result;
import com.tensynchina.hook.utils.XLogger;

/**
 * 代表微信com.tencnet.mm进程里任务执行的入口
 * Created by llx on 2018/3/15.
 */

public class ExecutorForMain {

    private ISolo mSolo;

    public ExecutorForMain(Context context) {
        mSolo = new Solo(context);
    }

    /**
     * 微信所有执行任务的入口
     * @param param 任务执行的参数
     * @return 执行的结果
     */
    @Subscriber(tag = Constant.WX_TASK_TAG, type = Type.BLOCK_RETURN, remote = true,
            model = ThreadModel.POOL)
    public Result executeTask(Param param) {
        XLogger.d("微信收到了一个任务 : " + param.toString());
        BaseTask task = CreatorForTask.create(param.getTaskTag());
        if (task != null) {
            XLogger.d("准备执行task : " + task.getClass().getName());
            return task.execute(mSolo,param);
        }
        return null;
    }

    @Subscriber(tag = Constant.PROCESS_KILL_TAG,remote = true,model = ThreadModel.HANDLER)
    public void killTask(String param) {
        XLogger.d("com.tencent.mm 准备 杀死自己");

        ExEventBus.destroy();

        Process.killProcess(Process.myPid());
    }
}

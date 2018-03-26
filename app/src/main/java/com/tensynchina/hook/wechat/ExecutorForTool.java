package com.tensynchina.hook.wechat;

import android.content.Context;
import android.os.Bundle;
import android.os.Process;

import com.llx278.exeventbus.ExEventBus;
import com.llx278.exeventbus.Subscriber;
import com.llx278.exeventbus.ThreadModel;
import com.llx278.exeventbus.Type;
import com.llx278.uimocker2.ISolo;
import com.llx278.uimocker2.Solo;
import com.tensynchina.hook.common.Constant;
import com.tensynchina.hook.task.Param;
import com.tensynchina.hook.task.Result;
import com.tensynchina.hook.utils.XLogger;

/**
 * 代表微信com.tencnet.mm:tools进程里任务执行的入口
 * Created by llx on 2018/3/19.
 */

public class ExecutorForTool {

    private ISolo mSolo;

    public ExecutorForTool(Context context) {
        mSolo = new Solo(context);
        // 在这里对activity的启动做一下拦截，
        XLogger.d("准备拦截activity");
        WebActivityObserver mObserver = new WebActivityObserver();
        mSolo.getActivityUtils().addActivityLifeObserver(mObserver);
    }

    /**
     * 微信tools进程任务执行的入口
     * @param param 任务执行参数
     * @return 执行的结果
     */
    @Subscriber(tag = WConstant.TOOLS_TAG, type = Type.BLOCK_RETURN, remote = true,
            model = ThreadModel.POOL)
    public Result executeTask(Param param) {
        XLogger.d("com.tencent.mm:tools收到了一个任务 : " + param.toString());
        BaseTask task = CreatorForTaskTool.create(param.getTaskTag());
        if (task != null) {
            XLogger.d("准备执行task");
            return task.execute(mSolo,param);
        }
        return null;
    }

    /**
     * 此事件用来替换指定activity的启动参数
     * @param event 指定的事件
     */
    @Subscriber(tag = WConstant.TOOLS_REPLACE_URL,remote = true)
    public void replaceUrl(ReplaceUrlEvent event) {
        XLogger.d("准备替换activity的启动参数");
    }

    @Subscriber(tag = Constant.PROCESS_KILL_TAG,remote = true,model = ThreadModel.HANDLER)
    public void killTask(String param) {
        XLogger.d("com.tencent.mm:tools 准备 杀死自己");
        ExEventBus.destroy();
        Process.killProcess(Process.myPid());
    }
}

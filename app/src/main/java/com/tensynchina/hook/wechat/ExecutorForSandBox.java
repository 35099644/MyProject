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

public class ExecutorForSandBox {

    private ISolo mSolo;

    public ExecutorForSandBox(Context context) {
        mSolo = new Solo(context);
    }

    @Subscriber(tag = Constant.PROCESS_KILL_TAG,remote = true,model = ThreadModel.HANDLER)
    public void killTask(String param) {
        XLogger.d("com.tencent.mm:sandbox 准备 杀死自己");
        ExEventBus.destroy();
        Process.killProcess(Process.myPid());
    }
}

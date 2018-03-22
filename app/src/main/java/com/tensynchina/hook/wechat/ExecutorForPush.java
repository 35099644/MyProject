package com.tensynchina.hook.wechat;

import android.os.Process;

import com.llx278.exeventbus.ExEventBus;
import com.llx278.exeventbus.Subscriber;
import com.llx278.exeventbus.ThreadModel;
import com.tensynchina.hook.common.Constant;
import com.tensynchina.hook.utils.XLogger;

/**
 * Created by llx on 2018/3/22.
 */

public class ExecutorForPush {
    @Subscriber(tag = Constant.PROCESS_KILL_TAG,remote = true,model = ThreadModel.HANDLER)
    public void killTask(String param) {
        XLogger.d("com.tencent.mm:push 准备 杀死自己");
        ExEventBus.destroy();
        Process.killProcess(Process.myPid());
    }
}

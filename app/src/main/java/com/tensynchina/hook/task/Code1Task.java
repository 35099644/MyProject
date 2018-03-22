package com.tensynchina.hook.task;

import android.content.Context;

import com.llx278.exeventbus.ExEventBus;
import com.llx278.exeventbus.exception.TimeoutException;
import com.orhanobut.logger.Logger;
import com.tensynchina.hook.common.Constant;

/**
 *
 * Created by llx on 2018/3/22.
 */

public class Code1Task extends TaskHandler {
    private Param mParam;
    Code1Task(Param param) {
        mParam = param;
    }
    @Override
    public void handle(Context context) {
        int taskTag = mParam.getTaskTag();
        if (taskTag == 1) {
            Logger.d("准备发布杀死其他进程的事件");
            String eventObj = "killSelf";
            String tag = Constant.PROCESS_KILL_TAG;
            String returnClassName = void.class.getName();
            long timeout = 1000 * 5;
            try {
                ExEventBus.getDefault().remotePublish(eventObj,tag,returnClassName,timeout);
            } catch (TimeoutException e) {
                Logger.e(e,"");
            }
        }
    }
}

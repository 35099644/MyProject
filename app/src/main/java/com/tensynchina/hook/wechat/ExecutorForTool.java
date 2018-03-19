package com.tensynchina.hook.wechat;

import android.content.Context;

import com.llx278.exeventbus.Subscriber;
import com.llx278.exeventbus.ThreadModel;
import com.llx278.exeventbus.Type;
import com.llx278.uimocker2.Solo;
import com.orhanobut.logger.Logger;
import com.tensynchina.hook.task.Param;
import com.tensynchina.hook.task.Result;
import com.tensynchina.hook.utils.XLogger;

/**
 * 代表微信com.tencnet.mm:tools进程里任务执行的入口
 * Created by llx on 2018/3/19.
 */

public class ExecutorForTool {

    private Solo mSolo;

    public ExecutorForTool(Context context) {
        mSolo = Solo.getInstance(context);
    }

    /**
     * 微信tools进程任务执行的入口
     * @param param 任务执行参数
     * @return 执行的结果
     */
    @Subscriber(tag = "com.tencent.mm:tools_TAG", type = Type.BLOCK_RETURN, remote = true,
            model = ThreadModel.POOL)
    public Result executeTask(Param param) {
        XLogger.d("com.tencent.mm:tools收到了一个任务 : " + param.toString());
        BaseTask task = TaskToolCreator.create(param.getTaskTag());
        if (task != null) {
            XLogger.d("准备执行task");
            return task.execute(mSolo,param);
        }
        return null;
    }

}

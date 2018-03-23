package com.tensynchina.hook.task;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.alibaba.fastjson.JSON;
import com.llx278.exeventbus.ExEventBus;
import com.llx278.exeventbus.Router;
import com.llx278.exeventbus.exception.TimeoutException;
import com.orhanobut.logger.Logger;
import com.tensynchina.hook.common.Constant;
import com.tensynchina.hook.common.Message;
import com.tensynchina.hook.utils.XLogger;

/**
 *
 * Created by llx on 2018/3/22.
 */

public class Code0Task extends TaskHandler {
    private Param mParam;
    Code0Task(Param param) {
        mParam = param;
    }
    @Override
    public void handle(Context context) {
        String packageName = mParam.getPackageName();
        PackageManager packageManager = context.getPackageManager();
        Intent launchIntentForPackage = packageManager.getLaunchIntentForPackage(packageName);
        context.startActivity(launchIntentForPackage);
        try {
            // 等待1s，等待对应包名的app已经启动
            Thread.sleep(1000);
            String tag = Constant.WX_TASK_TAG;
            Logger.d("tag  : " + tag + " param : " + mParam.toString());
            String resultClassName = Result.class.getName();
            long timeout = 1000 * 60 * 5;
            Result result = (Result) ExEventBus.getDefault().remotePublish(mParam,tag,resultClassName,timeout);
            Logger.d("获得了微信的返回结果 : " + result);
            if (result != null) {
                if (result.getError().getCode() == Error.OTHER) {
                    XLogger.d("发现了一个999错误，杀死所有进程");
                    String kileventObj = "killSelf";
                    String killtag = Constant.PROCESS_KILL_TAG;
                    String killreturnClassName = void.class.getName();
                    long killtimeout = 1000 * 5;
                    ExEventBus.getDefault().remotePublish(kileventObj,killtag,killreturnClassName,killtimeout);
                }
                ResultWrapper rw = new ResultWrapper(0,result);
                String uuid = result.getUuid();
                String resultMsg = JSON.toJSONString(rw);
                Message msg = new Message(resultMsg,uuid);
                ExEventBus.getDefault().remotePublish(msg, Constant.MESSAGE_SEND_TAG,
                        void.class.getName(),1000*5);
            }
        } catch (InterruptedException ignore) {
        } catch (TimeoutException e) {
            Logger.e(e,"");
        }
    }
}
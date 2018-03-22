package com.tensynchina.hook.wechat;

import com.llx278.uimocker2.ISolo;
import com.tensynchina.hook.task.Param;
import com.tensynchina.hook.task.Result;
import com.tensynchina.hook.utils.XLogger;

/**
 *
 * Created by llx on 2018/3/22.
 */

public class Task5 extends BaseTask {

    @Override
    public Result execute(ISolo solo, Param param) {
        XLogger.d("执行Task5");
        // Task5什么都不需要做，只是打开，等待数据库的插入操作
        solo.littleSleep(15);
        solo.getActivityUtils().finishOpenedActivities();
        return null;
    }
}

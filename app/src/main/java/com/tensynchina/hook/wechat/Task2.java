package com.tensynchina.hook.wechat;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.llx278.exeventbus.ExEventBus;
import com.llx278.exeventbus.exception.TimeoutException;
import com.llx278.uimocker2.ISolo;
import com.tensynchina.hook.task.Error;
import com.tensynchina.hook.task.Param;
import com.tensynchina.hook.task.Result;
import com.tensynchina.hook.utils.WaiterUtils;
import com.tensynchina.hook.utils.XLogger;

/**
 *
 * Created by llx on 2018/3/20.
 */

public class Task2 extends BaseTask {

    @Override
    public Result execute(ISolo solo, Param param) {
        Result result = new Result();
        try {
            result.setPackageName(param.getPackageName());
            result.setUuid(param.getAddressUuid());
            result.setTaskTag(param.getTaskTag());
            result.setTaskId(param.getTaskId());

            solo = new SoloForTask(solo);

            WeChatTask2 wt2 = JSON.parseObject(param.getJson(),WeChatTask2.class);
            String key = wt2.getKeyDes();
            if (TextUtils.isEmpty(key)) {
                result.setError(new Error(Error.KEY_EMPTY,"空关键词!"));
                return result;
            }

            solo.littleSleep();
            View searchIcon = solo.findViewById(1);
            if (searchIcon == null) {
                result.setError(new Error(Error.LAYOUT_ERROR,"没有找到搜索按钮!"));
                return result;
            }
            solo.littleSleep();
            solo.getClicker().clickOnView(searchIcon);
            View articleView = solo.getWaiter().waitForTextAppearAndGet("^文章$");
            if (articleView == null) {
                result.setError(new Error(Error.LAYOUT_ERROR,"没有找到文章按钮"));
                return result;
            }
            solo.littleSleep();
            solo.getClicker().clickOnView(articleView);

            // 等待tools进程的结果
            String processName = WConstant.WX_TOOLS_PROCESS;
            Context context = solo.getContext();
            long timeout = 1000 * 15;
            boolean hasCreated = WaiterUtils.waitForProcessCreated(context, processName, timeout);
            if (!hasCreated) {
                result.setError(new Error(Error.LAYOUT_ERROR,"没有等到" + WConstant.WX_TOOLS_PROCESS + "启动"));
                return result;
            }
            solo.littleSleep(5);
            // 点击公众号结束以后，再弹出的界面就进入了com.tencent.mm:tools进程
            XLogger.d("准备向tool进程发送消息");

            String tag = WConstant.TOOLS_TAG;
            String returnName = Result.class.getName();
            result = (Result) ExEventBus.getDefault().remotePublish(param, tag, returnName, 1000 * 60 * 15);
            solo.littleSleep(5);
        } catch (TimeoutException e) {
            result.setError(new Error(Error.TIME_OUT,"等待微信tools进程执行结果超时"));
        } catch (Exception e) {
            result.setError(new Error(Error.OTHER,e.getMessage()));
        } finally {
            solo.getActivityUtils().finishOpenedActivities();
        }
        return result;
    }
}

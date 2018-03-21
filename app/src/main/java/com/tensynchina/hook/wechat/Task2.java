package com.tensynchina.hook.wechat;

import android.text.TextUtils;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.llx278.exeventbus.ExEventBus;
import com.llx278.exeventbus.exception.TimeoutException;
import com.llx278.uimocker2.ISolo;
import com.tensynchina.hook.task.Error;
import com.tensynchina.hook.task.Param;
import com.tensynchina.hook.task.Result;

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

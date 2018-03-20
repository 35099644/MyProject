package com.tensynchina.hook.wechat;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.llx278.exeventbus.ExEventBus;
import com.llx278.uimocker2.Clicker;
import com.llx278.uimocker2.ISolo;
import com.tensynchina.hook.task.Error;
import com.tensynchina.hook.task.Param;
import com.tensynchina.hook.task.Result;
import com.tensynchina.hook.utils.XLogger;


/**
 * 主动关注公众号
 * Created by llx on 2018/3/16.
 */

public class Task1 extends BaseTask {

    @Override
    public Result execute(ISolo solo, Param param) {
        Result result = new Result();
        try {
            XLogger.d("执行com.tennet.mm的task1");
            result.setPackageName(param.getPackageName());
            result.setTaskId(param.getTaskId());
            result.setTaskTag(param.getTaskTag());
            result.setUuid(param.getAddressUuid());
            WeChatTask1 wtt1 = JSON.parseObject(param.getJson(),WeChatTask1.class);
            final String name = wtt1.getKeyDes().get(0);

            if (TextUtils.isEmpty(name)) {
                result.setError(new Error(Error.KEY_EMPTY,"公众号名为空!!"));
                return result;
            }
            solo = new SoloForTask(solo);
            Thread.sleep(1000);
            View searchIcon = solo.findViewById(1);
            if (searchIcon == null) {
                result.setError(new Error(Error.LAYOUT_ERROR,"没有找到搜索按钮"));
                return result;
            }

            XLogger.d("点击搜索按钮");
            solo.getClicker().clickOnView(searchIcon);
            View biz = solo.getWaiter().waitForTextAppearAndGet("^公众号$", 1000 * 20);
            XLogger.d("点击公众号按钮");
            solo.getClicker().clickOnView(biz);
            Thread.sleep(3000);
            // 点击公众号结束以后，再弹出的界面就进入了com.tencent.mm:tools进程
            String tag = ExecutorForTool.TOOLS_TAG;
            ExEventBus.getDefault().
                    remotePublish(param, tag, Result.class.getName(), 1000 * 60);
            solo.getActivityUtils().waitForOnResume(WConstant.ACTIVITY_CONTACT_UI,1000 * 5,0);
            TextView view = solo.getWaiter().waitForTextViewAppearAndGet("^关注$", 1000 * 5);
            if (view == null) {
                // 公众号已经关注了
                result.setError(new Error(Error.DUPLICATE_ATTENTION_OFFICE_ACCOUNT,"此公众号已经被关注"));
            } else {
                Clicker clicker = solo.getClicker();
                clicker.clickOnView(view);
                if(solo.getActivityUtils().waitForOnResume(WConstant.ACTIVITY_CHATTING_UI,1000 * 30,0)){
                    result.setData("{\"success\"}");
                } else {
                    Activity currentActivity = solo.getActivityUtils().getCurrentActivity();
                    XLogger.d("currentActivity = " + currentActivity.getClass().getName());
                    result.setError(new Error(Error.ACCOUNT_FOLLOW_FAILED,"关注公众号失败!"));
                }
            }
            solo.sleep(1000);
            return result;
        } catch (Exception e) {
            XLogger.e(e);
            result.setError(new Error(Error.OTHER,e.getMessage()));
            return result;
        } finally {
            solo.getActivityUtils().finishOpenedActivities();
        }
    }
}

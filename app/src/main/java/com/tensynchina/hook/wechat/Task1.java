package com.tensynchina.hook.wechat;

import android.view.View;
import android.widget.TextView;

import com.llx278.exeventbus.ExEventBus;
import com.llx278.uimocker2.Clicker;
import com.llx278.uimocker2.Solo;
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
    public Result execute(Solo solo,Param param) {

        XLogger.d("执行com.tennet.mm的task1");
        try {
            Thread.sleep(1000);
            View searchIcon = solo.findViewById(1);
            if (searchIcon == null) {
                return null;
            }

            XLogger.d("点击搜索按钮");
            solo.getClicker().clickOnView(searchIcon);
            View biz = solo.getWaiter().waitForTextAppearAndGet("^公众号$", 1000 * 20);
            XLogger.d("点击公众号按钮");
            solo.getClicker().clickOnView(biz);
            Thread.sleep(3000);
            // 点击公众号结束以后，再弹出的界面就进入了com.tencent.mm:tools进程
            String tag = "com.tencent.mm:tools_TAG";
            ExEventBus.getDefault().
                    remotePublish(param, tag, Result.class.getName(), 1000 * 60);
            solo.getActivityUtils().waitForOnResume(Constant.ACTIVITY_CONTACT_UI,1000 * 5,0);
            TextView view = solo.getWaiter().waitForTextViewAppearAndGet("^关注$", 1000 * 5);
            Result result = new Result();
            result.setPackageName(param.getPackageName());
            result.setTaskId(param.getTaskId());
            result.setTaskTag(param.getTaskTag());
            result.setUuid(param.getAddressUuid());
            if (view == null) {
                // 公众号已经关注了
                result.setError(new Error(Error.DUPLICATE_ATTENTION_OFFICE_ACCOUNT,"此公众号已经被关注"));
            } else {
                Clicker clicker = solo.getClicker();
                clicker.clickOnView(view);
                result.setData("{\"success\"}");
            }
            solo.getActivityUtils().waitForOnResume(Constant.ACTIVITY_EN_5B8FBB1E,1000 * 5,0);
            solo.sleep(1000);
            solo.getActivityUtils().finishOpenedActivities();
            return result;
        } catch (Exception e) {
            XLogger.e(e);
        }

        return null;
    }
}

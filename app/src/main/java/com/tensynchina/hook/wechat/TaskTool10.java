package com.tensynchina.hook.wechat;

import android.app.Activity;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.llx278.uimocker2.By;
import com.llx278.uimocker2.ISolo;
import com.llx278.uimocker2.WebElement;
import com.tensynchina.hook.task.Error;
import com.tensynchina.hook.task.Param;
import com.tensynchina.hook.task.Result;
import com.tensynchina.hook.utils.XLogger;

import java.util.ArrayList;

import de.robv.android.xposed.XposedBridge;

/**
 * Created by llx on 2018/3/23.
 */

public class TaskTool10 extends BaseTask {
    @Override
    public Result execute(ISolo soloparam, Param param) {

        XLogger.d("进入TaskTool10");
        final ISolo solo = new SoloForTaskTool(soloparam);
        Result result = new Result();
        result.setPackageName(param.getPackageName());
        result.setTaskTag(param.getTaskTag());
        result.setTaskId(param.getTaskId());
        result.setUuid(param.getAddressUuid());
        try {

            final WeChatTask10 wt10 = JSON.parseObject(param.getJson(),WeChatTask10.class);
            final String webVieName = "com.tencent.smtt.sdk.WebView$SystemWebView";
            ArrayList<View> views = solo.getWaiter().waitForViewListAppearAndGet(webVieName, null);
            if (views == null || views.isEmpty()) {
                result.setError(new Error(Error.LAYOUT_ERROR,"没有找到 : " + webVieName));
                return result;
            }

            final View view = views.get(0);
            solo.runOnMainSync(new Runnable() {
                @Override
                public void run() {
                    try {
                        solo.getWebUtils().loadUrl(view,wt10.getUrl());
                    } catch (Exception e) {
                        XLogger.e(e);
                    }
                }
            });
            Thread.sleep(1000 * 5);
            By id = By.id("post-user");
            ArrayList<WebElement> webElementList = solo.getWebUtils().getWebElementList(id, false, view);

            solo.getWebUtils().clickOnWebElement(id,view);
            Activity currentActivity = solo.getActivityUtils().getCurrentActivity();
            String activityName = currentActivity.getClass().getName();
            boolean b = solo.getActivityUtils().waitForOnPause(activityName, 1000 * 5, 0);
            if (!b) {
                result.setError(new Error(Error.LAYOUT_ERROR,"等待：" + activityName + " onPasue超时"));
                return result;
            }

            result.setData(webElementList.get(0).getText());
            solo.littleSleep(5);
            return result;
        } catch (Exception e) {
            XLogger.e(e);
            result.setError(new Error(Error.LAYOUT_ERROR,e.getMessage()));
            return result;
        } finally {
            solo.getActivityUtils().finishOpenedActivities();
        }
    }
}

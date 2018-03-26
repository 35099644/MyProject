package com.tensynchina.hook.wechat;

import android.view.View;

import com.alibaba.fastjson.JSON;
import com.llx278.uimocker2.By;
import com.llx278.uimocker2.ISolo;
import com.llx278.uimocker2.WebElement;
import com.tensynchina.hook.common.Constant;
import com.tensynchina.hook.task.Error;
import com.tensynchina.hook.task.Param;
import com.tensynchina.hook.task.Result;
import com.tensynchina.hook.utils.IOUtils;
import com.tensynchina.hook.utils.XLogger;

import java.util.ArrayList;

/**
 *
 * Created by llx on 2018/3/21.
 */

public class TaskTool4 extends BaseTask {
    @Override
    public Result execute(ISolo soloparam, Param param) {
        XLogger.d("进入TaskTool4");
        final ISolo solo = new SoloForTaskTool(soloparam);
        Result result = new Result();
        result.setPackageName(param.getPackageName());
        result.setTaskTag(param.getTaskTag());
        result.setTaskId(param.getTaskId());
        result.setUuid(param.getAddressUuid());

        try {
            final String webVieName = "com.tencent.smtt.sdk.WebView$SystemWebView";
            ArrayList<View> views = solo.getWaiter().waitForViewListAppearAndGet(webVieName, null);
            if (views == null || views.isEmpty()) {
                result.setError(new Error(Error.LAYOUT_ERROR,"没有找到 : " + webVieName));
                return result;
            }

            final View view = views.get(0);
            Thread.sleep(1000 * 5);
            By tagName = By.tagName("html");
            ArrayList<WebElement> webElementList = solo.getWebUtils().
                    getWebElementList(tagName,
                            false, view);
            WebElement webElement = webElementList.get(0);
            String html = "<html>"+webElement.getInnerHtml() + "</html>";
            IOUtils.stringToFile(false,html, Constant.RESULT_LOCAL_PATH);
            result.setData(Constant.RESULT_LOCAL_PATH);
            return result;
        } catch (Exception e) {
            result.setError(new Error(Error.OTHER,e.getMessage()));
            return result;
        }finally {
            solo.getActivityUtils().finishOpenedActivities();
        }
    }
}

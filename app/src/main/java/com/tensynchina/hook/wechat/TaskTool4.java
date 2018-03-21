package com.tensynchina.hook.wechat;

import android.view.View;
import android.webkit.WebView;

import com.alibaba.fastjson.JSON;
import com.llx278.exeventbus.ExEventBus;
import com.llx278.uimocker2.By;
import com.llx278.uimocker2.Filter;
import com.llx278.uimocker2.ISolo;
import com.llx278.uimocker2.Solo;
import com.llx278.uimocker2.WebElement;
import com.tensynchina.hook.Constant;
import com.tensynchina.hook.task.Error;
import com.tensynchina.hook.task.Param;
import com.tensynchina.hook.task.Result;
import com.tensynchina.hook.utils.IOUtils;
import com.tensynchina.hook.utils.XLogger;

import java.util.ArrayList;

/**
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
            final WeChatTask4 wt4 = JSON.parseObject(param.getJson(),WeChatTask4.class);
            final String webVieName = "com.tencent.smtt.sdk.WebView$SystemWebView";
            final View view = solo.getSearcher().searchViewByFilter(webVieName, null, new Filter() {
                @Override
                public boolean match(View view) {
                    return view.getClass().getName().equals(webVieName);
                }
            }, true);
            solo.runOnMainSync(new Runnable() {
                @Override
                public void run() {
                    try {
                        solo.getWebUtils().loadUrl(view,wt4.getUrl());
                    } catch (Exception e) {
                        XLogger.e(e);
                    }
                }
            });
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

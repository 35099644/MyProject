package com.tensynchina.hook.wechat;

import android.view.View;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;
import com.llx278.uimocker2.By;
import com.llx278.uimocker2.Filter;
import com.llx278.uimocker2.ISolo;
import com.llx278.uimocker2.WebElement;
import com.tensynchina.hook.task.Param;
import com.tensynchina.hook.task.Result;
import com.tensynchina.hook.utils.XLogger;

import java.util.ArrayList;


/**
 * Created by llx on 2018/3/19.
 */

public class TaskTool1 extends BaseTask {

    @Override
    public Result execute(ISolo soloArg, final Param param) {

        try {
            final  ISolo solo = new SoloForTaskTool(soloArg);
            WeChatTask1 wtt1 = JSON.parseObject(param.getJson(),WeChatTask1.class);
            final String name = wtt1.getKeyDes().get(0);
            final EditText editText = solo.getWaiter().waitForEditTextAppearAndGet("^搜索公众号$",1000 * 20);
            solo.runOnMainSync(new Runnable() {
                @Override
                public void run() {
                    try {
                        editText.setText(name);
                    } catch (Exception e) {
                        XLogger.e(e);
                    }
                }
            });
            solo.runOnMainSync(new Runnable() {
                @Override
                public void run() {
                    try {
                        solo.mockSoftKeyBordSearchButton(editText);
                    } catch (Exception e) {
                        XLogger.e(e);
                    }
                }
            });
            Thread.sleep(5000);
            XLogger.d("获取此页面的源码");
            final String webVieName = "com.tencent.smtt.sdk.WebView$SystemWebView";
            View view = solo.getSearcher().searchViewByFilter(webVieName, null, new Filter() {
                @Override
                public boolean match(View view) {
                    return view.getClass().getName().equals(webVieName);
                }
            }, true);
            By title = By.attribute("title",name);
            ArrayList<WebElement> webElementList1 = solo.getWebUtils().getWebElementList(title, false, view);
            WebElement web = webElementList1.get(0);
            solo.getClicker().clickOnScreen(web.getLocationX(),web.getLocationY());
            Thread.sleep(1500);
            solo.getActivityUtils().finishOpenedActivities();
        } catch (Exception e) {
            XLogger.e(e);
        }
        return null;
    }
}

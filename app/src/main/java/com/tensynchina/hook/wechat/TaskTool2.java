package com.tensynchina.hook.wechat;

import android.os.SystemClock;
import android.view.View;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;
import com.llx278.uimocker2.By;
import com.llx278.uimocker2.Filter;
import com.llx278.uimocker2.ISolo;
import com.llx278.uimocker2.WebElement;
import com.tensynchina.hook.common.Constant;
import com.tensynchina.hook.task.Error;
import com.tensynchina.hook.task.Param;
import com.tensynchina.hook.task.Result;
import com.tensynchina.hook.utils.IOUtils;
import com.tensynchina.hook.utils.XLogger;

import java.util.ArrayList;
import java.util.Random;

/**
 *
 * Created by llx on 2018/3/20.
 */

public class TaskTool2 extends BaseTask{
    @Override
    public Result execute(ISolo soloArg, final Param param) {
        XLogger.d("执行 TaskTool2");
        final ISolo solo = new SoloForTaskTool(soloArg);
        Result result = new Result();
        try {
            result.setPackageName(param.getPackageName());
            result.setTaskId(param.getTaskId());
            result.setTaskTag(param.getTaskTag());
            result.setUuid(param.getAddressUuid());

            Random random = new Random(SystemClock.elapsedRealtime());
            int i = random.nextInt(3);
            // 最4分钟 最长7分钟
            long sleepTime = 1000 * 60 * 4 + 1000 * 60 * i;
            solo.sleep(1000);
            final EditText editText = solo.getWaiter().waitForEditTextAppearAndGet("^搜索资讯$");
            if (editText == null) {
                result.setError(new Error(Error.LAYOUT_ERROR,"没有找到搜索资讯的EditText"));
                return result;
            }
            WeChatTask2 wt2 = JSON.parseObject(param.getJson(),WeChatTask2.class);
            final String keyDes = wt2.getKeyDes();
            solo.runOnMainSync(new Runnable() {
                @Override
                public void run() {
                    editText.setText(keyDes);
                }
            });
            solo.littleSleep(2);
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
            final String webVieName = "com.tencent.smtt.sdk.WebView$SystemWebView";
            View view = solo.getSearcher().searchViewByFilter(webVieName, null, new Filter() {
                @Override
                public boolean match(View view) {
                    return view.getClass().getName().equals(webVieName);
                }
            }, true);
            solo.littleSleep(5);
            By tagName = By.tagName("html");
            ArrayList<WebElement> webElementList = solo.getWebUtils().
                    getWebElementList(tagName, false, view);
            WebElement webElement = webElementList.get(0);
            String html = "<html>"+webElement.getInnerHtml() + "</html>";
            IOUtils.stringToFile(false,html, Constant.RESULT_LOCAL_PATH);
            result.setData(Constant.RESULT_LOCAL_PATH);
            return result;
        } catch (Exception e) {
            result.setError(new Error(Error.OTHER,e.getMessage()));
            return result;
        } finally {
            solo.getActivityUtils().finishOpenedActivities();
        }
    }
}

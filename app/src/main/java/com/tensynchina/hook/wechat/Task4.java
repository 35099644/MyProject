package com.tensynchina.hook.wechat;

import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.llx278.exeventbus.ExEventBus;
import com.llx278.uimocker2.Filter;
import com.llx278.uimocker2.ISolo;
import com.llx278.uimocker2.ReflectUtil;
import com.tensynchina.hook.task.Error;
import com.tensynchina.hook.task.Param;
import com.tensynchina.hook.task.Result;
import com.tensynchina.hook.utils.XLogger;

import java.util.ArrayList;

/**
 *
 * Created by llx on 2018/3/21.
 */

public class Task4 extends BaseTask {

    @Override
    public Result execute(ISolo solo, Param param) {
        XLogger.d("进入task4");
        solo = new SoloForTask(solo);
        Result result = new Result();
        result.setPackageName(param.getPackageName());
        result.setTaskId(param.getTaskId());
        result.setTaskTag(param.getTaskTag());
        result.setUuid(param.getAddressUuid());
        try {
            View view = null;
            solo.littleSleep(15);
            long endTime = SystemClock.uptimeMillis() + 1000 * 20;
            while (SystemClock.uptimeMillis() < endTime) {
                view = solo.getSearcher().searchViewByFilter("com.tencent.mm.ui.base.NoMeasuredTextView", null, new Filter() {
                    @Override
                    public boolean match(View view) {

                        ArrayList<String> customViewText = ReflectUtil.getCustomViewText(view, null);
                        for (String str : customViewText) {
                            for (String nickName : WConstant.NICK_NAME_LIST) {
                                if (TextUtils.equals(nickName, str)) {
                                    return true;
                                }
                            }
                        }
                        return false;
                    }
                }, true);
                if (view != null) {
                    break;
                }
            }
            if (view == null) {
                result.setError(new Error(Error.LAYOUT_ERROR,
                        "没有在主页面上找到此微信指定的昵称,当前可用昵称:" + WConstant.NICK_NAME_LIST));
                return result;
            }
            solo.littleSleep(1);
            solo.getClicker().clickOnView(view);
            solo.littleSleep();
            View item = solo.getWaiter().waitForTextAppearAndGet("https://");
            if (item == null) {
                result.setError(new Error(Error.LAYOUT_ERROR,"没有找到https://www.baidu.com，对应的按钮"));
                return result;
            }
            WeChatTask4 wt4 = JSON.parseObject(param.getJson(),WeChatTask4.class);
            XLogger.d("先将url发送到tools进程");
            String activityName = "com.tencent.mm.plugin.webview.ui.tools.WebViewUI";
            ReplaceUrlEvent event = new ReplaceUrlEvent(activityName,wt4.getUrl());
            //ExEventBus.getDefault().stickyRemotePublish(event, WConstant.TOOLS_REPLACE_URL,1000 * 15);
            solo.littleSleep();
            XLogger.d("准备click");
            View viewById = solo.findViewById(0x7f10017a);
            solo.getClicker().clickOnView(viewById);
            solo.littleSleep();
            String returnClassName = Result.class.getName();
            result = (Result) ExEventBus.getDefault().remotePublish(param, WConstant.TOOLS_TAG,
                    returnClassName, 1000 * 60);
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

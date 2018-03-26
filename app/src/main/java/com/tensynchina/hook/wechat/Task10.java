package com.tensynchina.hook.wechat;

import android.content.Context;
import android.os.SystemClock;
import android.view.View;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.llx278.exeventbus.ExEventBus;
import com.llx278.uimocker2.ISolo;
import com.tensynchina.hook.task.Error;
import com.tensynchina.hook.task.Param;
import com.tensynchina.hook.task.Result;
import com.tensynchina.hook.utils.IOUtils;
import com.tensynchina.hook.utils.RegexUtils;
import com.tensynchina.hook.utils.WaiterUtils;
import com.tensynchina.hook.utils.XLogger;

import java.util.ArrayList;

/**
 *
 * Created by llx on 2018/3/23.
 */

public class Task10 extends BaseTask {
    @Override
    public Result execute(ISolo solo, Param param) {
        XLogger.d("执行task10任务");
        solo = new SoloForTask(solo);
        Result result = new Result();
        result.setPackageName(param.getPackageName());
        result.setTaskId(param.getTaskId());
        result.setTaskTag(param.getTaskTag());
        result.setUuid(param.getAddressUuid());

        try {
            String paramStr = JSON.toJSONString(param);
            IOUtils.stringToFile(false,paramStr,WConstant.REPLACEABLE_URL);
            ArrayList<ListView> listViews = solo.getWaiter().waitForViewListAppearAndGet(ListView.class, true);
            ListView listView = listViews.get(0);
            long endTime = SystemClock.uptimeMillis() + 1000 * 20;
            View nickNameView = null;
            while (SystemClock.uptimeMillis() < endTime) {
                solo.sleep(1000);
                String className = "com.tencent.mm.ui.base.NoMeasuredTextView";
                String regex = RegexUtils.toRegexStr(WConstant.NICK_NAME_LIST);
                nickNameView = solo.getSearcher().forceSearchViewByTextAndClassName(className, listView, regex, true);
                if (nickNameView != null) {
                    XLogger.d("nickNameView: " + nickNameView);
                    break;
                }
            }
            if (nickNameView == null) {
                result.setError(new Error(Error.LAYOUT_ERROR,
                        "没有在主页面上找到此微信指定的昵称,当前可用昵称:" + WConstant.NICK_NAME_LIST));
                return result;
            }
            solo.littleSleep();
            solo.getClicker().clickOnView(nickNameView);
            solo.littleSleep();
            View item = solo.getWaiter().waitForTextAppearAndGet("https://");
            if (item == null) {
                result.setError(new Error(Error.LAYOUT_ERROR,"没有找到https://www.google.com，对应的按钮"));
                return result;
            }
            solo.littleSleep();
            View viewById = solo.findViewById(0x7f10017a);
            solo.getClicker().clickOnView(viewById);

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

            String returnClassName = Result.class.getName();
            result = (Result) ExEventBus.getDefault().remotePublish(param, WConstant.TOOLS_TAG,
                    returnClassName, 1000 * 60);
            View followView = solo.getWaiter().waitForTextAppearAndGet("^关注$");
            if (followView == null) {
                result.setData("{\\\"status\\\":\\\"1\\\",\\\"account\\\":\\\"nickname\\\"}");
                return result;
            }
            solo.getClicker().clickOnView(followView);
            solo.littleSleep();
            if (!solo.getActivityUtils().waitForOnResume(WConstant.ACTIVITY_CHATTING_UI,1000 * 5,0)) {
                result.setData("{\\\"status\\\":\\\"1\\\",\\\"account\\\":\\\"nickname\\\"}");
                return result;
            }
            solo.littleSleep(2);
            result.setData("{\\\"status\\\":\\\"0\\\",\\\"account\\\":\\\""+result.getData()+"\\\"}");
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

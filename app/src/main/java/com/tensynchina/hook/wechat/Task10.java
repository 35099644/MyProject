package com.tensynchina.hook.wechat;

import android.os.SystemClock;
import android.view.View;
import android.widget.ListView;

import com.llx278.exeventbus.ExEventBus;
import com.llx278.uimocker2.ISolo;
import com.tensynchina.hook.task.Error;
import com.tensynchina.hook.task.Param;
import com.tensynchina.hook.task.Result;
import com.tensynchina.hook.utils.RegexUtils;
import com.tensynchina.hook.utils.XLogger;

import java.util.ArrayList;

/**
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
                result.setError(new Error(Error.LAYOUT_ERROR,"没有找到https://www.baidu.com，对应的按钮"));
                return result;
            }
            solo.littleSleep();
            View viewById = solo.findViewById(0x7f10017a);
            solo.getClicker().clickOnView(viewById);
            solo.littleSleep();
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
            result.setData("{\\\"status\\\":\\\"0\\\",\\\"account\\\":\\\""+result.getData()+"\\\"}");
            solo.littleSleep();
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

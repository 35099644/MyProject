package com.tensynchina.hook.wechat;

import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.llx278.exeventbus.ExEventBus;
import com.llx278.exeventbus.exception.TimeoutException;
import com.llx278.uimocker2.ISolo;
import com.tensynchina.hook.task.Error;
import com.tensynchina.hook.task.Param;
import com.tensynchina.hook.task.Result;
import com.tensynchina.hook.utils.XLogger;

import java.util.ArrayList;

/**
 * Created by llx on 2018/3/20.
 */

public class Task3 extends BaseTask {

    @Override
    public Result execute(ISolo solo, Param param) {
        solo = new SoloForTask(solo);
        Result result = new Result();
        result.setPackageName(param.getPackageName());
        result.setTaskId(param.getTaskId());
        result.setUuid(param.getAddressUuid());
        result.setTaskTag(param.getTaskTag());
        try {
            WeChatTask3 wt3 = JSON.parseObject(param.getJson(),WeChatTask3.class);
            String key = wt3.getKeyDes();
            if (TextUtils.isEmpty(key)) {
                result.setError(new Error(Error.KEY_EMPTY,"公众号名为空"));
                return result;
            }

            solo.littleSleep();
            boolean clickSuccess = solo.waitForTextAndClick("^通讯录$");
            if (!clickSuccess) {
                result.setError(new Error(Error.LAYOUT_ERROR,"点击通讯录标签失败"));
                return result;
            }
            solo.littleSleep();
            solo.waitForTextAndClick("^公众号$");
            solo.littleSleep();
            ArrayList<ListView> listViews = solo.getWaiter().waitForViewListAppearAndGet(ListView.class, true);
            ListView listView = listViews.get(0);
            solo.littleSleep();
            View view = solo.getWaiter().waitForTextAppearWithVerticallyScrollAndGet("^" + key + "$",listView);
            if (view == null) {
                result.setError(new Error(Error.OFFICE_ACCOUNT_IS_NOT_ATTENTION,"此公众号没有被关注"));
                return result;
            }
            solo.getClicker().clickOnView(view);
            solo.littleSleep();
            // 避免异常弹出地理位置对话框
            solo.getClicker().clickOnScreen(354.5f,68.9f);
            solo.littleSleep();
            solo.getClicker().clickOnScreen(648.0f,74.0f);
            View historyView = solo.getWaiter().waitForTextAppearAndGet("^查看历史消息$");
            if (historyView == null) {
                result.setError(new Error(Error.LAYOUT_ERROR,"没有找到查看历史消息按钮"));
                return result;
            }
            solo.littleSleep();
            solo.getClicker().clickOnView(historyView);
            solo.littleSleep(5);
            // 进入了tool进程
            String tag = WConstant.TOOLS_TAG;
            String returnName = Result.class.getName();
            result = (Result) ExEventBus.getDefault().remotePublish(param, tag, returnName, 1000 * 60);
            solo.littleSleep();
            return result;
        } catch (TimeoutException e) {
            XLogger.e(e);
            result.setError(new Error(Error.TIME_OUT,"等待历史消息页面返回数据超时"));
            return result;
        } catch (Exception e){
            result.setError(new Error(Error.OTHER,e.getMessage()));
            return result;
        }finally {
            solo.getActivityUtils().finishOpenedActivities();
        }
    }
}

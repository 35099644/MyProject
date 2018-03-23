package com.tensynchina.hook.wechat;

import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.llx278.uimocker2.ISolo;
import com.tensynchina.hook.task.Error;
import com.tensynchina.hook.task.Param;
import com.tensynchina.hook.task.Result;

import java.util.ArrayList;

/**
 * Created by llx on 2018/3/23.
 */

public class Task8 extends BaseTask {
    @Override
    public Result execute(ISolo solo, Param param) {
        solo = new SoloForTask(solo);
        Result result = new Result();
        result.setPackageName(param.getPackageName());
        result.setTaskId(param.getTaskId());
        result.setTaskTag(param.getTaskTag());
        result.setUuid(param.getAddressUuid());
        try {

            WeChatTask8 wt8 = JSON.parseObject(param.getJson(),WeChatTask8.class);
            if (wt8.getKeyDes() == null || wt8.getKeyDes().isEmpty()) {
                result.setError(new Error(Error.KEY_EMPTY,"取消公众号的列表为空!"));
                return result;
            }

            solo.waitForTextAndClick("^微信$");
            solo.littleSleep();
            solo.waitForTextAndClick("^通讯录$");
            boolean success = solo.waitForTextAndClick("^公众号$");
            if (!success) {
                result.setError(new Error(Error.LAYOUT_ERROR,"没有找到公众号按钮"));
                return result;
            }
            solo.littleSleep();
            ArrayList<ListView> listViews = solo.getWaiter().waitForViewListAppearAndGet(ListView.class,true);
            if (listViews == null || listViews.isEmpty()) {
                result.setError(new Error(Error.LAYOUT_ERROR,"没有找到listView"));
                return result;
            }
            ListView listView = listViews.get(0);
            for (String key:wt8.getKeyDes()) {
                if (TextUtils.isEmpty(key)) {
                    continue;
                }
                View view = solo.getWaiter().waitForTextAppearWithVerticallyScrollAndGet(
                        "^" + key + "$", listView);
                if (view == null) {
                    result.setError(new Error(Error.LAYOUT_ERROR,"在公众号列表上没有找到名为'"+key+"'的公众号"));
                    return result;
                }
                solo.littleSleep();
                solo.getClicker().longClickOnView(view,1000 * 2);
                solo.littleSleep();
                boolean open = solo.getDialogUtils().waitForDialogToOpen(1000 * 5);
                if (!open) {
                    result.setError(new Error(Error.LAYOUT_ERROR,"打开'取消关注'对话框失败"));
                    return result;
                }
                solo.littleSleep();
                View cancelViewFirst = solo.getWaiter().waitForTextAppearAndGet("^取消关注$");
                if (cancelViewFirst == null) {
                    result.setError(new Error(Error.LAYOUT_ERROR,"没有找到'取消关注'按钮"));
                    return result;
                }
                solo.littleSleep();
                solo.getClicker().clickOnView(cancelViewFirst);
                solo.littleSleep();
                View cancelViewSecond = solo.getWaiter().waitForTextAppearAndGet("^不再关注$");
                solo.littleSleep();
                solo.getClicker().clickOnView(cancelViewSecond);
                boolean close = solo.getDialogUtils().waitForDialogToClose(1000 * 5);
                if (!close) {
                    result.setError(new Error(Error.LAYOUT_ERROR,"关闭'不再关注'对话框失败"));
                    return result;
                }
            }
            result.setData("{\"status\":\"0\"}");
            return result;
        } catch (Exception e) {
            result.setError(new Error(Error.OTHER,e.getMessage()));
            return result;
        } finally {
            solo.getActivityUtils().finishOpenedActivities();
        }
    }
}

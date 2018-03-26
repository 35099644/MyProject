package com.tensynchina.hook.wechat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.alibaba.fastjson.JSON;
import com.llx278.uimocker2.ActivityLifeCycleObserverImpl;
import com.tensynchina.hook.task.Param;
import com.tensynchina.hook.utils.IOUtils;
import com.tensynchina.hook.utils.XLogger;

import java.io.IOException;

/**
 * Created by llx on 2018/3/26.
 */

public class WebActivityObserver extends ActivityLifeCycleObserverImpl {


    @Override
    public void beforeOnCreate(Activity activity, Bundle icicle) {
        super.beforeOnCreate(activity, icicle);
        try {
            String paramStr = IOUtils.fileToString(WConstant.REPLACEABLE_URL);
            Param param = JSON.parseObject(paramStr,Param.class);
            if (param == null) {
                return;
            }

            if (param.getTaskTag() == 4) {
                String json = param.getJson();
                WeChatTask4 wtt4 = JSON.parseObject(json,WeChatTask4.class);
                String rawUrl = wtt4.getUrl();
                Intent intent = activity.getIntent();
                Bundle extras = intent.getExtras();
                if (extras != null) {
                    extras.putString("rawUrl",rawUrl);
                    intent.putExtras(extras);
                }
                return;
            }

            if (param.getTaskTag() == 10) {

                String json = param.getJson();
                WeChatTask10 wtt10 = JSON.parseObject(json,WeChatTask10.class);
                String rawUrl = wtt10.getUrl();
                Intent intent = activity.getIntent();
                Bundle extras = intent.getExtras();
                if (extras != null) {
                    extras.putString("rawUrl",rawUrl);
                    intent.putExtras(extras);
                }
            }


        } catch (Exception ignore) {
        }
    }
}

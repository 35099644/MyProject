package com.tensynchina.hook;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import com.tensynchina.hook.utils.XLogger;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by llx on 2018/3/26.
 */

class NoxLoader {
    private int count = 0;
    void load(XC_LoadPackage.LoadPackageParam lpparam) {
        XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                count++;
                if (count == 1) {
                    final Context context = (Context) param.args[0];
                    final Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Intent messageServiceIntent = new Intent("com.tensynchina.Message");
                            messageServiceIntent.setComponent(new ComponentName("com.tensynchina.hook","com.tensynchina.hook.push.MessageService"));
                            context.startService(messageServiceIntent);
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent taskServiceIntent = new Intent("com.tensynchina.Task");
                                    taskServiceIntent.setComponent(new ComponentName("com.tensynchina.hook","com.tensynchina.hook.task.TaskService"));
                                    context.startService(taskServiceIntent);
                                    XLogger.d("启动服务结束");
                                }
                            },1000);
                        }
                    });
                }
            }
        });
    }
}

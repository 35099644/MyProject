package com.tensynchina.hook;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.llx278.exeventbus.ExEventBus;
import com.tensynchina.hook.utils.XLogger;
import com.tensynchina.hook.wechat.ExecutorForMain;
import com.tensynchina.hook.wechat.ExecutorForTool;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 *
 * Created by llx on 2018/3/15.
 */

public class XposedHookLoadPackage implements IXposedHookLoadPackage {

    private static final String WE_CHAT = "com.tencent.mm";
    private ExecutorForMain mExecutorForMain;
    private ExecutorForTool mExecutorForTool;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (lpparam.packageName.equals(WE_CHAT)) {
            if ("com.tencent.mm".equals(lpparam.processName)) {
                XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class,
                        new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        // 注册exeventbus
                        XLogger.d("进入 com.tencent.mm");
                        new Thread(){
                            @Override
                            public void run() {
                                XLogger.d("在com.tencent.mm进程内部注册ExEventBus!!!");
                                Context context = (Context) param.args[0];
                                ExEventBus.create(context);
                                mExecutorForMain = new ExecutorForMain(context);
                                ExEventBus.getDefault().register(mExecutorForMain);
                            }
                        }.start();
                    }
                });
            } else if ("com.tencent.mm:tools".equals(lpparam.processName)) {
                XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        XLogger.d("进入 com.tencent.mm:tools");
                        new Thread(){
                            @Override
                            public void run() {
                                XLogger.d("在com.tencent.mm：tools进程内注册ExEventBus");
                                Context context = (Context) param.args[0];
                                ExEventBus.create(context);
                                mExecutorForTool = new ExecutorForTool(context);
                                ExEventBus.getDefault().register(mExecutorForTool);
                            }
                        }.start();
                    }
                });
            }
        } else if (lpparam.packageName.equals("com.vphone.launcher") && lpparam.processName.equals("com.vphone.launcher")) {

            XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    Context context = (Context) param.args[0];
                    Intent messageServiceIntent = new Intent("com.tensynchina.Message");
                    messageServiceIntent.setComponent(new ComponentName("com.tensynchina.hook","com.tensynchina.hook.push.MessageService"));
                    context.startService(messageServiceIntent);

                    Intent taskServiceIntent = new Intent("com.tensynchina.Task");
                    taskServiceIntent.setComponent(new ComponentName("com.tensynchina.hook","com.tensynchina.hook.task.TaskService"));
                    context.startService(taskServiceIntent);
                    XLogger.d("启动服务结束");
                }
            });
        }
    }
}

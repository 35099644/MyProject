package com.tensynchina.hook;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import com.llx278.exeventbus.ExEventBus;
import com.tensynchina.hook.utils.XLogger;
import com.tensynchina.hook.wechat.ExecutorForCpuLoader;
import com.tensynchina.hook.wechat.ExecutorForMain;
import com.tensynchina.hook.wechat.ExecutorForPush;
import com.tensynchina.hook.wechat.ExecutorForSandBox;
import com.tensynchina.hook.wechat.ExecutorForSupport;
import com.tensynchina.hook.wechat.ExecutorForTool;
import com.tensynchina.hook.wechat.WXDatabase;
import com.tensynchina.hook.wechat.WXDatabaseCPULoader;
import com.tensynchina.hook.wechat.WXDatabasePush;
import com.tensynchina.hook.wechat.WXDatabaseSandbox;
import com.tensynchina.hook.wechat.WXDatabaseSupport;
import com.tensynchina.hook.wechat.WXDatabaseTools;

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
    private ExecutorForPush mExecutorForPush;
    private ExecutorForCpuLoader mExecutorForCpuLoader;
    private ExecutorForSandBox mExecutorForSandBox;
    private ExecutorForSupport mExecutorForSupport;
    private int count = 0;

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (lpparam.packageName.equals(WE_CHAT)) {
            if ("com.tencent.mm".equals(lpparam.processName)) {
                XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class,
                        new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        // 注册exeventbus
                        XLogger.d("进入 com.tencent.mm");
                        XposedHelpers.findAndHookMethod(
                                "com.tencent.wcdb.database.SQLiteDatabase",
                                lpparam.classLoader,
                                "insertWithOnConflict",
                                "java.lang.String",
                                "java.lang.String",
                                "android.content.ContentValues",
                                int.class,
                                new WXDatabase());
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
                        XposedHelpers.findAndHookMethod(
                                "com.tencent.wcdb.database.SQLiteDatabase",
                                lpparam.classLoader,
                                "insertWithOnConflict",
                                "java.lang.String",
                                "java.lang.String",
                                "android.content.ContentValues",
                                int.class,
                                new WXDatabaseTools());

                    }
                });
            } else if ("com.tencent.mm:push".equals(lpparam.processName)) {
                XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        XLogger.d("进入 com.tencent.mm:push");
                        new Thread(){
                            @Override
                            public void run() {
                                XLogger.d("在com.tencent.mm:push进程内注册ExEventBus");
                                Context context = (Context) param.args[0];
                                ExEventBus.create(context);
                                mExecutorForPush = new ExecutorForPush();
                                ExEventBus.getDefault().register(mExecutorForPush);
                            }
                        }.start();
                        XposedHelpers.findAndHookMethod(
                                "com.tencent.wcdb.database.SQLiteDatabase",
                                lpparam.classLoader,
                                "insertWithOnConflict",
                                "java.lang.String",
                                "java.lang.String",
                                "android.content.ContentValues",
                                int.class,
                                new WXDatabasePush());
                    }
                });
            } else if ("com.tencent.mm:support".equals(lpparam.processName)) {
                XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        XLogger.d("进入 com.tencent.mm:support");
                        new Thread(){
                            @Override
                            public void run() {
                                XLogger.d("在com.tencent.mm:support进程内注册ExEventBus");
                                Context context = (Context) param.args[0];
                                ExEventBus.create(context);
                                mExecutorForSupport = new ExecutorForSupport();
                                ExEventBus.getDefault().register(mExecutorForSupport);
                            }
                        }.start();
                        XposedHelpers.findAndHookMethod(
                                "com.tencent.wcdb.database.SQLiteDatabase",
                                lpparam.classLoader,
                                "insertWithOnConflict",
                                "java.lang.String",
                                "java.lang.String",
                                "android.content.ContentValues",
                                int.class,
                                new WXDatabaseSupport());

                    }
                });
            } else if ("com.tencent.mm:cuploader".equals(lpparam.processName)) {
                XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        XLogger.d("进入 com.tencent.mm:cpuloader");
                        new Thread(){
                            @Override
                            public void run() {
                                XLogger.d("在com.tencent.mm:cpuloader进程内注册ExEventBus");
                                Context context = (Context) param.args[0];
                                ExEventBus.create(context);
                                mExecutorForCpuLoader= new ExecutorForCpuLoader(context);
                                ExEventBus.getDefault().register(mExecutorForCpuLoader);
                            }
                        }.start();
                        XposedHelpers.findAndHookMethod(
                                "com.tencent.wcdb.database.SQLiteDatabase",
                                lpparam.classLoader,
                                "insertWithOnConflict",
                                "java.lang.String",
                                "java.lang.String",
                                "android.content.ContentValues",
                                int.class,
                                new WXDatabaseCPULoader());
                    }
                });
            } else if ("com.tencent.mm:sandbox".equals(lpparam.processName)) {
                XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        XLogger.d("进入 com.tencent.mm:sandbox");
                        new Thread(){
                            @Override
                            public void run() {
                                XLogger.d("在com.tencent.mm:sandbox进程内注册ExEventBus");
                                Context context = (Context) param.args[0];
                                ExEventBus.create(context);
                                mExecutorForSandBox = new ExecutorForSandBox(context);
                                ExEventBus.getDefault().register(mExecutorForSandBox);
                            }
                        }.start();
                        XposedHelpers.findAndHookMethod(
                                "com.tencent.wcdb.database.SQLiteDatabase",
                                lpparam.classLoader,
                                "insertWithOnConflict",
                                "java.lang.String",
                                "java.lang.String",
                                "android.content.ContentValues",
                                int.class,
                                new WXDatabaseSandbox());
                    }
                });
            }
        } else if (lpparam.packageName.equals("com.vphone.launcher") && lpparam.processName.equals("com.vphone.launcher")) {

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
}

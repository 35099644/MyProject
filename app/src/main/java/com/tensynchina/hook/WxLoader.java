package com.tensynchina.hook;

import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.Intent;

import com.llx278.exeventbus.ExEventBus;
import com.tensynchina.hook.utils.XLogger;
import com.tensynchina.hook.wechat.ExecutorForCpuLoader;
import com.tensynchina.hook.wechat.ExecutorForMain;
import com.tensynchina.hook.wechat.ExecutorForPush;
import com.tensynchina.hook.wechat.ExecutorForSandBox;
import com.tensynchina.hook.wechat.ExecutorForSupport;
import com.tensynchina.hook.wechat.ExecutorForTool;
import com.tensynchina.hook.wechat.WConstant;
import com.tensynchina.hook.wechat.WXDatabase;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 *
 * Created by llx on 2018/3/26.
 */

class WxLoader {

    private ExecutorForMain mExecutorForMain;
    private ExecutorForTool mExecutorForTool;
    private ExecutorForPush mExecutorForPush;
    private ExecutorForCpuLoader mExecutorForCpuLoader;
    private ExecutorForSandBox mExecutorForSandBox;
    private ExecutorForSupport mExecutorForSupport;

    void load(final XC_LoadPackage.LoadPackageParam lpparam) {
        if (WConstant.WX_MAIN_PROCESS.equals(lpparam.processName)) {
            handleMainProcess(lpparam);
            return;
        }

        if (WConstant.WX_TOOLS_PROCESS.equals(lpparam.processName)) {
            handleToolProcess(lpparam);
            return;
        }

        if (WConstant.WX_PUSH_PROCESS.equals(lpparam.processName)) {
            handlePushProcess(lpparam);
            return;
        }

        if (WConstant.WX_SUPPORT_PROCESS.equals(lpparam.processName)) {
            handleSupportProcess(lpparam);
            return;
        }

        if (WConstant.WX_LOADER_PROCESS.equals(lpparam.processName)) {
            handleLoaderProcess(lpparam);
            return;
        }

        if (WConstant.WX_SAND_BOX_PROCESS.equals(lpparam.processName)) {
            handleSandboxProcess(lpparam);
        }
    }


    private void handleMainProcess(final XC_LoadPackage.LoadPackageParam lpparam) {
        XposedHelpers.findAndHookMethod(Application.class,
                "attach",
                Context.class,
                new MainApplicationHook(lpparam));
    }

    private void handleToolProcess(final XC_LoadPackage.LoadPackageParam lpparam) {
        XposedHelpers.findAndHookMethod(Application.class,
                "attach",
                Context.class,
                new ToolApplicationHook(lpparam));
    }

    private void handlePushProcess(final XC_LoadPackage.LoadPackageParam lpparam) {
        XposedHelpers.findAndHookMethod(Application.class,
                "attach",
                Context.class,
                new PushApplicationHook(lpparam));
    }

    private void handleSupportProcess(final XC_LoadPackage.LoadPackageParam lpparam) {
        XposedHelpers.findAndHookMethod(Application.class,
                "attach",
                Context.class,
                new SupportApplicationHook(lpparam));
    }

    private void handleLoaderProcess(final XC_LoadPackage.LoadPackageParam lpparam) {
        XposedHelpers.findAndHookMethod(Application.class,
                "attach",
                Context.class,
                new LoaderApplicationHook(lpparam));
    }

    private void handleSandboxProcess (final XC_LoadPackage.LoadPackageParam lpparam) {
        XposedHelpers.findAndHookMethod(Application.class,
                "attach",
                Context.class,
                new SandboxApplicationHook(lpparam));
    }

    private class SandboxApplicationHook extends XC_MethodHook {
        private XC_LoadPackage.LoadPackageParam mLpparam;
        SandboxApplicationHook(XC_LoadPackage.LoadPackageParam lpparam) {
            mLpparam = lpparam;
        }

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

            XposedHelpers.findAndHookMethod("com.tencent.mm.sandbox.updater.UpdaterService",
                    mLpparam.classLoader, "onCreate", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    XLogger.d("启动com.tencent.mm.sandbox.updater.UpdateService, onCreate 直接返回!!!!!!!");
                    param.setResult(null);
                }
            });

            XposedHelpers.findAndHookMethod("com.tencent.mm.sandbox.updater.UpdaterService",
                    mLpparam.classLoader, "onStartCommand", Intent.class, int.class, int.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    XLogger.d("启动 com.tencent.mm.sandbox.updater.UpdateService onStartCommand 阻止执行！！");
                    param.setResult(Service.START_NOT_STICKY);
                }
            });

            XposedHelpers.findAndHookMethod("com.tencent.mm.sandbox.updater.UpdaterService",
                    mLpparam.classLoader, "onStart", Intent.class, int.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    XLogger.d("启动 com.tencent.mm.sandbox.updater.UpdateService onStart 阻止执行！！");
                    param.setResult(null);
                }
            });

            XposedHelpers.findAndHookMethod("com.tencent.mm.sandbox.updater.UpdaterService",
                    mLpparam.classLoader, "onBind", Intent.class,new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    XLogger.d("启动 com.tencent.mm.sandbox.updater.UpdateService onBind 阻止执行！！");
                    param.setResult(null);
                }
            });
        }
    }

    private class LoaderApplicationHook extends XC_MethodHook {
        private XC_LoadPackage.LoadPackageParam mLpparam;
        LoaderApplicationHook(XC_LoadPackage.LoadPackageParam lpparam) {
            mLpparam = lpparam;
        }
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
        }
    }

    private class SupportApplicationHook extends XC_MethodHook {
        private XC_LoadPackage.LoadPackageParam mLpparam;

        SupportApplicationHook(XC_LoadPackage.LoadPackageParam lpparam) {
            mLpparam = lpparam;
        }
        @Override
        protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
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
        }
    }

    private class PushApplicationHook extends XC_MethodHook {
        private XC_LoadPackage.LoadPackageParam mLpparam;
        PushApplicationHook(XC_LoadPackage.LoadPackageParam lpparam) {
            mLpparam = lpparam;
        }

        @Override
        protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
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
        }
    }

    private class ToolApplicationHook extends XC_MethodHook {

        private XC_LoadPackage.LoadPackageParam mLpparam;
        ToolApplicationHook(XC_LoadPackage.LoadPackageParam lpparam) {
            mLpparam = lpparam;
        }
        @Override
        protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
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
    }

    private class MainApplicationHook extends XC_MethodHook {
        private XC_LoadPackage.LoadPackageParam mLpparam;

        MainApplicationHook(XC_LoadPackage.LoadPackageParam lpparam) {
            mLpparam = lpparam;
        }
        @Override
        protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
            XLogger.d("进入 com.tencent.mm");
            XposedHelpers.findAndHookMethod(
                    "com.tencent.wcdb.database.SQLiteDatabase",
                    mLpparam.classLoader,
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
    }
}

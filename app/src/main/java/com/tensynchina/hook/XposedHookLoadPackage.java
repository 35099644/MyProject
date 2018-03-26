package com.tensynchina.hook;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 *
 * Created by llx on 2018/3/15.
 */

public class XposedHookLoadPackage implements IXposedHookLoadPackage {

    private WxLoader mWxLoader;
    private NoxLoader mNoxLoader;

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        switch (lpparam.packageName) {
            case "com.tencent.mm":
                if (mWxLoader == null) {
                    mWxLoader = new WxLoader();
                }
                mWxLoader.load(lpparam);
                break;
            case "com.vphone.launcher":
                if (mNoxLoader == null) {
                    mNoxLoader = new NoxLoader();
                }
                mNoxLoader.load(lpparam);
                break;
            default:
        }
    }
}

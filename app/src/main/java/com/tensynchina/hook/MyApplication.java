package com.tensynchina.hook;

import android.app.Application;
import android.os.Process;
import android.util.Log;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

/**
 * Created by llx on 2018/3/15.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.addLogAdapter(new AndroidLogAdapter());
        Logger.d("MyApplication start ! currentPid : " + Process.myPid());
    }
}

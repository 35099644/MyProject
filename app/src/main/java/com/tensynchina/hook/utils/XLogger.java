package com.tensynchina.hook.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.robv.android.xposed.XposedBridge;

/**
 * 简单的对xposed的日志进行封装
 * Created by llx on 2018/3/15.
 */

public class XLogger {

    public static void d(String log) {
        String time = getTime();
        XposedBridge.log(time + " : " + log);
    }

    public static void e(Throwable t) {
        String time = getTime();
        XposedBridge.log("----------------- exception time is : " + time+"---------------------");
        XposedBridge.log(t);
        XposedBridge.log("----------------- end exception ---------------------");
    }

    private static String getTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ", Locale.CHINA).
                format(new Date(System.currentTimeMillis()));
    }

}

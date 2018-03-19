package com.llx278.exeventbus;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by llx on 2018/2/3.
 */

public class ELogger {
    private static final String TAG = "EventBusDecorator";
    private static final boolean DEBUG = true;
    private static final String SPILT = "  ";

    public static void d(String tag, String msg) {
        if(DEBUG) {
            Log.d(tag,msg);
        }
    }

    public static void d(String msg) {
        d(TAG,msg);
    }

    public static void i(String tag,String msg) {
        if (DEBUG){
            Log.i(TAG,msg);
        }
    }

    public static void i(String msg){
        i(TAG,msg);
    }

    public static void e(String tag,String msg,Throwable e){
        if (e != null) {
            Log.e(tag,msg,e);
        }
    }

    public static void e(String msg,Throwable e) {
        e(TAG,msg,e);
    }

    private static String timeStamp2DateStr(long timestamp) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(new Date(timestamp));
    }
}

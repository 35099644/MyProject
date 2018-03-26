package com.tensynchina.hook.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.SystemClock;
import android.text.TextUtils;

import java.util.List;

/**
 * Created by llx on 2018/3/26.
 */

public class WaiterUtils {

    public static boolean waitForProcessCreated(Context context, String processName, long timeout) {

        if (context == null || TextUtils.isEmpty(processName)) {
            return false;
        }

        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            long endTime = SystemClock.uptimeMillis() + timeout;
            while (SystemClock.uptimeMillis() < endTime) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ignore) {
                }

                List<ActivityManager.RunningAppProcessInfo> runningAppProcesses =
                        activityManager.getRunningAppProcesses();
                for (ActivityManager.RunningAppProcessInfo info : runningAppProcesses) {
                    if (info.processName.equals(processName)) {
                        return true;
                    }
                }
            }
            return false;
        }
        return false;
    }

}

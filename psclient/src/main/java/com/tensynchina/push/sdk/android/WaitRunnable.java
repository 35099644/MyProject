package com.tensynchina.push.sdk.android;

import android.util.Log;

/**
 * Created by llx on 27/06/2017.
 */

public class WaitRunnable implements Runnable {

    private String mId;
    private long mTimeOut;
    private boolean mHasTimeout;
    private TimeoutNotify mListener;
    public final Object mLockObj = new Object();

    public WaitRunnable (String id,long timeOut) {
        mId = id;
        mTimeOut = timeOut;
    }

    public void setonTimeOutNotifyListener(TimeoutNotify listener) {
        mListener = listener;
    }

    public void setHasTimeout(boolean timeout) {
        mHasTimeout = timeout;
    }

    @Override
    public void run() {
        mHasTimeout = true;
        try {
            synchronized (this) {
                while (mHasTimeout) {
                    wait(mTimeOut);
                    if (mHasTimeout && mListener != null) {
                        mListener.onTimeout(mId);
                        mHasTimeout = false;
                    }
                }
            }
        } catch (InterruptedException e) {
            Log.e("ee",Log.getStackTraceString(e));
        }
    }

    public interface TimeoutNotify {
        void onTimeout(String id);
    }
}

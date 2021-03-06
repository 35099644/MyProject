package com.llx278.exeventbus.remote;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;


import com.llx278.exeventbus.*;
import com.llx278.exeventbus.remote.Address;
import com.llx278.exeventbus.remote.MockPhysicalLayer;
import com.llx278.exeventbus.remote.Receiver;

import java.util.List;

/**
 * Created by llx on 2018/2/28.
 */

public class TestService3 extends Service implements Receiver {

    private String mBroadcastStr;
    private String mReceiveStr;

    private IMyTestInterface.Stub mTest = new IMyTestInterface.Stub() {
        @Override
        public String getProcessName() throws RemoteException {
            Holder holder = processName();
            return holder.mProcessName + "-" + holder.mPid;
        }

        @Override
        public String getBroadcastStr() throws RemoteException {
            return mBroadcastStr;
        }

        @Override
        public String getReceiveStr() throws RemoteException {
            return mReceiveStr;
        }

        @Override
        public void mockSendMessage(String address, Bundle message) throws RemoteException {
            mTransferLayer.send(address,message);
        }

        @Override
        public boolean mockSendMessage1(String address, Bundle message, long timeout) throws RemoteException {
            return false;
        }

        @Override
        public void mockSendBroadcast(Bundle message) throws RemoteException {

        }

        @Override
        public void clear() throws RemoteException {
            mBroadcastStr = null;
            mReceiveStr = null;
        }
    };

    private MockPhysicalLayer mTransferLayer;

    @Override
    public void onCreate() {
        super.onCreate();
        mTransferLayer = new MockPhysicalLayer(this);
        mTransferLayer.setOnReceiveListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mTransferLayer.destroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mTest;
        //return null;
    }

    public Holder processName() {
        Holder holder;
        ActivityManager systemService = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses =
                systemService.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo infp : runningAppProcesses) {
            if (infp.pid == Process.myPid()) {
                holder = new Holder(infp.processName,infp.pid);
                return holder;
            }
        }
        return null;
    }

    @Override
    public void onMessageReceive(String where, Bundle message) {
        mBroadcastStr = message.getString(com.llx278.exeventbus.Constant.KEY_BROADCAST);
        mReceiveStr = message.getString(com.llx278.exeventbus.Constant.KEY_RECEIVE) + ":" + Address.createOwnAddress();
        Log.d("main","TestService3 : " + mReceiveStr);
    }

    private class Holder  {
        final String mProcessName;
        final int mPid;
        Holder(String processName,int pid) {
            mProcessName = processName;
            mPid = pid;
        }
    }
}

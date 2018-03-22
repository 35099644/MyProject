package com.tensynchina.hook.push;

import android.os.Environment;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.orhanobut.logger.Logger;
import com.tensynchina.hook.utils.IOUtils;
import com.tensynchina.push.sdk.android.PSReceiverWrapper;

import java.io.File;

/**
 * Created by llx on 28/06/2017.
 */

public class Receiver {

    private PSReceiverWrapper mReceiver;
    private String token;
    private String vids;
    private String sids;
    private String uuid;
    private String masterHost;
    private String masterPort;
    private String mTag = "#278#";
    private final Object mLock = new Object();
    private boolean mCanContinue = true;
    private boolean isAck = false;
    private MonitorThread mMonitorThread;

    private class MonitorThread extends Thread {
        @Override
        public void run() {

            while (true) {
                try {
                    Thread.sleep(15 * 60 * 1000);
                } catch (InterruptedException e) {
                    Logger.e(e,"");
                }

                send(uuid, mTag);
                try {
                    isAck = false;
                    synchronized (mLock) {
                        while (!isAck) {
                            mLock.wait(20 * 1000);
                            if (!isAck) {
                                registerPSClient();
                                return;
                            }
                        }
                    }
                } catch (InterruptedException e) {
                    Logger.e(e,"");
                }
            }
        }
    }

    public void notifyAck() {
        //Logger.d("notifyAck");
        isAck = true;
        synchronized (mLock) {
            mLock.notify();
        }
    }

    public void send(String touid, String msg) {
        String[] sids = this.sids.split(",");
        String[] vids = this.vids.split(",");
        mReceiver.sendmsg(Integer.parseInt(sids[0]), Integer.parseInt(vids[0]), touid, msg);
    }

    public PSReceiverWrapper getPsReceiver() {
        return mReceiver;
    }

    public String getMyUid() {
        return uuid;
    }

    public String getTag() {
        return mTag;
    }


    public void registerPSClient() {
        Logger.d("registerPSClient");
        File configFile = new File(new File(Environment.getExternalStorageDirectory(), ".llx278").getAbsolutePath(), "PushConfigName.json");

        try {
            String configStr = IOUtils.fileToString(configFile.getAbsolutePath());
            JSONObject jo = JSON.parseObject(configStr);
            token = jo.getString(PConstant.TOKEN);
            vids = jo.getString(PConstant.VIDS);
            sids = jo.getString(PConstant.SIDS);
            uuid = jo.getString(PConstant.UUID);
            masterHost = jo.getString(PConstant.HOST);
            masterPort = jo.getString(PConstant.PORT);
            if (mReceiver == null) {
                mReceiver = PSReceiverWrapper.getInstance(token, uuid, masterHost, Integer.valueOf(masterPort));
                mReceiver.getRequest().add(PConstant.SIDS, sids);
                mReceiver.getRequest().add(PConstant.VIDS, vids);
                mReceiver.setTimeout(10000);
                mReceiver.tostart();
            } else {
                mReceiver.getPsclient().tryrestart();
            }

            Logger.d("尝试启动监控线程");
            if (mMonitorThread != null) {
                mMonitorThread.interrupt();
                mMonitorThread = null;
            }

            mMonitorThread = new MonitorThread();
            mMonitorThread.setDaemon(true);
            mMonitorThread.start();

        } catch (Exception e) {
            Logger.e(e,"");
        }
    }
}

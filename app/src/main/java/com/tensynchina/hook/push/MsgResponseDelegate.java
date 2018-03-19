package com.tensynchina.hook.push;

import android.util.Log;

import com.orhanobut.logger.Logger;
import com.tensynchina.push.client.Kind;
import com.tensynchina.push.client.impl.AbstractPResponse;
import com.tensynchina.push.client.impl.PClient;
import com.tensynchina.push.client.impl.PRequest;
import com.tensynchina.push.sdk.android.ArmDelegate;

import de.robv.android.xposed.XposedBridge;

/**
 *
 * Created by llx on 23/06/2017.
 */

public class MsgResponseDelegate implements ArmDelegate {

    @Override
    public void heart(int retry) {

    }

    @Override
    public void debug(String msg) {
        //Logger.d(msg);
    }

    @Override
    public void debug(String msg, Exception e) {
        Log.d("main",msg);
        Log.e("main","",e);
    }

    @Override
    public void auhtorizeErr() {
        Logger.d("auhtorizeErr");
    }

    @Override
    public void serverready(PClient<? extends PRequest, ? extends AbstractPResponse> pclient) {

    }

    @Override
    public void receiveString(PClient<? extends PRequest, ? extends AbstractPResponse> pclient, String[] params, String message) {
    }

    @Override
    public void receiveByte(PClient<? extends PRequest, ? extends AbstractPResponse> pclient, String[] val, byte[] message) {

    }

    @Override
    public void receiveother(PClient<? extends PRequest, ? extends AbstractPResponse> pclient, int tag, Kind kind, Object message) {

    }

    @Override
    public void ready(PClient<? extends PRequest, ? extends AbstractPResponse> pclient) {

    }
}

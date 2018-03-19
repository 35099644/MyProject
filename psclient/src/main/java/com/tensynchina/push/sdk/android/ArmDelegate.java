package com.tensynchina.push.sdk.android;

import com.tensynchina.push.client.ResponseDelegate;

/**
 * Created by llx on 03/05/2017.
 */

public interface ArmDelegate extends ResponseDelegate {
    public void heart(int retry);
    void debug(String msg);
    void debug(String msg, Exception e);
}

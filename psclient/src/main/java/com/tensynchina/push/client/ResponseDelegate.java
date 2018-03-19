package com.tensynchina.push.client;

import com.tensynchina.push.client.impl.AbstractPResponse;
import com.tensynchina.push.client.impl.PClient;
import com.tensynchina.push.client.impl.PRequest;

/**
 * Created by susy on 07/04/2017.
 */
public interface ResponseDelegate {
    void auhtorizeErr();
    void serverready(PClient<? extends PRequest, ? extends AbstractPResponse> pclient);
    void receiveString(PClient<? extends PRequest, ? extends AbstractPResponse> pclient, String[] val, String message);
    void receiveByte(PClient<? extends PRequest, ? extends AbstractPResponse> pclient, String[] val, byte[] message);
    void receiveother(PClient<? extends PRequest, ? extends AbstractPResponse> pclient, int tag, Kind kind, Object message);
    void ready(PClient<? extends PRequest, ? extends AbstractPResponse> pclient);
}

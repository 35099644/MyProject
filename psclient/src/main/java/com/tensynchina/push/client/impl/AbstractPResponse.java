package com.tensynchina.push.client.impl;

import com.tensynchina.push.client.IPResponse;
import com.tensynchina.push.client.Kind;
import com.tensynchina.push.client.ResponseDelegate;

/**
 * Created by susy on 07/04/2017.
 */
public abstract class AbstractPResponse<RT extends PRequest> implements IPResponse{
    protected PClient<RT,AbstractPResponse> pclient;
    protected ResponseDelegate delegate;

    protected abstract void authorized();
    protected abstract void authorizefailed();
    protected abstract void serverready();
    protected abstract void serversynccfg(String message);
    protected abstract void receiveString(String[] head,String message);
    protected abstract void receiveByte(String[] head,byte[] message);

    protected void applyStringMsg(String message){
        String[] vals = pclient.spliptPushMsgBody(message,0,7);
        receiveString(vals,message);
        if(delegate!=null){
            delegate.receiveString(pclient,vals,message);
        }
    }
    protected void applyByteMsg(byte[] message){
        String[] vals = pclient.spliptPushMsgBody(message,0,9);
        receiveByte(vals,message);
        if(delegate!=null){
            delegate.receiveByte(pclient,vals,message);
        }
    }
    @Override
    public void receive(int tag, Kind kind, Object message) {
        if(tag==1){
            switch (kind){
                case INT:
                    int v = (Integer)message;
                    if(v==0){
                        authorized();
                    } else if(v==3){
                        authorizefailed();
                        if(delegate!=null){
                            delegate.auhtorizeErr();
                        }
                    } else if(v==1){
                        serverready();
                        if(delegate!=null){
                            delegate.serverready(pclient);
                        }
                    }
                    break;
                case STRING:
                    serversynccfg((String)message);
                    break;
            }
        }else if(tag==2){
            switch (kind){
                case STRING:
                    applyStringMsg((String)message);
                    break;
                case BYTES:
                    applyByteMsg((byte[])message);
                    break;
            }
        }else{
            /*if(delegate!=null){
                delegate.receiveother(pclient,tag,kind,message);
            }*/
            if(delegate!=null){
                if(tag==0&&kind.ordinal()==0&&message instanceof Integer&&(Integer)message==0){
                    Thread t = new Thread(new Runnable() {
                        public void run() {
                            delegate.ready(pclient);
                        }
                    });
                    t.setDaemon(true);
                    t.start();
                }else {
                    delegate.receiveother(pclient, tag, kind, message);
                }
            }
        }
    }

    @Override
    public void overflow(int tag, Kind kind, Object message) {

    }

    public void setDelegate(ResponseDelegate delegate) {
        this.delegate = delegate;
    }

    void setPclient(PClient<RT, AbstractPResponse> pclient) {
        this.pclient = pclient;
    }
}

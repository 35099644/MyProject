package com.tensynchina.push.sdk.android;

import android.text.TextUtils;
import android.util.Log;

import com.tensynchina.push.client.Kind;
import com.tensynchina.push.client.ResponseDelegate;
import com.tensynchina.push.client.impl.AbstractPResponse;
import com.tensynchina.push.client.impl.PClient;
import com.tensynchina.push.client.impl.PRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * Created by llx on 27/06/2017.
 */

@SuppressWarnings("unused")
public class PSReceiverWrapper implements WaitRunnable.TimeoutNotify {

    private static PSReceiverWrapper mPSReceiverWrapper;
    private static PSReceiver mPsReceiver;
    private DelegateWrapper mDelegateWrapper;
    private long mTimeout = 0;
    private Map<String,MessageHolder> mMessageCache;
    private ExecutorService mResponseWaitPool;
    private TimeoutNotifyDelegate mListener;

    public static PSReceiverWrapper getInstance(String token, String uuid, String masterhost, int masterport) throws Exception {
        if (mPSReceiverWrapper == null) {
            synchronized (PSReceiverWrapper.class) {
                if (mPSReceiverWrapper == null) {
                    mPSReceiverWrapper = new PSReceiverWrapper(token,uuid,masterhost,masterport);
                }
            }
        }
        return mPSReceiverWrapper;
    }

    private PSReceiverWrapper(String token,String uuid,String masterhost,int masterport) throws Exception {
        mPsReceiver = PSReceiver.getInstance(token,uuid,masterhost,masterport);
        mDelegateWrapper = new DelegateWrapper();
        mResponseWaitPool = Executors.newCachedThreadPool();
        mMessageCache  = new HashMap<>();
    }

    public PSRequest getRequest(){
        return mPsReceiver.getRequest();
    }

    public void setDelegate(ResponseDelegate delegate){
        mDelegateWrapper.setArmDelegate(delegate);
        mPsReceiver.setDelegate(mDelegateWrapper);
    }

    @SuppressWarnings("unused")
    public static void destoryInstance() {
        if (mPSReceiverWrapper != null) {
            synchronized (PSReceiverWrapper.class) {
                if (mPSReceiverWrapper != null) {
                    PSReceiver.destoryInstance();
                    mPSReceiverWrapper = null;
                }
            }
        }
    }

    public void setOnSendTimeoutListener(TimeoutNotifyDelegate listener) {
        mListener = listener;
    }

    public void sendmsg(int sid,int vid,String touid,String msg){
        MessageHolder mh = packageMsg(sid,vid,-1,touid,null,msg);
        // int sid, int vid, int route, String touid, String method
        String newMsg = mh.id + "#" + sid + "#" + vid + "#" + "-" + "#" + touid +"#" + "-" + ":" + mh.msg;
        mPsReceiver.sendmsg(sid,vid,touid,newMsg);
        mh.waitResponse.setonTimeOutNotifyListener(this);
        mResponseWaitPool.execute(mh.waitResponse);
    }

    private MessageHolder packageMsg(int sid, int vid, int route, String touid, String method, String msg) {
        String uuid = UUID.randomUUID().toString();
        MessageHolder mh = new MessageHolder(sid,vid,route,touid,method,msg,uuid,new WaitRunnable(uuid,mTimeout));
        mMessageCache.put(uuid,mh);
        return mh;
    }

    public void sendmsg(int sid,int vid,String touid,String method,String msg){
        MessageHolder mh = packageMsg(sid,vid,-1,touid,method,msg);
        String newMsg = mh.id + "#" + sid + "#" + vid + "#" + "-" + "#" + touid +"#" + method + ":" + mh.msg;
        mPsReceiver.sendmsg(sid,vid,touid,method,newMsg);
        mh.waitResponse.setonTimeOutNotifyListener(this);
        mResponseWaitPool.execute(mh.waitResponse);
    }

    public void sendmsg(int sid,int vid,int route,String touid,String method,String msg){
        MessageHolder mh = packageMsg(sid,vid,route,touid,method,msg);
        String newMsg = mh.id + "#" + sid + "#" + vid + "#" + route + "#" + touid +"#" + method + ":" + mh.msg;
        mPsReceiver.sendmsg(sid,vid,route,touid,method,newMsg);
        mh.waitResponse.setonTimeOutNotifyListener(this);
        mResponseWaitPool.execute(mh.waitResponse);
    }

    public void addServerID(int serverid){
        mPsReceiver.addServerID(serverid);
    }

    public void tostart(){
        mPsReceiver.tostart();
    }

    public void shutdown() {
        mPsReceiver.shutdown();
    }

    /**
     * 设置超时时间
     * @param timeout 0 永远等待
     */
    public void setTimeout(long timeout) {
        this.mTimeout = timeout;
    }

    public PClient<PSRequest, PSResponse> getPsclient() {
        return mPsReceiver.getPsclient();
    }

    @Override
    public void onTimeout(String id) {
        MessageHolder mh = mMessageCache.remove(id);
        if (mListener != null) {
            mListener.onTimeout(mh.sid,mh.vid,mh.route,mh.touid,mh.method,mh.msg);
        }
    }

    private class DelegateWrapper implements ArmDelegate {

        private ResponseDelegate armDelegate;

        void setArmDelegate(ResponseDelegate armDelegate) {
            this.armDelegate = armDelegate;
        }

        @Override
        public void heart(int retry) {
            if (armDelegate instanceof ArmDelegate) {
                ((ArmDelegate) armDelegate).heart(retry);
            }
        }

        @Override
        public void debug(String msg) {
            if (armDelegate instanceof ArmDelegate) {
                ((ArmDelegate) armDelegate).debug(msg);
            }

        }

        @Override
        public void debug(String msg, Exception e) {
            if (armDelegate instanceof ArmDelegate) {
                ((ArmDelegate) armDelegate).debug(msg,e);
            }
        }

        @Override
        public void auhtorizeErr() {
            armDelegate.auhtorizeErr();
        }

        @Override
        public void serverready(PClient<? extends PRequest, ? extends AbstractPResponse> pclient) {
            armDelegate.serverready(pclient);
        }

        @Override
        public void receiveString(PClient<? extends PRequest, ? extends AbstractPResponse> pclient, String[] params, String message) {

            // 在这这里拿到id，根据id找到对应的锁，然后释放。
            String msg = message;
            String posstr = params[params.length-1];
            String msgPre = "";
            if(posstr!=null){
                int pos = pclient.parseInt(posstr,-1);
                if(pos>=0){
                    if(pos>=message.length()){
                        msg = "";
                    }else {
                        msgPre = message.substring(0,pos);
                        msg = message.substring(pos);
                    }
                }
            }
            Log.d("ee","收到msg : " + msg);
            if (TextUtils.isEmpty(msg)) {
                // 不是有效的msg 忽略
                return;
            }
            String uid = params[0];
            int i = msg.indexOf(":");
            String prefix = msg.substring(0,i);
            String[] prefixs = prefix.split("#");
            // int sid, int vid, int route, String touid, String method
            String id = prefixs[0];
            String aMsg = msg.substring(i+1);
            String mAckTag = "ack&%";
            if (aMsg.equals(mAckTag)) {
                // 接收到了一个应答请求，释放等待这个应答请求的锁.
                MessageHolder messageHolder = mMessageCache.remove(id);
                if (messageHolder == null) {
                    Log.e("ee","messageHolder is null");
                    return;
                }

                if (messageHolder.waitResponse != null) {
                    synchronized (messageHolder.waitResponse) {
                        messageHolder.waitResponse.setHasTimeout(false);
                        messageHolder.waitResponse.notify();
                    }
                }
                return;
            }

            int sid = Integer.parseInt(prefixs[1]);
            int vid = Integer.parseInt(prefixs[2]);
            int route = "-".equals(prefixs[3]) ? -999:Integer.parseInt(prefixs[2]);
            String touid = prefixs[4];
            String method = prefixs[5];
            // 接收到了一条消息，响应ack应答
            String ackMsg = id + ":" + mAckTag;
            if (route == -999 && "-".equals(method)) {
                mPsReceiver.sendmsg(sid,vid,uid,ackMsg);
            } else if ("-".equals(method)) {
                mPsReceiver.sendmsg(sid,vid,uid,method,ackMsg);
            } else if (-999 == route) {
                mPsReceiver.sendmsg(sid,vid,route,uid,method,ackMsg);
            }
            armDelegate.receiveString(pclient,params,msgPre + aMsg);
        }

        @Override
        public void receiveByte(PClient<? extends PRequest, ? extends AbstractPResponse> pclient, String[] val, byte[] message) {
            armDelegate.receiveByte(pclient,val,message);
        }

        @Override
        public void receiveother(PClient<? extends PRequest, ? extends AbstractPResponse> pclient, int tag, Kind kind, Object message) {
            armDelegate.receiveother(pclient,tag,kind,message);
        }

        @Override
        public void ready(PClient<? extends PRequest, ? extends AbstractPResponse> pclient) {
            armDelegate.ready(pclient);
        }
    }

    private class MessageHolder {
        int sid;
        int vid;
        int route;
        String touid;
        String method;
        String msg;
        String id;
        final WaitRunnable waitResponse;

        MessageHolder(int sid, int vid, int route, String touid, String method, String msg, String id, WaitRunnable waitResponse) {
            this.sid = sid;
            this.vid = vid;
            this.route = route;
            this.touid = touid;
            this.method = method;
            this.msg = msg;
            this.id = id;
            this.waitResponse = waitResponse;
        }
    }

    public interface TimeoutNotifyDelegate {
        void onTimeout(int sid,int vid,int route,String touid,String method,String msg);
    }
}


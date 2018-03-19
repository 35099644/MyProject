package com.tensynchina.push.sdk.android;

import android.util.Log;

import com.tensynchina.push.client.impl.AbstractPResponse;

import java.util.HashMap;
import java.util.Map;

public class PSResponse extends AbstractPResponse<PSRequest> {
	private PSReceiver receiver;
	private int reAuthorizeCnt=0;
	private Map<String,Map<Integer,byte[]>> pagecache = new HashMap<String,Map<Integer, byte[]>>();
	private Map<String,Integer> typecache = new HashMap<String,Integer>();

	@Override
	protected void authorized() {
	}

	@Override
	protected void authorizefailed() {
		debug("信息异常，不能通过PushService注册验证!");
		serverready();
	}

	@Override
	protected void serverready() {

		debug("PushService 不能为Receiver 提供有效的推送服务器地址，请等待!");
		int dl=2000;
		dl+=1000*reAuthorizeCnt++;
		try {
			Thread.sleep(dl);
		} catch (Exception e) {
		}
		if(reAuthorizeCnt>30)reAuthorizeCnt=30;
		receiver.request.authorize();
	}

	@Override
	protected void serversynccfg(String message) {
		reAuthorizeCnt=0;
		String point = (String)message;
		String[] vals = point.split(":");
		String ip = vals[0];
		int port = pclient.parseInt(vals[1],-1);
		debug("Receiver get point ok,point:"+point);
		receiver.regesterServer(ip, port);
	}

	@Override
	protected void receiveString(String[] head, String message) {
//		String msg = message;
//		String posstr = head[head.length-1];
//		if(posstr!=null){
//			int pos = pclient.parseInt(posstr,-1);
//			if(pos>=0){
//				if(pos>=message.length()){
//					msg = "";
//				}else {
//					msg = message.substring(pos);
//				}
//			}
//		}
//		debug("uid:"+receiver.request.getUuId()+",msg:"+msg);
	}

	@Override
	protected void receiveByte(String[] head, byte[] message) {
		/*
		String type = head[0];
		String pagestr = head[1];
		String sn = head[2];
		String posstr = head[head.length-1];
		byte[] data = null;
		if(posstr!=null){
			int pos = pclient.parseInt(posstr,-1);
			if(pos>=0&&pos<message.length){
				data = new byte[message.length-pos];
				System.arraycopy(message,pos,data,0,data.length);
			}
		}
		int page = 0;
		if(pagestr!=null){
			page = pclient.parseInt(pagestr,0);
		}
		if(data!=null&&page>=0){
			if(data!=null) {
				if (!pagecache.containsKey(sn)) {
					pagecache.put(sn, new HashMap<Integer, byte[]>());
				}
				pagecache.get(sn).put(page, data);
			}else{
				if(pagecache.containsKey(sn)) {
					Map<Integer,byte[]> datamap = pagecache.remove(sn);
					for (int i = 0; i < page; i++) {

					}
				}
			}
		}
		*/


	}

	public PSResponse(PSReceiver receiver) {
		this.receiver = receiver;
	}

	@Override
	public void debug(String msg) {
		ArmDelegate ad = (ArmDelegate)this.delegate;
		if (ad != null) {
			ad.debug(msg);
		}

	}

	@Override
	public void debug(String msg, Exception e) {
		ArmDelegate ad = (ArmDelegate)this.delegate;
		if (ad != null) {
			ad.debug(msg,e);
		}
	}

	@Override
	public void heart(int retry) {
		ArmDelegate ad = (ArmDelegate)this.delegate;
		if (ad != null) {
			ad.heart(retry);
		}
	}
}

package com.tensynchina.push.sdk.android;

import com.tensynchina.push.client.impl.PRequest;

public class PSRequest extends PRequest<PSResponse> {

	public PSRequest(String uuid) {
		super(uuid);
	}
	public void send(String sid,String vid ,String routeid,String touid,String method,String data){

		String head[] = new String[]{sid,vid,routeid,getUuId(),touid,method};
		String val = pclient.buildPushMsgBody(head,data);
		System.out.println("send string msg:"+val);
		super.send(2, 2, val);
	}
	public void send(String sid,String vid ,String routeid,String touid,String method,String type,String page,String sn,byte[] data){
		String head[] = new String[]{sid,vid,routeid,getUuId(),touid,method,type,page,sn};
		byte[] val = pclient.buildPushMsgBody(head,data);
		super.send(2, 3, val);
	}

}

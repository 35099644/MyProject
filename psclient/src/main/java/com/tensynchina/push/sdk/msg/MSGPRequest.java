package com.tensynchina.push.sdk.msg;

import com.tensynchina.push.client.impl.PRequest;

public class MSGPRequest extends PRequest<MSGPResponse> {

	public MSGPRequest(String uuid) {
		super(uuid);
		this.taghigh = 2<<4;
	}
//	public void send(int tag,int kind,String val){
//		msg.sender(tag, kind, val);
//	}
	public void send(int tag,int kind,String token,String timeout,String sid,String vid ,String routeid,String touid,String method,String data){
		String head[] = new String[]{token,timeout,sid,vid,routeid,touid,method};
		String val = pclient.buildPushMsgBody(head,data);
		System.out.println("send string msg:"+val);
		super.send(tag, kind, val);
	}
	public void send(int tag,int kind,String token,String timeout,String sid,String vid ,String routeid,String touid,String method,String type,String page,String sn,byte[] data){
		String head[] = new String[]{token,timeout,sid,vid,routeid,touid,method,type,page,sn};
		byte[] val = pclient.buildPushMsgBody(head,data);
		super.send(tag, kind, val);
	}
	public String buildUrlParams(){
		String pms = "";
		if(params()!=null){
			for(String key:params().keySet()){
				if("token".equals(key))continue;
				if(pms.length()==0)pms=key+"="+params().get(key);
				else
					pms+="&"+key+"="+params().get(key);
			}
		}
		return pms;
	}
}

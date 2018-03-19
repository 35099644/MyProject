package com.tensynchina.push.sdk.msg;


import com.tensynchina.push.client.Kind;
import com.tensynchina.push.client.impl.AbstractPResponse;

import java.util.HashMap;
import java.util.Map;

public class MSGPResponse extends AbstractPResponse<MSGPRequest> {
	private String cfg;
	private boolean configisok=false;
	private MsgProvider provider;
	private Map<String,Map<Integer,byte[]>> pagecache = new HashMap<String,Map<Integer, byte[]>>();

	@Override
	protected void authorized() {
		debug("Msg Provider 发送config信息!");
		pclient.request().send(pclient.request().buildTag(2), Kind.STRING.ordinal(),"tk="+pclient.request().getToken()+"&cfg="+cfg+"&"+pclient.request().buildUrlParams());
		Thread t = new Thread(waitcfgok);
		t.setDaemon(true);
		t.start();
	}

	@Override
	protected void authorizefailed() {
		debug("Msg Provider 提供的config 信息异常，不能通过PushService注册验证!");
	}

	@Override
	protected void serverready() {
		debug("Msg Provider 通过PushService注册验证!");
		configisok=true;
	}

	@Override
	protected void serversynccfg(String message) {
		String str=message;
		Map<String,String> params = MsgProvider.parseParams(str);
		String[] vals = params.get("point").split(":");
		String ip = vals[0];
		int[] sids=null;
		int[] vids=null;
		int port = pclient.parseInt(vals[1],-1);
		vals = params.get("sids").split(",");
		if(vals!=null){
			sids = new int[vals.length];
			for(int i=0;i<sids.length;i++){
				sids[i]=pclient.parseInt(vals[i],0);
			}
		}
		vals = params.get("vids").split(",");
		if(vals!=null){
			vids = new int[vals.length];
			for(int i=0;i<vids.length;i++){
				vids[i]=pclient.parseInt(vals[i],0);
			}
		}

		if(port>0){
			provider.regesterServer(ip, port,sids,vids);
		}
		debug("provider receive:"+str);
	}

	@Override
	protected void receiveString(String[] head, String message) {
		String sid = head[0];
		String vid = head[1];
		String routeid = head[2];
		String fuid = head[3];
		String tuid = head[4];
		String md = head[5];
		String msg = message;
		String posstr = head[head.length-1];
		if(posstr!=null){
			int pos = pclient.parseInt(posstr,-1);
			if(pos>=0){
				if(pos>=message.length()){
					msg = "";
				}else {
					msg = message.substring(pos);
				}
			}
		}
		debug("provider recv msg:"+msg);
	}

	@Override
	protected void receiveByte(String[] head, byte[] message) {
		String sid = head[0];
		String vid = head[1];
		String routeid = head[2];
		String fuid = head[3];
		String tuid = head[4];
		String md = head[5];
		String type = head[6];
		String pagestr = head[7];
		String sn = head[8];
		int startpos = pclient.parseInt(head[head.length-1],0);
		byte[] datas=null;
		if(startpos>0&&startpos<message.length){
			datas = new byte[message.length-startpos];
			System.arraycopy(message,startpos,datas,0,datas.length);

		}

		int page = 0;
		if(pagestr!=null){
			page = pclient.parseInt(pagestr,0);
		}
		if(page>=0){
			byte[] pushmsg = pclient.buildPushMsgBody(new String[]{pclient.request().getToken(),"0",md,sid,fuid,tuid,type,""+page,sn},datas);
			pclient.request().send(pclient.request().buildTag(1),Kind.BYTES.ordinal(),pushmsg);
		}

	}

	private Runnable waitcfgok=new Runnable() {
		@Override
		public void run() {
			try {
				Thread.sleep(5000);
			} catch (Exception e) {
			}
			if(!configisok){
				pclient.tryrestart();
			}
		}
	};
	public String getCfg() {
		return cfg;
	}
	public MSGPResponse(String cfg,MsgProvider provider) {
		this.cfg = cfg;
		this.provider = provider;
	}

	@Override
	public void debug(String msg, Exception e) {
		//logger.error(msg, e);
		System.out.println(msg);
	}
	@Override
	public void debug(String msg) {
		//logger.debug(msg);
		System.out.println(msg);
	}

	@Override
	public void heart(int retry) {
		//debug("heart retry:"+retry);
	}
}

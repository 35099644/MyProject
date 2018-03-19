package com.tensynchina.push.sdk.msg;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tensynchina.push.client.IPClient;
import com.tensynchina.push.client.IPSaver;
import com.tensynchina.push.client.impl.PushClientManager;

public class MsgProvider {
	private PushClientManager pcm;
	protected MSGPRequest request;
	protected MSGPResponse response;
	private static MsgProvider provider;
//	private String cfg="";
//	private ObjectMapper om;
	private String masterhost;
	private int masterport;
	private MSGCache msgcache;
	private Map<String,IPClient<MSGPRequest,MSGPResponse>> pclients;
	protected IPClient<MSGPRequest,MSGPResponse> masterclient;
	private void init(String token){
		pclients = new HashMap<String,IPClient<MSGPRequest,MSGPResponse>>();
		msgcache = new MSGCache();
		String cfg="{\"token\":\"tk00005\",";
		cfg+="\"prefixcount\":3,\"version\":\"1.0\",";
		cfg+="\"funcs\":{";
			cfg+="\"check\":{";
					cfg+="\"pnames\":[";
						cfg+="\"p:range\",\"sids\"";
						cfg+="],";
					cfg+="\"in\":[";
							cfg+="{\"idx\":0,\"type\":1,\"expr\":{\"split\":\",\"}},";
							cfg+="{\"idx\":0,\"type\":1,\"expr\":{\"split\":\",\",\"sidx\":1}},";
							cfg+="{\"idx\":1,\"type\":3,\"expr\":{\"split\":\",\",\"eidx\":-1}}";
							cfg+="],";
					cfg+="\"oper\":[";
							cfg+="{\"op\":\"ge\",\"left\":2,\"right\":0},";
							cfg+="\"and\",";
							cfg+="{\"op\":\"le\",\"left\":2,\"right\":1}]";
					
					
					cfg+="},";
//			cfg+="\"downhit\":{";
//					cfg+="\"pnames\":[";
//							cfg+="\"vids\",\"_\",\"sids\"";
//							cfg+="],";
//					cfg+="\"in\":[";
//							cfg+="{\"idx\":0,\"type\":3,\"expr\":{\"split\":\",\",\"eidx\":-1}},";
//							cfg+="{\"idx\":1,\"type\":0,\"expr\":{\"split\":\":\",\"sidx\":1}},";
//							cfg+="{\"idx\":1,\"type\":0,\"expr\":{\"split\":\":\",\"sidx\":0}},";
//							cfg+="{\"idx\":2,\"type\":3,\"expr\":{\"split\":\",\",\"eidx\":-1}},";
//							cfg+="{\"idx\":-1,\"type\":3,\"value\":\"0\"}";
//							cfg+="],";
//					cfg+="\"oper\":[";
//							cfg+="\"(\",";
//							cfg+="{\"op\":\"in\",\"left\":1,\"right\":0},";
//							cfg+="\")\",";
//							cfg+="\"or\",";
//							cfg+="\"(\",";
//							cfg+="{\"op\":\"in\",\"left\":2,\"right\":3},";
//							cfg+="\"and\",";
//							cfg+="{\"op\":\"eq\",\"left\":0,\"right\":4},";
//							cfg+="\")\"";
//							cfg+="]";
//					cfg+="},";
			cfg+="\"downhit\":{\n" +
					"\"pnames\":[\"uid\",\"_\"]," +
					"\"in\":[\n" +
					"{\"idx\":0,\"type\":0,\"expr\":{\"split\":\",\",\"eidx\":-1}}," +
					"{\"idx\":1,\"type\":0,\"expr\":{\"split\":\":\",\"sidx\":2}}" +
					"]," +
					"\"oper\":[{\"op\":\"eq\",\"left\":1,\"right\":0}]" +
					"},";
			cfg+="\"uphit\":{";
					cfg+="\"pnames\":[";
							cfg+="\"p:range\",\"_\"";
							cfg+="],";
					cfg+="\"in\":[";
							cfg+="{\"idx\":0,\"type\":1,\"expr\":{\"split\":\",\"}},";
							cfg+="{\"idx\":0,\"type\":1,\"expr\":{\"split\":\",\",\"sidx\":1}},";
							cfg+="{\"idx\":1,\"type\":1,\"expr\":{\"split\":\":\",\"sidx\":0}}";
							cfg+="],";
					cfg+="\"oper\":[";
							cfg+="{\"op\":\"ge\",\"left\":2,\"right\":0},";
							cfg+="\"and\",";
							cfg+="{\"op\":\"le\",\"left\":2,\"right\":1}";
							cfg+="]";
					cfg+="}";
			cfg+="}";
		cfg+="}";
//		om = new ObjectMapper();
//		try {
//			JsonNode jnode = om.readTree(cfg);
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
		
		try {
			pcm = PushClientManager.getInstance(new IPSaver() {
				@Override
				public void save(String key, byte[] data) {
				}
				@Override
				public byte[] get(String key) {
					return null;
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.request.initAddress(masterhost, masterport);
		response = new MSGPResponse(cfg,this);
		masterclient = pcm.buildClient(request, response);
		//response.setIPClient(masterclient);
		masterclient.setSyncdo(true);
		masterclient.start();
	}
	public void tostart(){
		this.init(this.request.getToken());
	}
	public int parseInt(String v,int defaultval){
		try {
			return Integer.parseInt(v);
		} catch (Exception e) {
		}
		return defaultval;
	}
	protected void regesterServer(String ip,int port,int[] sids,int[] vids){
		//ip:sids
		String point=ip+":"+port;
		if(!pclients.containsKey(point)){
			MSGPRequest r = new MSGPRequest(this.request.getUuId());
			this.request.copyto(r);
			r.initAddress(ip, port);
			MSGPResponse res = new MSGPResponse(response.getCfg(),this);
			IPClient<MSGPRequest,MSGPResponse> ipc = pcm.buildClient(r, res);
			//res.setIPClient(ipc);
			pclients.put(point,ipc);
			ipc.setSyncdo(true);
			ipc.start();
		}
		System.out.println("Provider start one ipclient.");
		if(sids!=null)
		for(int sid:sids){
			msgcache.cache(sid, point);
		}
		if(vids!=null)
		for(int vid:vids){
			msgcache.cache(point,vid);
		}
		System.out.println("msgcache:"+msgcache.toString());
	}
	private MsgProvider(String token,String uuid,String masterhost,int masterport){
		this.request = new MSGPRequest(uuid);
		this.request.setToken(token);
		this.masterhost = masterhost;
		this.masterport = masterport;
		
	}
	public static MsgProvider getInstance(String token,String uuid,String masterhost,int masterport)throws Exception {
		if (provider == null) {
			synchronized (MsgProvider.class) {
				if (provider == null) {
					provider = new MsgProvider(token,uuid,masterhost,masterport);
				}
			}
		}
		return provider;
	}
	public void shutdown() {
		if(masterclient!=null){
			masterclient.shutdown();
		}
		if(pclients!=null){
			for(IPClient<MSGPRequest,MSGPResponse> pc:pclients.values()){
				pc.shutdown();
			}
			pclients.clear();
		}
	}
	public static Map<String,String> parseParams(String str){
		return parseParams(null,str);
	}
	public static Map<String,String> parseParams(Map<String,String> params,String str){
		if(params==null)params = new HashMap<String,String>();
		if(str!=null){
			String[] d1=str.split("&");
			for(int i=0;i<d1.length;i++){
				if(d1[i]==null||d1[i].length()<=1)continue;
				String[] vv = d1[i].split("=");
				if(vv.length>1)params.put(vv[0], vv[1]);
				else params.put(vv[0], "");
			}
		}
		return params;
	}
	public static String[] spliptPushMsgBody(String body){
		if(body==null)return null;
		int l = body.length();
		int rspos=0;
		int cnt=5;
		int pos=0;
		int lastpos=0;
		String[] rs = new String[cnt];
		while(rspos<cnt&&pos<l){
			if(body.charAt(pos)==':'){
				rs[rspos]=body.substring(lastpos,pos);
				lastpos=pos+1;
				rspos++;
			}
			pos++;
		}
		return rs;
	}
	public static String buildPushMsgBody(String token,int serverid,int vehicleid,int cachetimeout,String msg){
		if(msg==null)return token+":"+cachetimeout+"m:"+serverid+":"+vehicleid;
		else return token+":"+cachetimeout+"m:"+serverid+":"+vehicleid+":"+msg;
	}
	public static void main(String[] args) {
		MsgProvider mp = null;
		byte[] cs = new byte[128];
		boolean run = true;
		Thread t= null;
		while (run) {
			String info = null;
			try {
				InputStream is = System.in;
				is.read(cs);
				int le = cs.length;
				for (int i = 0; i < cs.length; i++) {
					if (cs[i] == '\n' || cs[i] == '\r') {
						le = i;
						break;
					}
				}
				byte[] cmds = new byte[le];
				System.arraycopy(cs, 0, cmds, 0, le);
				info = new String(cmds);
			} catch (Exception e) {
			}
			System.out.println(info);
			if ("exit".equalsIgnoreCase(info)) {
				run = false;
				if (mp != null) {
					mp.shutdown();
				}
				break;
			} else if ("start".equalsIgnoreCase(info)) {
				try {
					mp = MsgProvider.getInstance("tk00005", "tk00005", "172.16.24.44", 19999);
//					mp = MsgProvider.getInstance("tk00005", "tk00005", "127.0.0.1", 19999);
					mp.request.add("range", "3,5");
					mp.tostart();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if(info.startsWith("asend:")){
				if(t==null&&mp!=null){
					final String msg = info.substring(6);
					final MsgProvider _mp = mp;
					t = new Thread(new Runnable() {
						int r = (int)(Math.random()*3)+1;
						@Override
						public void run() {
							while(true){
								String tk="tk00005";
								int sid=2;
								int vid=50001;
								int timeout=5;
								List<String> iplist = _mp.msgcache.checkPServer(sid, vid);
								if(iplist!=null)
								for(String point:iplist){
									IPClient<MSGPRequest,MSGPResponse> pclient = _mp.pclients.get(point);
									System.out.println("asend pclient:" + pclient);
									if(pclient!=null){
										pclient.request().send(pclient.request().buildTag(1), 2, buildPushMsgBody(tk,sid,vid,timeout,msg+">"+System.currentTimeMillis()));
									}
								}
								try {
									Thread.sleep(r*500);
								} catch (Exception e) {
								}
							}
						}
					});
					t.setDaemon(true);
					t.start();
				}
			}else{
				
				
				if (info.startsWith("send:")) {
					String msg = info.substring(5);
					if (mp != null) {
						//msgbody: sid:vid:timeout:msg
						String tk="tk00005";
						int sid=2;
						int vid=50001;
						int timeout=5;
						List<String> iplist = mp.msgcache.checkPServer(sid, vid);
						if(iplist!=null)
						for(String point:iplist){
							IPClient<MSGPRequest,MSGPResponse> pclient = mp.pclients.get(point);
							if(pclient!=null){
								pclient.request().send(pclient.request().buildTag(1), 2, buildPushMsgBody(tk,sid,vid,timeout,msg+System.currentTimeMillis()));
							}
						}
						//String[] val = msg.split(",");
						System.out.println("TcpClient.main.cmd:" + msg);
						
					}
				}
			}
		}

	}
}

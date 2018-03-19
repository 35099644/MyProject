package com.tensynchina.push.sdk.android;

import android.util.Log;

import com.tensynchina.push.client.IPClient;
import com.tensynchina.push.client.IPSaver;
import com.tensynchina.push.client.ResponseDelegate;
import com.tensynchina.push.client.impl.PClient;
import com.tensynchina.push.client.impl.PushClientManager;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author susy
 *
 */
public class PSReceiver {
	private PushClientManager pcm;
	protected PSRequest request;
	private static PSReceiver receiver;
	private String uuid;
	private String masterhost;
	private PClient<PSRequest,PSResponse> psclient;
	private PClient<PSRequest,PSResponse> masterclient;
	private int masterport;
	private List<Integer> serverIdList;
	private ResponseDelegate delegate;
	public PSReceiver(String token,String uuid,String masterhost,int masterport) {
		this.uuid = uuid;
		this.request = new PSRequest(this.uuid);
		this.request.setToken(token);
		this.masterhost = masterhost;
		this.masterport = masterport;
		init();
	}

	public PSRequest getRequest() {
		return request;
	}

	public PClient<PSRequest, PSResponse> getPsclient() {
		return psclient;
	}
	public void setDelegate(ResponseDelegate delegate){
		this.delegate = delegate;
		if(psclient!=null){
			psclient.response().setDelegate(this.delegate);
		}
	}
	public static PSReceiver getInstance(String token, String uuid, String masterhost, int masterport)throws Exception {
		if (receiver == null) {
			synchronized (PSReceiver.class) {
				if (receiver == null) {
					receiver = new PSReceiver(token,uuid,masterhost,masterport);
				}
			}
		}
		return receiver;
	}

	public static void destoryInstance() {
		if (receiver != null) {
			synchronized (PSReceiver.class) {
				if (receiver != null) {
					receiver.shutdown();
					receiver = null;
				}
			}
		}
	}
	protected void regesterServer(String ip,int port){
        if(psclient==null){
			PSRequest r = new PSRequest(this.request.getUuId());
			this.request.copyto(r);
			r.initAddress(ip, port);
			PSResponse response = new PSResponse(this);
			response.setDelegate(this.delegate);
			psclient = pcm.buildClient(r, response);
			psclient.setSyncdo(true);
			psclient.start();
			masterclient.shutdown();
		}else{
			psclient.changeAddress(ip, port);
		}

		//System.out.println("Provider start one ipclient.");
	}
	private void init(){
		serverIdList = new ArrayList<Integer>();
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
		
	}
	public void sendmsg(int sid,int vid,String touid,String msg){
		sendmsg(sid,vid,2,touid,"dhbyuid",msg);
	}
	public void sendmsg(int sid,int vid,String touid,String method,String msg){
		sendmsg(sid,vid,2,touid,method,msg);
	}
	public void sendmsg(int sid,int vid,int route,String touid,String method,String msg){
		if(psclient!=null){
			//String [] head = new String[]{};
			psclient.request().send(""+sid,""+vid,""+route,touid,method,msg);
		}
	}
	public void addServerID(int serverid){
		serverIdList.add(serverid);
	}
	public void tostart(){
		Log.d("PushService","toStart from PSReceiver ..");
		this.request.initAddress(masterhost, masterport);
		if(serverIdList.size()>0){
			String sids = null;
			for(Integer sid:serverIdList){
				if(sids==null)sids=""+sid;
				else sids+=","+sid;
			}
			this.request.add("sids", sids);
		}
		PSResponse response = new PSResponse(this);
		masterclient = pcm.buildClient(request, response);
		masterclient.setSyncdo(true);
		masterclient.start();
		//System.out.println("Rec masterclient start ok.");
	}
	public void shutdown() {
		if(masterclient!=null){
			masterclient.shutdown();
		}
		if(psclient!=null){
			psclient.shutdown();
		}
	}

}

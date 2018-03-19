package com.tensynchina.push.client.impl;

import java.util.HashMap;
import java.util.Map;

import com.tensynchina.push.client.IPRequest;
import com.tensynchina.push.client.IPResponse;
import com.tensynchina.push.client.Kind;
import com.tensynchina.push.client.tcp.CMsg;

/**
 * PC客户端~请求数据
 * @author susy
 *
 * @param <RET>
 */
public class PRequest<RET extends IPResponse> implements IPRequest{
	public PRequest(String uuid) {
		this.uuid = uuid;
	}
	protected PClient<PRequest,AbstractPResponse> pclient;


	private String host;
	private int port;
	private Map<String,Object> params;
	protected CMsg<PRequest<RET>,RET> msg;
	protected int taghigh;
	private String uuid;
	protected int cachetimeout=0;



	/*
         * (non-Javadoc)
         * @see com.tensynchina.push.client.IPRequest#add(java.lang.String, java.lang.String)
         */
	@Override
	public synchronized void add(String key, String value) {
		if(params==null)params=new HashMap<String,Object>();
		params.put(key, value);
	}
	/*
	 * (non-Javadoc)
	 * @see com.tensynchina.push.client.IPRequest#add(java.lang.String, int)
	 */
	@Override
	public synchronized void add(String key, int value) {
		if(params==null)params=new HashMap<String,Object>();
		params.put(key, value);
	}
	public Map<String,Object> params(){
		return params;
	}
	public int getCachetimeout() {
		return cachetimeout;
	}

	public void setCachetimeout(int cachetimeout) {
		this.cachetimeout = cachetimeout;
	}
	/*
	 * (non-Javadoc)
	 * @see com.tensynchina.push.client.IPRequest#remove(java.lang.String)
	 */
	@Override
	public synchronized void remove(String key) {
		if(params==null)return;
		params.remove(key);
	}
	/*
	 * (non-Javadoc)
	 * @see com.tensynchina.push.client.IPRequest#setToken(java.lang.String)
	 */
	@Override
	public void setToken(String token) {
		if(params==null)params=new HashMap<String,Object>();
		params.put("token", token);
	}
	/*
	 * (non-Javadoc)
	 * @see com.tensynchina.push.client.IPRequest#host()
	 */
	@Override
	public String host() {
		return host;
	}
	/*
	 * (non-Javadoc)
	 * @see com.tensynchina.push.client.IPRequest#port()
	 */
	@Override
	public int port() {
		return port;
	}

	void setMsg(CMsg<PRequest<RET>,RET> msg) {
		this.msg = msg;
	}
	/*
	 * (non-Javadoc)
	 * @see com.tensynchina.push.client.IPRequest#getToken()
	 */
	@Override
	public String getToken() {
		return (String)params.get("token");
	}
	/*
	 * (non-Javadoc)
	 * @see com.tensynchina.push.client.IPRequest#getStringParam(java.lang.String)
	 */
	@Override
	public String getStringParam(String key) {
		// TODO Auto-generated method stub
		return (String)params.get(key);
	}
	/*
	 * (non-Javadoc)
	 * @see com.tensynchina.push.client.IPRequest#getIntParam(java.lang.String)
	 */
	@Override
	public int getIntParam(String key) {
		Integer v = (Integer)params.get(key);
		if(v==null)return 0;
		return v;
	}

	/*
	 * (non-Javadoc)
	 * @see com.tensynchina.push.client.IPRequest#buildTag(int)
	 */
	@Override
	public byte buildTag(int tag) {
		return (byte)(taghigh|tag);
	}
	/*
	 * (non-Javadoc)
	 * @see com.tensynchina.push.client.IPRequest#getUuId()
	 */
	@Override
	public String getUuId() {
		return this.uuid;
	}
	/*
	 * (non-Javadoc)
	 * @see com.tensynchina.push.client.IPRequest#authorize()
	 */
	@Override
	public void authorize() {
		if(msg!=null)
		msg.sender(buildTag(0), Kind.STRING.ordinal(),authorizeInfo());
	}
	private String authorizeInfo() {
//		String authorize = getStringParam("authorize");
		String pms = "";
		if(params!=null){
			for(String key:params.keySet()){
				if("token".equals(key)||"hc".equals(key)||"tk".equals(key))continue;
				pms+="&"+key+"="+params.get(key);
			}
		}
		return "tk="+this.getToken()+"&hc="+this.uuid+"&uid="+this.uuid+pms;
	}
	/*
	 * (non-Javadoc)
	 * @see com.tensynchina.push.client.IPRequest#initAddress(java.lang.String, int)
	 */
	@Override
	public void initAddress(String host, int port) {
		this.host=host;
		this.port=port;
	}
	/*
	 * (non-Javadoc)
	 * @see com.tensynchina.push.client.IPRequest#copyto(com.tensynchina.push.client.IPRequest)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <T extends IPRequest> void copyto(T request) {
		PRequest<RET> nr = (PRequest<RET>)request;
		nr.taghigh=this.taghigh;
		nr.host=this.host;
		nr.port=this.port;
		if(params!=null){
			if(nr.params==null)nr.params=new HashMap<String,Object>();
			for(String k:params.keySet()){
				nr.params.put(k, params.get(k));
			}
		}
	}
	@Override
	public void syncparams() {
		if(msg!=null)
			msg.sender(buildTag(4), Kind.STRING.ordinal(),authorizeInfo());
	}
	//2:string ,3:byte
	public void send(int tag,int kind,String data){
		msg.sender(tag, kind, data);
	}
	public void send(int tag,int kind,byte[] data){
		msg.sender(tag, kind, data);
	}
	public void setPclient(PClient<PRequest, AbstractPResponse> pclient) {
		this.pclient = pclient;
	}


}

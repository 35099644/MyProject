package com.tensynchina.push.client.impl;

import android.util.Log;

import java.util.concurrent.ConcurrentLinkedQueue;

import com.tensynchina.push.client.IPClient;
import com.tensynchina.push.client.IPRequest;
import com.tensynchina.push.client.IPResponse;
import com.tensynchina.push.client.Kind;
import com.tensynchina.push.client.tcp.CMsg;
import com.tensynchina.push.client.tcp.TCPClient;

/**
 * PC客户端对象
 * @author susy
 *
 * @param <RT> 请求对象类型
 * @param <RET> 应答回调对象类型
 */
public class PClient<RT extends IPRequest,RET extends IPResponse> implements IPClient<RT,RET>{
	/**
	 * 最大连接数
	 */
	private int maxcount = 500;
	private ConcurrentLinkedQueue<Object[]> msgqueue;
	private boolean sleeping;
	/**
	 * 工作线程
	 */
	private Thread worker;
	private boolean running;
	private int counter;
	private RT request;
	private RET response;
	private PushClientManager pcm;
	private boolean syncdo;
	private TCPClient<RT,RET> tcpclient;
	private Runnable workrunner=new Runnable() {
		@Override
		public void run() {
			long delay = 30000;
			while(running){
				long t = delay;
				try {
					//final String msg = msgqueue.poll();
					final Object[] datas = msgqueue.poll();
					counter--;
					if(datas!=null&&datas.length==3&&datas[1]!=null){
						int tag = (Integer)datas[0];
						Kind kind = (Kind)datas[1];
						response().receive(tag, kind, datas[2]);
						t=10;
					}else{
						t = delay;
						if(msgqueue.isEmpty())counter=0;
					}
				} catch (Exception e) {
					response().debug("writer run failed.",e);
				}
				if(t>0)tosleep(t);
			}
			
		}
	};
	/**
	 * @param request 请求信息对象
	 * @param response 应答回调对象
	 * @param pcm 
	 */
	PClient(RT request, RET response,PushClientManager pcm) {
		this.request = request;
		this.response = response;
		this.pcm = pcm;
	}
	/*
	 * (non-Javadoc)
	 * @see com.tensynchina.push.client.IPClient#start()
	 */
	@Override
	public void start(){
		//response.debug("start from PClient!! running = " + running);
		Log.d("PushService","start from PClient!! running = " + running);
		if(running)return;
		running = true;
		if(msgqueue==null){
			msgqueue = new ConcurrentLinkedQueue<Object[]>();
		}
		if(worker==null&&!syncdo){
			worker = new Thread(workrunner);
			worker.setDaemon(true);
			worker.start();
		}
		if(tcpclient!=null){
			tcpclient.tostart();
		}
		pcm.getSaver();
	}
	void setTcpClient(TCPClient<RT,RET> tcpclient){
		this.tcpclient = tcpclient;
	}
	private void tosleep(long t){
		try {
			sleeping=true;
			Thread.sleep(t);
		} catch (Exception e){
		}
		sleeping=false;
	}
	/**
	 * 触发回调
	 * @param datas 应答数据(已经部分解析)
	 */
	public void push(Object[] datas){
		if(syncdo){
			if(datas!=null&&datas.length==3&&datas[1]!=null){
				int tag = (Integer)datas[0];
				Kind kind = (Kind)datas[1];
				response().receive(tag, kind, datas[2]);
			}
		}else{
			if(counter>maxcount){
				Object[] _d = msgqueue.poll();
				int tag = (Integer)_d[0];
				Kind k = (Kind)_d[1];
				response().overflow(tag,k,_d[2]);
			}else{
				counter++;
			}
			msgqueue.offer(datas);
			tonotify();
		}
	}
	/*
	 * (non-Javadoc)
	 * @see com.tensynchina.push.client.IPClient#changeAddress(java.lang.String, int)
	 */
	@Override
	public void changeAddress(String host,int port){
		if(this.tcpclient!=null){
			this.tcpclient.changeAddress(host, port);
		}
	}
	private void tonotify(){
		/*if(worker!=null&&sleeping){
			worker.interrupt();
		}*/
		worker.interrupt();
	}
	@Override
	public RET response() {
		return response;
	}
	/*
	 * (non-Javadoc)
	 * @see com.tensynchina.push.client.IPClient#request()
	 */
	@Override
	public RT request() {
		return request;
	}
	/*
	 * (non-Javadoc)
	 * @see com.tensynchina.push.client.IPClient#shutdown()
	 */
	@Override
	public void shutdown() {
		response.debug("shutdown from PClient ......");
		running = false;
		if(worker!=null){
			this.tonotify();
		}
		if(tcpclient!=null){
			tcpclient.shutdown();
		}
	}
	/*
	 * (non-Javadoc)
	 * @see com.tensynchina.push.client.IPClient#ready()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void ready() {
		if(this.request instanceof PRequest){
			PRequest<RET> pr = (PRequest<RET>)this.request;
			pr.setMsg((CMsg<PRequest<RET>,RET>)tcpclient.getMsgbody());
		}
	}
	/*
	 * (non-Javadoc)
	 * @see com.tensynchina.push.client.IPClient#setSyncdo(boolean)
	 */
	public void setSyncdo(boolean syncdo) {
		this.syncdo = syncdo;
	}
	/*
	 * (non-Javadoc)
	 * @see com.tensynchina.push.client.IPClient#tryrestart()
	 */
	@Override
	public void tryrestart() {
		tcpclient.willreconnect();
	}
	public String[] spliptPushMsgBody(byte[] body, int start, int count) {
		if (body == null || count == 0) return null;
		int l = body.length;
		char stopc = '#';
		int rspos = 0;
		int cnt = count;
		int pos = 0;
		int lastpos = 0;
		String[] rs = new String[cnt+1];
		while (rspos < cnt + start && pos < l) {
			if (body[pos] == stopc) {
				if(lastpos<pos){
					if (rspos >= start ) {
						rs[rspos - start] = new String(body, lastpos, pos - lastpos);
					}
				}
				pos++;
				break;
			} else if (body[pos] == ':') {
				if (rspos >= start ) {
					if(lastpos==pos){
						rs[rspos - start] ="";
					}else {
						rs[rspos - start] = new String(body, lastpos, pos - lastpos);
					}
				}
				lastpos = pos + 1;
				rspos++;
				if (pos+1<l&&body[pos+1] == stopc) {
					pos+=2;
					break;
				}
			}
			pos++;
		}
		rs[rs.length - 1] = ""+pos;
		return rs;
	}
	public int parseInt(String v,int defaultValue){
		if(v==null)return defaultValue;
		try {
			return Integer.parseInt(v);
		} catch (Exception e) {
			return defaultValue;

		}
	}
	public String[] spliptPushMsgBody(String body, int start, int count) {
		if (body == null || count == 0) return null;
		int l = body.length();
		char stopc = '#';
		int rspos = 0;
		int cnt = count;
		int pos = 0;
		int lastpos = 0;
		String[] rs = new String[cnt+1];
		while (rspos < cnt + start && pos < l) {
			if (body.charAt(pos) == stopc) {
				if(lastpos<pos){
					if (rspos >= start && rspos - start<cnt) {
						rs[rspos - start] = body.substring(lastpos, pos);
					}
				}
				pos++;
				break;
			} else if (body.charAt(pos) == ':') {
				if (rspos >= start) {
					if(lastpos==pos){
						rs[rspos - start] ="";
					}else {
						rs[rspos - start] = body.substring(lastpos, pos);
					}
				}
				lastpos = pos + 1;
				rspos++;
				if (pos+1<l&&body.charAt(pos+1) == stopc) {
					pos+=2;
					break;
				}
			}
			pos++;
		}
		rs[rs.length - 1] = ""+pos;
		return rs;
	}
	public String buildPushMsgBody(String [] head,String msg){
		if(head==null||head.length==0)return null;
		StringBuffer sb = new StringBuffer();
		sb.append('#');
		for(String h : head){
			sb.append(h);
			sb.append(':');
		}
		if(msg==null)return sb.toString();
		else return sb.toString()+"#"+msg;
	}
	protected byte[] char2byte(int start,char[] src,byte[] datas){
		for(int i=0;i<src.length;i++){
			datas[start+i]=(byte)(src[i]&0xff);
		}
		return datas;
	}
	public byte[] buildPushMsgBody(String [] head,byte[] msg){
		//if(method==null)method="";
		if(head==null||head.length==0)return null;
		StringBuffer sb = new StringBuffer();
		sb.append('#');
		for(String h : head){
			sb.append(h);
			sb.append(':');
		}
		if(msg==null){

			String header = sb.toString();//token+":"+cachetimeout+"m:"+method+":"+sid+":"+matchparam+":"+fuid+":"+type+":"+page+":"+sn+":";
			//int l = header.length();
			//byte[] hdata = new byte[l];
			//sb.toString().getBytes();
			//char2byte(0,header.toCharArray(),hdata);
			return header.getBytes();
		}
		else{

			String header = sb.toString()+"#";//token+":"+cachetimeout+"m:"+method+":"+sid+":"+matchparam+":"+fuid+":"+type+":"+page+":"+sn+":#";
			int l = header.length();
			byte[] alldata = new byte[l+msg.length];
			System.arraycopy(msg,0,alldata,l,msg.length);
			char2byte(0,header.toCharArray(),alldata);

			return alldata;
		}
	}
}

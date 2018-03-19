package com.tensynchina.push.client.impl;

import com.tensynchina.push.client.IPClient;
import com.tensynchina.push.client.IPRequest;
import com.tensynchina.push.client.IPResponse;
import com.tensynchina.push.client.IPSaver;
import com.tensynchina.push.client.tcp.TCPClient;
/**
 * 输出客户端~
 * @author susy
 *
 */
final public class PushClientManager {
	private IPSaver saver;
	//private PClient pclient;
	//TCPClient tcpclient;

	public IPSaver getSaver() {
		return saver;
	}

	/**
	 * @uml.property name="manager"
	 */
	private static PushClientManager manager;

	private PushClientManager(IPSaver saver)throws Exception  {
		this.saver = saver;
		if(this.saver==null){
			throw new Exception("IPSaver 不允许为空!");
		}
	}

	/**
	 */
	public static PushClientManager getInstance(IPSaver saver)throws Exception {
		if (manager == null) {
			synchronized (PushClientManager.class) {
				if (manager == null) {
					manager = new PushClientManager(saver);
				}
			}
		}
		return manager;
	}

	/**
	 * 创建客户端对象实例
	 * @param request 请求信息对象
	 * @param response 应答回调对象
	 * @return 客户端对象实例
	 */
	public <RT extends PRequest,RET extends AbstractPResponse> PClient<RT,RET> buildClient(RT request, RET response) {
		//if(pclient==null){
			PClient<RT,RET> pclient = new PClient<RT,RET>(request,response,this);
			TCPClient<RT,RET> tcpclient = new TCPClient<RT,RET>(pclient,this);
			pclient.setTcpClient(tcpclient);
			response.setPclient(pclient);
			request.setPclient(pclient);
			
		//}
		return pclient;
	}


}

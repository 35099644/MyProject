package com.tensynchina.push.client;
/**
 * 客户端接口类
 * @author susy
 *
 * @param <RT> 请求对象
 * @param <RET> 返回对象
 */
public interface IPClient<RT extends IPRequest,RET extends IPResponse>{
	/**
	 * 连接线程启动后触发
	 */
	public void ready();
	/**
	 * 启动数据交互线程
	 */
	public void start();
	/**
	 * 重新尝试重新启动数据交互线程
	 */
	public void tryrestart();

	/**
	 * 请求信息对象
	 */
	public RET response();

	/**
	 * 应答回调对象
	 */
	public RT request();
	/**
	 * JVM或应用退出前执行，释放资源
	 */
	public void shutdown();
	/**
	 * 同步处理应答数据(默认false)
	 * @param syncdo
	 */
	public void setSyncdo(boolean syncdo);
	/**
	 * 即刻修改连接地址
	 * @param host 服务端ip地址
	 * @param port 服务端端口号
	 */
	public void changeAddress(String host, int port);
}

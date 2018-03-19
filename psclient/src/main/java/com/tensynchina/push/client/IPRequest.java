package com.tensynchina.push.client;
/**
 * 接受的数据 接口类
 * @author susy
 *
 */
public interface IPRequest {

	/**
	 * 添加全局参数(尽量少)
	 * @param key
	 * @param value
	 */
	public void add(String key, String value);
	/**
	 * 添加全局参数(尽量少)
	 * @param key
	 * @param value
	 */
	public void add(String key, int value);

	/**
	 * 复制属性信息到传入对象
	 * @param request
	 */
	public <T extends IPRequest> void copyto(T request);

	/**
	 * 去除一个全局参数
	 * @param key
	 */
	public void remove(String key);

	/**
	 * 返回初始设置的服务端IP
	 * @return
	 */
	public String host();
	/**
	 * 返回初始设置的服务端PORT
	 * @return
	 */
	public int port();
	/**
	 * 设置推送服务为客户提供的token(客户唯一标示，重要)
	 * @param token
	 */
	public void setToken(String token);
	/**
	 * 初始化服务端IP、PORT
	 * @param host
	 * @param port
	 */
	public void initAddress(String host, int port);
	/**
	 * 返回token
	 * @return
	 */
	public String getToken();
	/**
	 * 返回该客户端id(客户端唯一标示，重要)
	 * @return
	 */
	public String getUuId();
	/**
	 * 向服务端发送数据时指定操作类型tag值(分高位和低位)
	 * @param tag
	 * @return
	 */
	public byte buildTag(int tag);
	/**
	 * 获取字符串全局参数
	 * @param key
	 * @return
	 */
	public String getStringParam(String key);
	/**
	 * 获取int类型全局参数
	 * @param key
	 * @return
	 */
	public int getIntParam(String key);
//	public String authorizeInfo();
	/**
	 * 进行远程验证
	 */
	void authorize();
	void syncparams();
}

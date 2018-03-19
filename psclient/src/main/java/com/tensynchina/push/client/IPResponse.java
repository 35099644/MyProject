package com.tensynchina.push.client;

/**
 * 返回的数据对象 接口类
 * @author susy
 *
 */
public interface IPResponse {

		
		/**
		 * 有应答数据时该方法被调用
		 * @param tag 操作码
		 * @param kind 数据类型
		 * @param message 数据体
		 */
		public void receive(int tag, Kind kind, Object message);

		
		/**
		 * 输出调试信息
		 * @param msg
		 */
		public void debug(String msg);
		/**
		 * 输出调试信息
		 * @param msg
		 * @param e 异常
		 */
		public void debug(String msg, Exception e);
		/**
		 * 当使用异步应答数据处理时，数据缓冲区溢出时被调用
		 * @param tag 操作码
		 * @param kind 数据类型
		 * @param message 数据体
		 */
		public void overflow(int tag, Kind kind, Object message);
		/**
		 * 当有客户端发送心跳数据后调用该方法
		 * @param retry
		 */
		public void heart(int retry);

}

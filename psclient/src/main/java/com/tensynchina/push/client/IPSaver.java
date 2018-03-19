package com.tensynchina.push.client;

/**
 * 数据操作类 接口
 * @author susy
 *
 */
public interface IPSaver {

		
		/**
		 * 本地保存数据
		 * @param key
		 * @param data
		 */
		public abstract void save(String key, byte[] data);
		/**
		 * 本地获取数据
		 * @param key
		 * @return
		 */
		public abstract byte[] get(String key);
		

}

package com.llx278.utils;

import android.os.Environment;
import android.os.StatFs;

import java.io.File;

public final class SDCardHelper {

	public static boolean SDCardExist() {
		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
	}

	public static long getSDFreeSize() {
		if(!SDCardExist())
		{
			return 0L;
		}
		File path = Environment.getExternalStorageDirectory();
		StatFs sf = new StatFs(path.getPath());
		long blockSize = sf.getBlockSize();
		long freeBlocks = sf.getAvailableBlocks();
		return (freeBlocks * blockSize); // 单位MB
	}

	public static long getSDCardSize() {
		if(!SDCardExist())
		{
			return 0L;
		}
		File path = Environment.getExternalStorageDirectory();
		StatFs sf = new StatFs(path.getPath());
		long blockSize = sf.getBlockSize();
		long allBlocks = sf.getBlockCount();
		return (allBlocks * blockSize); // 单位MB
	}
	
	/**
	 *  检查存储卡是否只读
	 * @return
	 */
	public static boolean isReadOnlySDCard(){
		
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED_READ_ONLY);
	}

}

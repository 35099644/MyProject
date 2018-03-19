package com.llx278.utils;

import android.text.TextUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class EncryptUtils {



	/**
	 * AES加密
	 * @param data
	 * @param sKey
	 * @return
	 * @throws Exception
	 */
	public static byte[] encrypt(byte[] data, String sKey) throws Exception
	{
		if (TextUtils.isEmpty(sKey))
		{
			throw new IllegalArgumentException("Key is null");
		}
		if (sKey.length() != 16)
		{
			throw new IllegalArgumentException("Key length != 16");
		}
		byte[] raw = sKey.getBytes();
		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");// "算法/模式/补码方式"
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
		return cipher.doFinal(data);
	}
	
	/**
	 * AES解密
	 * @param src
	 * @param sKey
	 * @return
	 * @throws Exception
	 */
	public static byte[] decrypt(byte[] src, String sKey) throws Exception
	{
		if (sKey == null)
		{
			throw new IllegalArgumentException();
		}
		if (sKey.length() != 16)
		{
			throw new IllegalArgumentException("length is invaild");
		}
		byte[] raw = sKey.getBytes();
		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");// "算法/模式/补码方式"
		cipher.init(Cipher.DECRYPT_MODE, skeySpec);
		return cipher.doFinal(src);
	}
}

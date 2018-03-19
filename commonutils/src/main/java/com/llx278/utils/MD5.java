package com.llx278.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5
 *
 * 
 */
final public class MD5
{
	/**
	 * 计算一个Byte数组的MD5
	 * 
	 * @param content
	 * @return
	 */
	public static String getMD5_16(byte[] content)
	{
		try
		{
			if (content != null)
			{
				MessageDigest md = MessageDigest.getInstance("MD5");
				md.update(content);
				StringBuffer sb = new StringBuffer();
				byte[] data = md.digest();
				int i;
				for (int offset = 0; offset < data.length; offset++)
				{
					i = data[offset];
					if (i < 0)
					{
						i += 256;
					}
					if (i < 16)
					{
						sb.append("0");
					}
					// 将整型 十进制 i 转换为16位，用十六进制参数表示的无符号整数值的字符串表示形式。
					sb.append(Integer.toHexString(i));
				}
				// 返回16位MD5码
				return sb.toString().substring(8, 24);
			}
		} catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	
	public static byte[] getMD5_bin(byte[] content)
	{
		try
		{
			if (content != null)
			{
				MessageDigest md = MessageDigest.getInstance("MD5");
				md.update(content);
				return md.digest();
			}
		} catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 计算一个Byte数组的MD5
	 * 
	 * @param content
	 * @return
	 */
	public static String getMD5(byte[] content)
	{
		try
		{
			if (content != null)
			{
				MessageDigest md = MessageDigest.getInstance("MD5");
				md.update(content);
				StringBuffer sb = new StringBuffer();
				byte[] data = md.digest();
				int i;
				for (int offset = 0; offset < data.length; offset++)
				{
					i = data[offset];
					if (i < 0)
					{
						i += 256;
					}
					if (i < 16)
					{
						sb.append("0");
					}
					// 将整型 十进制 i 转换为16位，用十六进制参数表示的无符号整数值的字符串表示形式。
					sb.append(Integer.toHexString(i));
				}
				// 返回16位MD5码
				return sb.toString();
			}
		} catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	
	public static boolean compare(byte[] md51, byte[] md52)
	{
		if (null == md51 || null == md52)
			return false;
		boolean equal = true;
		int len1 = md51.length;
		int len2 = md51.length;
		if (len1 == len2)
		{
			for (int i = 0; i < len1; i++)
			{
				if (md51[i] != md52[i])
				{
					equal = false;
					break;
				}
			}
		} else
		{
			equal = false;
		}

		return equal;
	}
}

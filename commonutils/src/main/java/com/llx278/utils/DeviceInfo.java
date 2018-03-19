package com.llx278.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public final class DeviceInfo
{
	private static String systemVersion;
	private static int sdkVersion;
	private static boolean isSystemApp;//该客户端是否是系统APP
	private static int density;
	private static String imei;
	private static String imsi;
	private static String userAgent;
	private static int screenWidth ;
	private static int screenHeight ;
	private static String carrier;
	private static String model;
	private static String brand;
	private static int version;
	private static String cmac;  // 生成有线网卡地址   对于手机端来说，此参数无效
	private static String wmac;  //  生成无线网卡地址
	
	
	
	
	public static void init( Context context )
	{
		systemVersion = Build.VERSION.INCREMENTAL;
		sdkVersion = Build.VERSION.SDK_INT;
		isSystemApp = isSystemApp();
		
		try {
			version = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		density = context.getResources().getDisplayMetrics().densityDpi;
		screenWidth = context.getResources().getDisplayMetrics().widthPixels;
		screenHeight = context.getResources().getDisplayMetrics().heightPixels;
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		model = Build.MODEL;
		brand = Build.BRAND;
		if(tm != null){
			imei = tm.getDeviceId();
			if(TextUtils.isEmpty(imei)){
				imei = "";
			}
			imsi = tm.getSubscriberId();
		}
		get_phone_ua(context);
		
		carrier = getCarriar(tm);
		cmac = cGenerateCmac();
		wmac = cGenerateWmac(context);
	}

	public static boolean isSystemApp(Context ctx) {

		if ((ctx.getApplicationInfo().flags & ApplicationInfo.FLAG_SYSTEM) > 0) {
			return true;
		}
		return false;
	}
	
	public static boolean isLaterThanHoneycombMR2()
	{
		return sdkVersion >= 13;
	}

	public static boolean isLaterThanHoneycomb()
	{
		return sdkVersion >= 11;
	}
	
	/**
	 * UserAgent:厂商|型号|SDK的VersionName|代号|SDK版本
	 */
	public static String get_phone_ua(Context c) {
//		if (userAgent == null) {
//			StringBuffer sb = new StringBuffer();
//			sb.append(Build.MANUFACTURER);//制造商
//			sb.append("/");
//			sb.append(Build.MODEL);//手机型号
//			sb.append("/");
//			sb.append(Build.VERSION.RELEASE);
//			sb.append("/");
//			sb.append(Build.DISPLAY);
//			sb.append("/");
//			sb.append(Build.VERSION.SDK_INT);
//			userAgent = sb.toString();
//		}
//		return userAgent;
		return "";
	}

	
	
	private static String getCarriar(TelephonyManager tm)
	{
		if(tm == null)
		{
			return "";
		}
		if(tm.getSimState() != TelephonyManager.SIM_STATE_READY)
		{
			return "";
		}
		String on = tm.getSimOperatorName();
		int mnc = get_mnc(on);
		String ret = "unkown";
		if(0 == mnc || 2 == mnc || 7 == mnc)
		{
			ret = "cmcc";
		}
		else if(1 == mnc)
		{
			ret = "unicom";
		}
		else if(3 == mnc)
		{
			ret = "telecom";
		}
		return ret;
	}
	
	private static int get_mnc(String numeric){
		if(TextUtils.isEmpty(numeric)){
			return -1;
		}
		if (numeric.length() < 5) {
			return -1;
		}
		int ret = -1;
		try {
			ret = Integer.parseInt(numeric.substring(numeric.length()
					- (numeric.length() > 5 ? 3 : 2)));
		} catch (Exception e) {
		}
		return ret;
	}

	private static String cGenerateCmac() {
		String mac = generateCmac();
		if(mac == null){
			return "";
		}
		String nMac = mac.replace(':', '-');
		if("00-00-00-00-00-00".equals(mac)){
			return "";
		}
		return nMac;
	}

	/** 获得有线网卡的地址
	 * @return
	 */
	private static String generateCmac() {
		String strMacAddr = null;
		try {
			InetAddress ip = getLocalInetAddress();

			byte[] b = NetworkInterface.getByInetAddress(ip).getHardwareAddress();
			StringBuilder buffer = new StringBuilder();
			for (int i = 0; i < b.length; i++) {
				if (i != 0) {
					buffer.append('-');
				}

				String str = Integer.toHexString(b[i] & 0xFF);
				buffer.append(str.length() == 1 ? 0 + str : str);
			}
			strMacAddr = buffer.toString().toUpperCase();
		} catch (Exception e) {
			e.printStackTrace();
			return strMacAddr;
		}
		return strMacAddr;
	}

	protected static InetAddress getLocalInetAddress() {
		InetAddress ip = null;
		try {
			Enumeration<NetworkInterface> en_netInterface = NetworkInterface.getNetworkInterfaces();
			while (en_netInterface.hasMoreElements()) {
				NetworkInterface ni = (NetworkInterface) en_netInterface.nextElement();
				Enumeration<InetAddress> en_ip = ni.getInetAddresses();
				while (en_ip.hasMoreElements()) {
					ip = en_ip.nextElement();
					if (!ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":") == -1)
						break;
					else
						ip = null;
				}

				if (ip != null) {
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ip;
		}
		return ip;
	}

	private static String cGenerateWmac(Context context) {
		String mac = generateWmac(context);
		if(mac == null){
			return "";
		}
		String nMac = mac.replace(':', '-');
		// 00-50-56-C1-01-8D
		if("00-00-00-00-00-00".equals(mac)){
			return "";
		}
		return nMac;
	}

	/**
	 *  生成无线网卡的地址
	 * @return
	 */
	private static String generateWmac(Context ctx) {
		WifiManager wifi = (WifiManager) ctx.getApplicationContext().getSystemService (Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		return info.getMacAddress();
	}

	public static String getSystemVersion() {
		return systemVersion;
	}

	public static int getSdkVersion() {
		return sdkVersion;
	}

	public static boolean isSystemApp() {
		return isSystemApp;
	}

	public static int getDensity() {
		return density;
	}

	public static String getImei() {
		return imei;
	}

	public static String getImsi() {
		return imsi;
	}

	public static String getUserAgent() {
		return userAgent;
	}

	public static int getScreenWidth() {
		return screenWidth;
	}

	public static int getScreenHeight() {
		return screenHeight;
	}

	public static String getCarrier() {
		return carrier;
	}
 
	
	public static String getModel()
	{
		return model;
	}
	
	public static String getBrand()
	{
		return brand;
	}

	public static int getVersion() {
		return version;
	}

	/**
	 *  获得无线网卡地址
	 * @return
	 */
	public static String getCmac(){
		return cmac;
	}

	/**
	 * 获得有线网卡地址
	 * @return
	 */
	public static String getWmac(){
		return wmac;
	}
}

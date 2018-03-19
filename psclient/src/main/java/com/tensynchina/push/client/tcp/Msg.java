package com.tensynchina.push.client.tcp;

import java.io.ByteArrayOutputStream;
/**
 * 消息类-抽象类
 * @author susy
 *
 */
public abstract class Msg {
	protected byte[] indatas;
	protected byte[] outdatas;
	protected boolean reply;
	
	protected static final byte cast = (byte) 0xAA;
	protected static final byte heart=(byte)0xFF;

	public void clearin(){
		this.indatas = null;
		this.outdatas = null;
	}
	protected byte[] char2byte(int start,char[] src,byte[] datas){
		for(int i=0;i<src.length;i++){
			datas[start+i]=(byte)(src[i]&0xff);
		}
		return datas;
	}
	protected byte[] int2byte(int val,int start,int n,byte[] datas){
		for(int i=0;i<n;i++){
			if(i>0){
				datas[start+i]=(byte)((val>>(i*8))&0xff);
			}else{
				datas[start+i]=(byte)(val&0xff);
			}
		}
		return datas;
	}
	protected byte[] long2byte(long val,int start,int n,byte[] datas){
		for(int i=0;i<n;i++){
			if(i>0){
				datas[start+i]=(byte)((val>>(i*8))&0xff);
			}else{
				datas[start+i]=(byte)(val&0xff);
			}
		}
		return datas;
	}
	protected byte[] bytes2byte(byte[] val,int start,int n,byte[] datas){
		for(int i=0;i<n;i++){
			datas[start+i]=val[i];
		}
		return datas;
	}
	protected int bytesSum(byte[] datas){
		int sum=0;
		for(int i=0;i<datas.length-1;i++){
			sum+=(datas[i]&0xff);
		}
		return sum;
	}
	protected int getByte(int start){
		if(start>=0&&start<this.indatas.length){
			return this.indatas[start];
		}
		return 0;
	}
	protected int getBytes(int start,int len){
		if(start>=0&&start+len<=this.indatas.length){
			int rs=0;
			for(int i=start;i<start+len;i++){
				if(i-start>0){
					rs|=(this.indatas[i]&0xff)<<((i-start)*8);
				}else{
					rs|=this.indatas[i]&0xff;
				}
			}
			return rs;
		}
		return -1;
	}
	protected long getLongBytes(int start,int len){
		if(start>=0&&start+len<=this.indatas.length){
			long rs=0;
			for(int i=start;i<start+len;i++){
				if(i-start>0){
					rs|=(this.indatas[i]&0xff)<<((i-start)*8);
				}else{
					rs|=this.indatas[i]&0xff;
				}
			}
			return rs;
		}
		return -1;
	}
	protected String getString(int start,int length){
		int len = length;
		if(length<0){
			len = this.indatas.length-start+length;
		}
		if(start>=0&&start+len<=this.indatas.length){
			try {
				return new String(this.indatas,start,len,"utf-8");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	protected String getChars(int start,int length){
		int len = length;
		if(length<0){
			len = this.indatas.length-start+length;
		}
		String info = "";
		if(start>=0&&start+len<=this.indatas.length){
			for(int i=start;i<start+len;i++){
				info+=(char)this.indatas[i];
			}
		}
		return info;
	}
	public abstract byte[] toBytes();
	public static String bytesToHex(byte[] data){
		if(data!=null&&data.length>0){
//			String info="";
			StringBuffer info = new StringBuffer();
			for(byte b : data){
				info.append(Integer.toHexString(b&0xff).toUpperCase()+" ");//"0x"+
//				info+="0x"+Integer.toHexString(b&0xff).toUpperCase()+" ";
			}
//			return info;
			return info.toString();
		}
		return null;
	}
	public String toHexString(){
		if(indatas==null)return "";
		return bytesToHex(indatas);
	}
	public void parseFromSource(byte[] source){
		if(source!=null&&source.length>0){
			this.clearin();
			ByteArrayOutputStream out = null;
			try {
				int pos=0;
				int len = source.length;
				for(int i=0;i<len;i++){
					byte b = source[i];
					if(b==heart){
						//skip
						if(out==null)out=new ByteArrayOutputStream();
						if(i>pos)out.write(source,pos,i-pos);
					} else if(b==cast&&i+1<len){
						
						if(out==null)out=new ByteArrayOutputStream();
						byte _b = (byte)(source[i+1]&0x0F);
						byte nv = (byte)((_b<<4|_b)&0xFF);
						out.write(source,pos,i-pos);
						out.write(nv);
						pos=i+2;
						
					}
				}
				if(pos<source.length&&out!=null){
					out.write(source,pos,source.length-pos);
				}
				if(pos==0){
					indatas = source;
				}else if(out!=null){
					indatas = out.toByteArray();
				}
			} catch (Exception e) {
			} finally {
				if(out!=null){
					try{
						out.close();
						out=null;
					}catch(Exception e){}
				}
			}
			
		}
	}
	public boolean isReply() {
		return reply;
	}
	public void setReply(boolean reply) {
		this.reply = reply;
	}

	/**
	 * 把十六进制拼写的字符串转成byte数组
	 * @param info
	 * @return
	 */
	public static byte[] hexStringToBytes(String info){
		String[] infos = info.split(" ");
		byte[] bytes = null;
		if(infos!=null&&infos.length>0)
		{
			bytes = new byte[infos.length];
			for(int i=0;i<bytes.length;i++){
				String str = infos[i];//.substring(2);
				int len = str.length();
				char firstChar,secondChar;
				if(len==2){
					firstChar = str.charAt(0);
					secondChar =str.charAt(1);
				}
				else{
					firstChar = '0';
					secondChar = str.charAt(0);
				}
				bytes[i] = (byte) (toByte(firstChar) << 4 | toByte(secondChar));
//				String str = infos[i].substring(2);
//				int len = str.length();
//				int sum = 0;
//				for(int j=0;j<len;j++){
//					sum+=Integer.valueOf(str.charAt(j)+"",16)*Math.pow(16,len-j-1);
//				}
//				bytes[i] = (byte) sum;
			}
		}
		return bytes;
	}
	private static int toByte(char c) {
		byte b = (byte) "0123456789ABCDEF".indexOf(c);
		return b;
	}
	public static void main(String[] args) {
		Integer iint = new Integer(-12931203);
		System.out.println(iint.hashCode());
		String k = "-12931203";
		System.out.println(k.hashCode());
		byte[] data = new byte[]{(byte)0xbb,0x21, 0x1 ,0x0, 0x0, 0x0, 0x65 ,0x0 ,0x0 ,0x0 ,(byte)0x87,(byte)0x83,0xC ,0x0 ,(byte)0x80, 0x1, 0x1 ,0x1F,(byte)0xee};
		data = new byte[]{
				0x00,0x30,(byte)0xFF,(byte)0xDA,0x05,0x43,0x4B,0x32,0x33,0x22,0x73,0x20,0x43,0x38,0x36,0x39,0x39,0x32,0x34,0x30,0x30,0x38,0x33,0x30,0x38,0x30,0x34,0x32,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x38,0x39,0x38,0x36,0x30,0x30,0x4d,0x46,0x53,0x53,0x59,0x59,0x47,0x58,0x58,0x58,0x58,0x58,0x58,0x50,0x38,0x39,0x36,0x30,0x30,0x37,0x38,0x39,0x34,0x35,0x36,0x31,0x32,0x33,0x05,0x00,0x01,0x00};
		//data=new byte[]{(byte)0x0b};
		int sum = 0;
		for(byte b : data){
			sum+=(b&0xff);
		}
//		byte _b = (byte)(data[0]&0x0F);
//		byte nv = (byte)((_b<<4|_b)&0xFF);
//		System.out.println(NettyUtils.bytesToHex(new byte[]{nv}));
		System.out.println("sum:"+sum);
	}
}

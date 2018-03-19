package com.tensynchina.push.sdk.msg;


public class Vehicle {
	int startvid;
	public void setStartvid(int startvid) {
		this.startvid = startvid;
	}
	public void setEndvid(int endvid) {
		this.endvid = endvid;
	}
	int endvid;
	int in(int vid){
		if(vid-1==endvid)return 1;
		if(vid>endvid)return 2;
		if(vid+1==startvid)return -1;
		if(vid<startvid)return -2;
		return 0;
	}
	public Vehicle(int startvid, int endvid) {
		if(startvid>endvid){
			this.startvid = endvid;
			this.endvid = startvid;
		}else{
			this.startvid = startvid;
			this.endvid = endvid;
		}
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "[start:"+startvid+",end:"+endvid+"]";
	}
	public static void main(String[] args) {
		byte[] bs = new byte[]{0x74,0x6B,0x3D,0x74,0x6B,0x30,0x30,0x30,0x30,0x35,0x26,0x68,0x63,0x3D,0x39,0x39,0x39,0x39,0x26,0x75,0x69,0x64,0x3D,0x39,0x39,0x39,0x39};
		String s = new String(bs);
		System.out.println(s);
	}
}

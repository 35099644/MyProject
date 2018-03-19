package com.tensynchina.push.sdk.msg;


import java.util.HashMap;

public class MyMap extends HashMap<Integer,Vehicle> {
	private static final long serialVersionUID = 1L;

	public Vehicle add(Vehicle value) {
		return super.put(this.size(), value);
	}
	public synchronized void insert(int index, Vehicle value) {
		int l = this.size();
		for(int i=l;i>index;i--){
			this.put(i, this.get(i-1));
		}
		this.put(index, value);
	}
	public synchronized Vehicle del(int index) {
		int l = this.size();
		Vehicle v = this.get(index);
		if(v!=null){
			for(int i=index;i<l-1;i++){
				this.put(i, this.get(i+1));
			}
			this.remove(l-1);
		}
		return v;
	}
	@Override
	public String toString() {
		return super.toString();
	}
	
}

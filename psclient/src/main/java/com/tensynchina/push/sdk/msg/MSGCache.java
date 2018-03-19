package com.tensynchina.push.sdk.msg;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MSGCache {
	private Map<Integer,List<String>> serverMapPServer=new HashMap<Integer,List<String>>();
	private Map<String,MyMap> pServerMapTerminal=new HashMap<String,MyMap>();
	public void cache(int sid,String ip) {
		if(!serverMapPServer.containsKey(sid)){
			serverMapPServer.put(sid, new ArrayList<String>());
		}
		List<String> iplist = serverMapPServer.get(sid);
		if(!iplist.contains(ip))iplist.add(ip);
	}
	public void cache(String ip,int vid) {
		if(!pServerMapTerminal.containsKey(ip)){
			pServerMapTerminal.put(ip, new MyMap());
		}
		//System.out.println("cache:ip:"+ip+",vid:"+vid);
		MyMap vehicles = pServerMapTerminal.get(ip);
		int len = vehicles.size();
		if(len==0){
			vehicles.put(len,new Vehicle(vid, vid));
		}else{
			Vehicle tmp=null;
			int delidx=-1;
			int addidx=-1;
			int mid=len/2;
			while(mid>=0||mid<len){
				Vehicle v = vehicles.get(mid);
				int rs=v.in(vid);
				if(rs==0){
					break;
				}else if(rs==1){
					tmp = vehicles.get(mid+1);
					if(tmp!=null&&tmp.in(vid)==-1){
						v.setEndvid(tmp.endvid);
						delidx=mid+1;
						break;
					}else{
						v.setEndvid(vid);
						break;
					}
				}else if(rs==-1){
					tmp = vehicles.get(mid-1);
					if(tmp!=null&&tmp.in(vid)==1){
						tmp.setEndvid(v.endvid);
						delidx=mid;
						break;
					}else{
						v.setStartvid(vid);
						break;
					}
				}else if(rs==2){
					tmp = vehicles.get(mid+1);
					if(tmp==null||tmp.in(vid)==-2){
						addidx=mid+1;
						break;
					}else{
						mid++;
					}
				}else if(rs==-2){
					tmp = vehicles.get(mid-1);
					if(tmp==null||tmp.in(vid)==2){
						addidx=mid;
						break;
					}else{
						mid--;
					}
				}
			}
			if(delidx>-1&&delidx<len){
				vehicles.del(delidx);
			}
			if(addidx>-1&&addidx<=len){
				vehicles.insert(addidx, new Vehicle(vid, vid));
			}
		}

	}
	public List<String> checkPServer(int sid,int vid){
		List<String> iplist=null;
		System.out.println("checkPServer sid:"+sid+",vid:"+vid);
		if(serverMapPServer.containsKey(sid)){
			List<String> _iplist=serverMapPServer.get(sid);
			System.out.println("checkPServer _iplist:"+_iplist);
			if(_iplist!=null){
				int l = _iplist.size();
				for(int i=0;i<l;i++){
					String ip = _iplist.get(i);
					MyMap vehicles = pServerMapTerminal.get(ip);
					if(vehicles!=null){
						int len = vehicles.size();
						for(int j=0;j<len;j++){
							Vehicle v = vehicles.get(j);
							System.out.println("checkPServer v:"+v);
							if(v!=null&&v.in(vid)==0){
								if(iplist==null)iplist = new ArrayList<String>();
								if(!iplist.contains(ip))iplist.add(ip);
								break;
							}
						}
					}
				}
			}
		}
		return iplist;
	}
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		if(serverMapPServer!=null){
			for(Integer sid:serverMapPServer.keySet()){
				List<String> iplist = serverMapPServer.get(sid);
				sb.append("["+sid+":"+iplist+"]");
			}
		}
		if(pServerMapTerminal!=null){
			for(String ip:pServerMapTerminal.keySet()){
				sb.append("["+ip+":"+pServerMapTerminal.get(ip)+"]");
			}
		}
		return sb.toString();
	}
	
}

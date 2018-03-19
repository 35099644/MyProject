package com.tensynchina.push.client.tcp;

import com.tensynchina.push.client.IPRequest;
import com.tensynchina.push.client.IPResponse;
import com.tensynchina.push.client.Kind;
/**
 * TCP ~消息对象
 * @author susy
 *
 * @param <RT>
 * @param <RET>
 */
public class CMsg<RT extends IPRequest,RET extends IPResponse> extends Msg {
	private TCPClient<RT,RET> tcpclient;
	
	public CMsg(TCPClient<RT,RET> tcpclient) {
		this.tcpclient = tcpclient;
	}
	@Override
	public byte[] toBytes() {
		return null;
	}
	public byte[] sender(int tag,int kind,Object val){
		byte[] datas = null;
		switch(kind){
		case 0://int
			datas = new byte[1+1+4+1];
			datas[0]=(byte)(tag&0xff);
			datas[1]=(byte)(kind&0xff);
			int2byte((Integer)val, 2, 4, datas);
			datas[datas.length-1]=(byte)(bytesSum(datas)&0xff);
			break;
		case 1:
			datas = new byte[1+1+8+1];
			datas[0]=(byte)(tag&0xff);
			datas[1]=(byte)(kind&0xff);
			long2byte((Long)val, 2, 8, datas);
			datas[datas.length-1]=(byte)(bytesSum(datas)&0xff);
			break;
		case 2:
			String sv = (String)val;
			datas = new byte[1+1+sv.getBytes().length+1];
			datas[0]=(byte)(tag&0xff);
			datas[1]=(byte)(kind&0xff);
			bytes2byte(sv.getBytes(), 2, sv.getBytes().length, datas);
			datas[datas.length-1]=(byte)(bytesSum(datas)&0xff);
			break;
		case 3:
			byte[] bdata = (byte[])val;
			datas = new byte[1+1+bdata.length+1];
			datas[0]=(byte)(tag&0xff);
			datas[1]=(byte)(kind&0xff);

			bytes2byte(bdata, 2, bdata.length, datas);
			datas[datas.length-1]=(byte)(bytesSum(datas)&0xff);
			break;
	}
//		BinaryServer server=new BinaryServer();
//		byte[][] datass = null;
//		try{
//			datass=server.packageInfo(datas);
//		}
//		catch (OutSizeException e){
//			System.out.println("发送数据过长！发送失败！");
//		}
//		byte[][] datass=server.packageInfo(datas);
//		for(byte[] sends:datass){
//			this.tosend(sends);
//		}
		this.tosend(datas);
		return datas;
	}
	private void tosend(byte[] datas){
		if(datas==null)return;
		try {
			tcpclient.writeflush(datas);
		} catch (Exception e) {
			e.printStackTrace();
			tcpclient.pclient.response().debug("tosend failed.",e);
		}
	}
	public Object[] unmarshal(){
		int tag = getByte(0);
		//tcpclient.pclient.response().debug(NettyUtils.bytesToHex(this.indatas));
		if(tag==0){
			return null;
		}
		int kind = getByte(1);
		int datapos = 2;
		//tcpclient.pclient.response().debug("CMsg unmarshal tag:"+tag+",kind:"+kind);
		Object[] rs = null;
		switch(kind){
		case 0://int
			int val = getBytes(datapos, 4);
			rs = new Object[]{tag,Kind.INT,val};
			break;
		case 1:
			long v = getLongBytes(datapos, 8);
			double dv = v/100000d;
			rs = new Object[]{tag,Kind.DOUBLE,dv};
			break;
		case 2:
			String sv = getString(datapos, -1);
			rs = new Object[]{tag,Kind.STRING,sv};
			break;
		case 3:
			int l = this.indatas.length-datapos-1;
			byte[] body = new byte[l];
			System.arraycopy(this.indatas,datapos,body,0,l);
			rs = new Object[]{tag,Kind.BYTES,body};
			break;
		default:
			String address = getChars(2, -1);
			int idx = address.indexOf(':');
			String host = address.substring(0,idx);
			int port = Integer.parseInt(address.substring(idx+1));
			tcpclient.pcm.getSaver().save("address", address.getBytes());
			tcpclient.changeAddress(host, port);
			break;
		}
		return rs;
	}
}

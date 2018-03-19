package com.tensynchina.push.client.tcp;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import com.tensynchina.push.client.IPRequest;
import com.tensynchina.push.client.IPResponse;
import com.tensynchina.push.client.impl.PClient;
import com.tensynchina.push.client.impl.PushClientManager;
/**
 * TCP客户端类-实现类
 * @author susy
 *
 * @param <RT>
 * @param <RET>
 */
final public class TCPClient<RT extends IPRequest,RET extends IPResponse> implements Runnable {
	private Socket socket;
	private InputStream inputStream;
	private OutputStream outputStream;
	private boolean running;
	private Thread reader;
	private Thread monitor;
	private boolean connecting;
	private boolean isconnected;
	PClient<RT,RET> pclient;
	PushClientManager pcm;
	/**
	 * 连接的地址
	 */
	private String host;
	/**
	 * 端口号
	 */
	private int port;
	
	private long lastcheck;
	private int checktimeout = 15000;
	private int connectRetry;
	private long connecttimestamp;
	
	protected static final byte cast = (byte) 0xAA;
	protected static final byte cast_cast = (byte) 0x0A;
	protected static final byte cast_s = (byte) 0x0B;
	protected static final byte cast_e = (byte) 0x0E;
	protected static final byte s = (byte) 0xBB;
	protected static final byte e = (byte) 0xEE;
	protected static final byte heart=(byte)0xFF;
	protected static final byte cast_heart=(byte)0x0F;
	
	private CMsg<RT,RET> msgbody;
	
	private boolean waitreply=false;
	
	public TCPClient(PClient<RT,RET> pclient,PushClientManager pcm) {
		this.pclient = pclient;
		this.pcm = pcm;
		this.host = pclient.request().host();
		
		this.port =pclient.request().port();
	}
	public void changeAddress(String host,int port){
		this.host = host;
		this.port = port;
		Log.d("PushService","changeAddress");
		connect();
	}
	private void connect(){
		if (connecting)
			return;
		connecting = true;
		waitreply=true;
		boolean tocloseold = false;
		if (socket != null) {
			Log.d("PushService","socekt isn't null ******************************* socket address is " + socket.toString());
		}
		Socket lastso = socket;
		connectRetry++;
		connecttimestamp = System.currentTimeMillis();
		try {
			socket = new Socket(this.host,this.port);
			Log.d("PushService","new Socket address ============= socket address is : " + socket.toString());
			socket.setTcpNoDelay(true);
			socket.setKeepAlive(true);
			socket.setReuseAddress(true);
			socket.setSoTimeout(30000);
			inputStream = socket.getInputStream();
			outputStream = socket.getOutputStream();
			isconnected = true;
//			if (listener != null)
//				listener.connected(this);
			tocloseold = true;
			connectRetry=0;
		} catch (Exception e) {
			pclient.response().debug("connect failed.[" + host + ":" + port + "]", e);
		}
		if (tocloseold) {
			pclient.response().debug("reconnect success![" + host + ":" + port  + "]");
			if (lastso != null)
				close(lastso);
		}
		connecting = false;
		this.authorize();
	}
	private void authorize(){
		this.pclient.request().authorize();
	}
	private void close(Socket sock) {
		if (sock == null)
			return;
		try {
			sock.shutdownInput();
		} catch (Exception e) {
			pclient.response().debug("shutdownInput failed.",e);
		}
		try {
			sock.shutdownOutput();
		} catch (Exception e) {
			pclient.response().debug("shutdownOutput failed.",e);
		}
		try {
			sock.close();
		} catch (IOException e) {
			pclient.response().debug("close socket failed.",e);
		}

	}
	boolean checkRemote() {
		//Log.d("PushService","checkRemote");
		long ct = System.currentTimeMillis();
		try {
			if (ct - lastcheck > checktimeout - 1) {
				lastcheck = ct;
				if (this.running &&this.isconnected&& this.outputStream != null) {
					//socket.sendUrgentData(0xff);
					//socket.getOutputStream().write(0xff);
					//socket.getOutputStream().flush();
					writeflush(null, true);
					pclient.response().heart(connectRetry);
					return true;
				}
			}
		} catch (IOException e) {
			pclient.response().debug("socket test failed.socket:[" + socket + "]", e);
			isconnected = false;
		}
		if (this.running && !this.isconnected) {
			if(connectRetry>0){
				int time = connectRetry>6?6:connectRetry;
				if(ct-connecttimestamp>checktimeout*time){
					Log.d("PushService","ct-connecttimestamp>checktimeout*time");
					this.connect();
				}
			}else{
				Log.d("PushService","else ....");
				this.connect();
			}
			pclient.response().heart(connectRetry);
		}
		return false;
	}
	public void tostart() {
		this.msgbody = new CMsg<RT,RET>(this);
		pclient.ready();
		startRunner();
	}
	public void willreconnect(){
		this.isconnected=false;
	}
	private void startRunner() {
		if (!this.running) {
			this.running = true;
			monitor = new Thread(this);
			monitor.setDaemon(true);
			monitor.start();
			reader = new Thread(new SoReader());
			reader.setDaemon(true);
			reader.start();
		}
	}

	public void shutdown() {
		if (this.running) {
			this.running = false;
		}
	}
	private void tosleep(long t) {
		try {
			Thread.sleep(t);
		} catch (Exception e) {
		}
	}
	private void release() {
		if (socket != null)
			close(socket);
	}
	public InputStream getstream() {
		if(socket==null){
			return null;
		}
		try {
			inputStream = socket.getInputStream();
		} catch (Exception e) {
			pclient.response().debug("getinputstream failed.", e);
		}
		return inputStream;
	}
	@Override
	public void run() {
		Log.d("PushService","Monitor from run connect ------------");
		this.connect();
		while (this.running) {
			//Log.d("PushService","monitoring .. ");
			tosleep(checktimeout);
			if(waitreply){
				this.isconnected=false;
			}
			if (running && checkRemote()) {
				
			}

		}
		pclient.response().debug("exit monitoring ..");
		release();
	}
	void writeflush(byte[] msg) throws IOException {
		writeflush(msg,false);
	}
	synchronized void writeflush(byte[] msg,boolean isheart) throws IOException {
		OutputStream out = outputStream;
		if(out==null){
			throw new IOException("OutputStream is null,Socket is unavailable!");
		}
		if (msg != null && msg.length > 0) {
			out.write(s);
			int pos = 0;
			for (int i = 0; i < msg.length; i++) {
				byte b = msg[i];
				// System.out.println("WriteFlush.b:"+b);
				if (b == s) {
					out.write(msg, pos, i - pos);
					out.write(cast);
					out.write(cast_s);
					pos = i + 1;
				} else if (b == e) {
					out.write(msg, pos, i - pos);
					out.write(cast);
					out.write(cast_e);
					pos = i + 1;
				} else if (b == cast) {
					out.write(msg, pos, i - pos);
					out.write(cast);
					out.write(cast_cast);
					pos = i + 1;
				} else if (b == heart) {
					out.write(msg, pos, i - pos);
					out.write(cast);
					out.write(cast_heart);
					pos = i + 1;
				}
			}
			if (pos < msg.length) {
				pclient.response().debug("pos:"+pos+",msg.length is "+msg.length+",(msg.length - pos) is "+(msg.length - pos)+"***");
				out.write(msg, pos, msg.length - pos);
			}
			out.write(e);
			out.flush();
		}
		if(isheart){
			out.write(0xff);
			out.flush();
//			socket.sendUrgentData(0xff);
			//pclient.response().debug("heart");
		}
		lastcheck = System.currentTimeMillis();
	}
	class SoReader implements Runnable {
		private int checkAvailable(InputStream inputStream) {
			if (inputStream == null)
				return 0;
			try {
				int n = inputStream.available();
				return n;
			} catch (Exception e) {
				pclient.response().debug("check socket inputstream Available failed.",e);
			}
			return 0;
		}
		private int findStart(byte[] data, int pos, byte b) {
			byte needle = b;
			for (int i = pos; i < data.length; i++) {
				if (data[i] == needle) {
					// return i - haystack.readerIndex();
					return i;
				}
			}
			return -1;
		}
		private void tosleep(long t) {
			try {
				Thread.sleep(t);
			} catch (Exception e) {
			}
		}
		private Object[] decoder(int start, byte[] datas) {
			Object[] result = null;
			// logger.debug("start:"+start+",datas len:"+datas.length);
			if (start >= datas.length)
				return null;
			int idx = findStart(datas, start, s);
			//if(idx>start){pclient.response().debug("decoder find start idx>0.datas:"+NettyUtils.bytesToHex(datas));}
			if (idx >= start) {
				int eidx = findStart(datas, idx, e);
				if (eidx - idx - 1 > 0) {
					result = new Object[2];
					result[0] = eidx;
					byte[] ds = new byte[eidx - idx - 1];
					System.arraycopy(datas, idx + 1, ds, 0, ds.length);
					result[1] = ds;
				}
			}
			return result;
		}

		@Override
		public void run() {
			byte[] cache = new byte[4096];
			int delay = 10;
			int longdelay=3000;
			byte[] data = new byte[0];
			boolean dl=true;
			while (running) {
				//pclient.response().debug("soReaderReading isrunning");
				int pos = 0;
				InputStream inputStream = getstream();
				while (running && pos >= 0 && checkAvailable(inputStream) > 0) {
					try {
						pos = inputStream.read(cache);
						//pclient.response().debug("get data from inputStream : " + String.valueOf(cache));
						//Log.d("PushService","get data from inputStream : " + String.valueOf(cache));
					} catch (Exception e) {
						pclient.response().debug("Socket Read Data failed.", e);
					}
					if (pos > 0) {
						byte[] _data = new byte[data.length + pos];
						System.arraycopy(data, 0, _data, 0, data.length);
						System.arraycopy(cache, 0, _data, data.length, pos);
						data = _data;
					}
					if (pos < cache.length) {
						dl=true;
						break;
					}
					if(data.length>=40960){
						dl=false;
						//System.out.println("====================================接收数据超出buffer 40960,dl:"+dl);
						break;
					}
				}
				if (data != null && data.length > 0) {
					waitreply=false;
					lastcheck = System.currentTimeMillis();//bug
//					pclient.response().debug("Socket Read Data len:"+data.length);
					//pclient.response().debug(NettyUtils.bytesToHex(data));
					try {writeflush(null, true);} catch (Exception e) {}
					Object[] rs = null;
					int start = 0;
					while ((rs = decoder(start, data)) != null) {
						byte[] ds = (byte[]) rs[1];
						if (ds != null) {
							try{
								msgbody.parseFromSource(ds);
								Object[] datas = msgbody.unmarshal();
								if(datas!=null){
									pclient.push(datas);
								}
							} catch (Exception e) {pclient.response().debug("deal bytes failed.", e);}
						}
						start = ((Integer) rs[0]) + 1;
					}
					if(start>0&&start<=data.length){
						if(start==data.length){
							data = new byte[0];
						}else{
							byte[] _data = new byte[data.length-start];
							System.arraycopy(data, start, _data, 0, _data.length);
							data = _data;
						}
					}
					delay=1;
				}else{
					delay+=1;
				}
				if(delay>longdelay)delay = longdelay;
				if(dl)tosleep(delay);
			}
			//pclient.response().debug("soReaderReading has shutdown");
		}

	}
	public CMsg<RT,RET> getMsgbody() {
		return msgbody;
	}
}

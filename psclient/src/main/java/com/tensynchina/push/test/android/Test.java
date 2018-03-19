package com.tensynchina.push.test.android;

import com.tensynchina.push.client.Kind;
import com.tensynchina.push.client.ResponseDelegate;
import com.tensynchina.push.client.impl.AbstractPResponse;
import com.tensynchina.push.client.impl.PClient;
import com.tensynchina.push.client.impl.PRequest;
import com.tensynchina.push.sdk.android.PSReceiver;
import com.tensynchina.push.sdk.android.PSRequest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by susy on 13/04/2017.
 */
public class Test {
    static String fromuid="3002";
    static long tm=System.currentTimeMillis();
    static int cnt=0;
    static Object lock=new Object();
    static boolean locked=false;
    static String home="";


    private static String getHome(){
        try{
            File jarfile = new File(Test.class.getProtectionDomain().getCodeSource().getLocation().getPath());
            if(jarfile!=null&&jarfile.exists()){
                String path = jarfile.getParentFile().getPath();
                if(path.endsWith("lib"))path = jarfile.getParentFile().getParentFile().getPath();
                if(path.indexOf("PSClient")>0){
                    path = path.substring(0,path.indexOf("PSClient")+8);
                }
                return path;
            }
        }catch(Exception e){e.printStackTrace();}
        return "";
    }
    static void tonotify(){
        if(locked) {
            try {
                synchronized (lock) {
                    lock.notify();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    static void towait(){
        if(!locked) {
            locked=true;
            try {
                synchronized (lock) {
                    lock.wait();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            locked=false;
        }
    }
    public static void main(String[] args){
        PSReceiver rec = null;
        List<PSReceiver> recList=new ArrayList<PSReceiver>();
        byte[] cs = null;//new byte[256];
        boolean run = true;
        final Map<String,Integer> pageSendTag = new HashMap<>();
        final Map<String,FileOutputStream> fosmap = new HashMap<>();
        ResponseDelegate delegate = new ResponseDelegate() {

            @Override
            public void auhtorizeErr() {
            }
            @Override
            public void serverready(PClient<? extends PRequest, ? extends AbstractPResponse> pclient) {
            }
            @Override
            public void receiveString(PClient<? extends PRequest, ? extends AbstractPResponse> pclient, String[] params, String message) {
                String msg = message;
                String posstr = params[params.length-1];
                if(posstr!=null){
                    int pos = pclient.parseInt(posstr,-1);
                    if(pos>=0){
                        if(pos>=message.length()){
                            msg = "";
                        }else {
                            msg = message.substring(pos);
                        }
                    }
                }

                String[] vals = msg.split(":");
                String tag = vals[0];
                String from = params[0];
                System.out.println("delegate string msg:"+message);
                if("apply".equals(tag)){
                    int page = pclient.parseInt(vals[1],0);
                    String sn = vals[2];
                    if(pageSendTag.containsKey(sn)){
                        if(pageSendTag.get(sn)==page){
                            tonotify();
                            if(pageSendTag.containsKey("lastpage")) {
                                int lastpage = pageSendTag.get("lastpage");
                                if (lastpage == page) {
                                    System.out.println("Send bytes Success.");
                                    pageSendTag.remove("lastpage");
                                    pageSendTag.remove(sn);
                                }
                            }
                        }
                    }
                }else if("msg".equals(tag)){

                    fromuid = from;
                    String sn = vals[1];
                    String txt = "";
                    if(vals.length>2)txt = vals[2];
                    System.out.println("来自"+from+"["+sn+"]:"+txt+",cnt:"+(++cnt)+",exaust:"+(System.currentTimeMillis()-tm)+"ms");
                }
            }
            @Override
            public void receiveByte(PClient<? extends PRequest, ? extends AbstractPResponse> pclient, String[] head, byte[] message) {
                String fuid = head[0];
                String type = head[1];
                String pagestr = head[2];
                String sn = head[3];
                int page = pclient.parseInt(pagestr,0);
                System.out.println("fuid:"+fuid+",type:"+type+",pagestr:"+pagestr+",page:"+page+",sn:"+sn);
                String posstr = head[head.length-1];
                byte[] datas=null;
                if(posstr!=null){
                    int pos = pclient.parseInt(posstr,-1);
                    if(pos>=0&&pos<message.length){
                        datas = new byte[message.length-pos];
                        System.arraycopy(message,pos,datas,0,datas.length);
                    }
                }
                if(page>=0){
                    int sid = 1;
                    int vid = 21503;
                    String route="2";
                    FileOutputStream fos = null;
                    try {
                        if(datas!=null){
                            if(!fosmap.containsKey(sn)){
                                String filepath = home + "/"+sn+"."+type;
                                File f = new File(filepath);
                                if(!f.exists()){
                                    f.createNewFile();
                                }
                                fos = new FileOutputStream(f);
                                fosmap.put(sn, fos);

                            }
                            fos = fosmap.get(sn);

                            fos.write(datas);
                            fos.flush();
                        }else{
                            fos = fosmap.remove(sn);
                            if(fos!=null){
                                System.out.println("sn:"+sn+",fos closed.");
                                fos.close();
                            }

                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        if(fos!=null){
                            try{fos.close();}catch (Exception e1){}
                        }
                    }
                    ((PSRequest)pclient.request()).send(""+sid, ""+vid, route,fuid,"dhbyuid", "apply:" + page + ":" + sn);
                }
            }
            @Override
            public void receiveother(PClient<? extends PRequest, ? extends AbstractPResponse> pclient, int tag, Kind kind, Object message) {
            }

            @Override
            public void ready(PClient<? extends PRequest, ? extends AbstractPResponse> pclient) {

            }
        };
        byte[] tmp = new byte[128];
        home = getHome();
        while (run) {
            String info = null;
            try {
                InputStream is = System.in;
                //int l = 0;
                cs = new byte[0];

                int len=is.read(tmp);
                //l+=len;
                while(len>0){
                    byte[] _cs= new byte[cs.length+len];
                    if(cs.length>0)System.arraycopy(cs,0,_cs,0,cs.length);
                    System.out.println("read len:"+len);
                    System.arraycopy(tmp,0,_cs,cs.length,len);
                    cs = _cs;
                    if(len>=tmp.length) {
                        len = is.read(tmp);
                        //l+=len;
                    }else{
                        break;
                    }

                }
                int le = cs.length;
                System.out.println("输入字符总长度:"+le);
                for (int i = 0; i < cs.length; i++) {
                    if (cs[i] == '\n' || cs[i] == '\r') {
                        le = i;
                        break;
                    }
                }
                byte[] cmds = new byte[le];
                System.arraycopy(cs, 0, cmds, 0, le);
                info = new String(cmds);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("Rec:"+info);
            File homepathfile = new File(home);
            System.out.println("home:"+homepathfile.getAbsolutePath());
            try {
                if ("exit".equalsIgnoreCase(info)) {
                    run = false;
                    for (PSReceiver rsr : recList) {
                        rsr.shutdown();
                    }
                    recList.clear();
                    if (rec != null) {
                        rec.shutdown();
                    }
                    break;
                } else if ("start".equalsIgnoreCase(info)) {
                    rec = PSReceiver.getInstance("tk00005", "3001", "172.16.24.41", 19999);//127.0.0.1

//					rec.request.add("sids","1,2");
                        rec.getRequest().add("sids", "1,2");
//					rec.request.add("vids","2,3,50001");
                        rec.getRequest().add("vids", "21503");
                        rec.setDelegate(delegate);
                        rec.tostart();
                        //rec.request.add("vids","2,3,50002");
//					System.out.println("Rec start:"+rec.uuid);

                } else if (info.startsWith("sendmp3:")) {
                    tm = System.currentTimeMillis();
                    cnt = 0;
                    String route="2";
                    String msg = info.substring(8);
                    String[] ps = msg.split(":");
                    String touid = fromuid;

                    if (ps.length > 0) {
                        touid = ps[0];
                        //msg = ps[1];
                    }
                    if (rec != null) {
                        int sid = 1;
                        int vid = 21503;
                        if (rec.getPsclient().request() != null) {
                            String sn = rec.getPsclient().request().getUuId() + (System.currentTimeMillis() % 10000000);
                            String filepath = home + "/music.mp3";
                            File mp3file = new File(filepath);
                            FileInputStream fis = null;
                            byte[] bb = new byte[128 * 1024];
                            byte[] bdata = null;
                            try {
                                fis = new FileInputStream(mp3file);
                                int page=0;

                                //int pos = 0;
                                int len = fis.available();
                                while (len>0){
                                    len = fis.read(bb);
                                    //pos +=len;
                                    if(len>0){
                                        bdata = new byte[len];
                                        System.arraycopy(bb,0,bdata,0,len);
                                        pageSendTag.put(sn,page);
                                        rec.getPsclient().request().send(""+sid,""+vid,route,touid,"dhbyuid","mp3",""+page,sn,bdata);
                                        System.out.println("send data page:"+page+",touid:"+touid);
                                        page++;


                                       towait();
                                    }

                                }
                                pageSendTag.put(sn,page);
                                pageSendTag.put("lastpage",page);
                                rec.getPsclient().request().send(""+sid,""+vid,route,touid,"dhbyuid","mp3",""+page,sn,null);
                                System.out.println("send data last page:"+page);
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                if (fis != null) {
                                    fis.close();
                                }
                            }
                        }

                    }
                } else if (info.startsWith("send:")) {
                    tm = System.currentTimeMillis();
                    cnt = 0;
                    String msg = info.substring(5);
                    String[] ps = msg.split(":");
                    String touid = fromuid;
                    if (ps.length > 1) {
                        touid = ps[0];
                        msg = ps[1];
                    }
                    if (rec != null) {
                        int sid = 1;
                        int vid = 21503;
                        if (rec.getPsclient().request() != null) {
                            long sn = System.currentTimeMillis();

                            rec.sendmsg(sid, vid, touid, "msg:" + sn + ":" + msg);
                        }
                        System.out.println("TcpClient.main.cmd:" + msg);

                    }
                } else if (info.startsWith("sendvid:")) {
                    tm = System.currentTimeMillis();
                    cnt = 0;
                    int sid = 1;
                    int vid = 21503;
                    String msg = info.substring(8);
                    String[] ps = msg.split(":");
                    String touid = "" + vid;
                    if (ps.length > 1) {
                        touid = ps[0];
                        msg = ps[1];
                    }
                    if (rec != null) {
                        if (rec.getPsclient().request() != null) {
                            long sn = System.currentTimeMillis();
                            rec.sendmsg(sid, vid, touid, "", "msg:" + sn + ":" + msg);
                        }
                        System.out.println("TcpClient.main.cmd:" + msg);

                    }
                } else if (info.startsWith("sendloop:")) {

                    int sid = 1;
                    int vid = 21503;
                    String msg = info.substring(9);
                    String[] ps = msg.split(":");
                    String touid = "" + vid;
                    if (ps.length > 1) {
                        touid = ps[0];
                        msg = ps[1];
                    }
                    if (rec != null) {

                        if (rec.getPsclient().request() != null) {
                            for (int i = 0; i < 1000; i++) {
                                long sn = System.currentTimeMillis();

                                rec.sendmsg(sid, vid, touid, "", "msg:" + sn + ":" + msg);
                                if (i % 100 == 0) {
                                    try {
                                        Thread.sleep(1);
                                    } catch (Exception e) {
                                    }
                                }
                            }
                        }
                        System.out.println("TcpClient.main.cmd:" + msg);
                        tm = System.currentTimeMillis();
                        cnt = 0;

                    }
                } else {
                    if (rec != null) {
                        int sid = 1;
                        int vid = 21503;
                        if (rec.getPsclient().request() != null) {
                            long sn = System.currentTimeMillis();
                            String touid = fromuid;
                            rec.sendmsg(sid, vid, touid, "msg:" + sn + ":" + info);
                        }

                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            //sendloop:书法家啊摔；书法家啊摔；书法家啊摔；书法家啊摔；书法家啊摔；书法家啊摔；书法家啊摔；书法家啊摔；书法家啊摔；书法家啊摔；书法家啊摔；
            //sendvid:书法家啊摔；书法家啊摔；书法家啊摔；书法家啊摔；书法家啊摔；书法家啊摔；书法家啊摔；书法家啊摔；书法家啊摔；书法家啊摔；书法家啊摔；书法家啊摔；
        }
    }
}

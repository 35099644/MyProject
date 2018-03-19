
var videohtml='<video id="example_video_1" class="video-js vjs-default-skin" controls preload="none" width="640" height="264" poster="" data-setup="{}">';
    //videohtml+='<source src="" type="video/mp4" />'
    videohtml+='<track kind="captions" src="demo.captions.vtt" srclang="en" label="English"></track>';
    videohtml+='<track kind="subtitles" src="demo.captions.vtt" srclang="en" label="English"></track>';
    //videohtml+='<p class="vjs-no-js">请使用支持html5的浏览器</p>';
    videohtml+='</video>';
var con = document.createElement('div');
con.id="mp4_con";
document.body.appendChild(con);
document.getElementById('mp4_con').innerHTML=videohtml;
var videoobj = document.querySelector('#mp4_con video');
var audiohtml='<audio controls="controls"></audio>';
con = document.createElement('div');
con.id="mp3_con";
document.body.appendChild(con);
con.innerHTML=audiohtml;
var audioobj = document.querySelector('#mp3_con audio');

var imghtml='<img id="img_001"/>';
con = document.createElement('div');
con.id="img_con";
document.body.appendChild(con);
con.innerHTML=imghtml;
var imgobj=document.querySelector('#img_con img');

var cache={};
var cache_size=2*1024*1024;
var audioContext=null;
try{
    audioContext=new AudioContext();
}catch(e){console.log(e);}
//navigator.getUserMedia = navigator.getUserMedia || navigator.webkitGetUserMedia || navigator.mozGetUserMedia || navigator.msGetUserMedia;

pushclient.pclient.response.onmessage=function(kind,msg,params){

    if(kind==Kind.STRING){
        console.log('msg:'+msg);
        var vals = msg.split(":");
        var tag = vals[0];
        if("apply"==tag){
            var from = params[0];
            window.fromuid=from;
            var page = ~~vals[1];
            var sn = vals[2];
            pushclient.pclient.request.apply(page,sn);
        }else if("msg"==tag){
             var from = params[0];
             window.fromuid=from;
             var sn = vals[1];
             var txt = vals[2];
             console.log("来自"+from+"["+sn+"]:"+txt);
         }
    }else if(kind==Kind.BYTE){
var applytype="apply";
        //[fuid,type,page,sn]
        var sn=params[3],fuid=params[0],page=params[2],type=params[1];
        console.log('sn:'+sn+',fuid:'+fuid+',page:'+page+',type:'+type);
        var sid=1;
        var vid=2001;
        var route=2;
        touid = fuid;
        //if(!touid)touid=window.fromuid;
            //console.log(msg);
            if(page>0&&msg==null){
                if(cache[sn]){
                    /*
                    cbuffer = cache[sn].buffer;
                    if(cbuffer.c){
                        cbuffer.pool[cbuffer.pool.length]=cbuffer.c;
                        cbuffer.c = null;
                        cbuffer.cl=0;
                        cbuffer.pool[cbuffer.pool.length]=null;
                    }
                    */
                    var datas = cache[sn];
                    var _data = new Uint8Array(datas.len);
                    var alldatas = [];
                    var pos = 0;
                    for(var i=0;i<page;i++){
                        alldatas[alldatas.length]=datas[i];
                        _data.set(datas[i],pos);
                        pos += datas[i].length;
                    }

                    if("mp4"==type){
                        var bb = new Blob(alldatas,{"type":"video/mp4"});
                        var source = window.URL.createObjectURL(bb);
                        videoobj.src=source;
                        videoobj.play();
                    }else if("mp3"==type){
                        //var vcontext = new webkitAudioContext();
                        //var bb = new Blob(alldatas,{"type":"video/mp3"});
                        //var source = window.URL.createObjectURL(bb);
                        //console.log("source:"+source);
                        //audioobj.src=source;

                        audioContext.decodeAudioData(_data.buffer,
                        function(buffer) { //解码成功则调用此函数，参数buffer为解码后得到的结果
                            var audioBufferSouceNode = audioContext.createBufferSource();
                            var analyser = audioContext.createAnalyser();
                            audioBufferSouceNode.connect(analyser);
                            analyser.connect(audioContext.destination);
                            audioBufferSouceNode.buffer = buffer;
                            audioBufferSouceNode.start(0);

                        }, function(e) {
                            console.log("!哎玛，文件解码失败:(");
                        });


                    }else if("png"==type){
                        var bb = new Blob(alldatas,{"type":"img/png"});
                        var source = window.URL.createObjectURL(bb);
                        imgobj.src = source;
                    }

                }
                pushclient.pclient.request.send(sid,vid,route,touid,"dhbyuid",applytype+":"+page+":"+sn);
            }else{
                if(!cache[sn]){
                    cache[sn]={"len":0};
                    cache[sn].type=type;
                    cache[sn].buffer={"c":null,"cl":0,"pool":[]};
                }
                //cbuffer = cache[sn].buffer;
                cache[sn][page]=msg;
                cache[sn].len+=msg.length;

                //sid,vid,routeid,touid,method,val
                //pushclient.pclient.request.send(2,Kind.STRING,"msg="+sid+":"+vid+":"+route+":"+page+":"+sn);
//                pushclient.pclient.request.send(sid,vid,route,2000,"dhbyuid",page+":"+sn);
                pushclient.pclient.request.send(sid,vid,route,touid,"dhbyuid",applytype+":"+page+":"+sn);
            }



    }
};
window.fromuid=2001;
//pushclient.pclient.request.host='127.0.0.1';
pushclient.pclient.request.host='172.16.24.41';
pushclient.pclient.request.port=19999;
//pushclient.pclient.request.port=19009;
//pushclient.pclient.request.port=19443;
pushclient.pclient.request.token='tk00005';
pushclient.pclient.request.uuid=2000;
//pushclient.pclient.request.uuid=2001;
pushclient.pclient.request.proxy="ws";
pushclient.pclient.request.add('sids','1');
pushclient.pclient.request.add('vids','21503');
pushclient.pclient.start();
//pushclient.pclient.request.sendbytes("2001","","jpg",new Blob())



function test(){
    d = new Image();
    //d.src = 'https://www.sogou.com/index/images/weather/multicolor/qing.png';
    d.src = 'https://www.sogou.com/index/images/weather/multicolor/duoyun.png';

    var canvas = document.createElement('CANVAS');
            var ctx = canvas.getContext('2d');
            canvas.height=d.height;
            canvas.width=d.width;
            ctx.drawImage(d,0,0);
            canvas.toBlob(function(blob){
                window.imgBlob=blob;
            });
}
function sendtest(){
    if(window.imgBlob){
        var sid=1;
        var vid='21503';
        var routeid=2;
        var touid=null;
        pushclient.pclient.wsclient.blobToArray(window.imgBlob,function(buffer){
            console.log(buffer);
            if(!touid)touid=window.fromuid;
//            pushclient.pclient.request.sendbytes(sid,vid,routeid,"2000","dhbyuid","png",buffer);
            pushclient.pclient.request.sendbytes(sid,vid,routeid,touid,"dhbyuid","png",buffer,true);
        });
    }
}
function sendImg(url){
    var img = new Image();
    var sid=1;
    var vid='21503';
    var routeid=2;
    var touid=null;
    img.crossOrigin='anonymous';
    img.setAttribute('crossOrigin', 'anonymous');
    img.onload=function(){
        var canvas = document.createElement('CANVAS');
        var ctx = canvas.getContext('2d');
        canvas.height=img.height;
        canvas.width=img.width;
        ctx.drawImage(img,0,0);
        canvas.toBlob(function(blob){
            pushclient.pclient.wsclient.blobToArray(blob,function(buffer){
                        console.log(buffer);
            if(!touid)touid=window.fromuid;
//                pushclient.pclient.request.sendbytes(sid,vid,routeid,"2000","dhbyuid","png",buffer,true);
                pushclient.pclient.request.sendbytes(sid,vid,routeid,touid,"dhbyuid","png",buffer,true);
            });
        });
    };
    img.src=url;
}
function testSendImg(url){
    var img = new Image();
    var sid=1;
    var vid='21503';
    var routeid=2;

    img.crossOrigin='anonymous';
    img.setAttribute('crossOrigin', 'anonymous');
    img.onload=function(){
        var canvas = document.createElement('CANVAS');
        var ctx = canvas.getContext('2d');
        canvas.height=img.height;
        canvas.width=img.width;
        ctx.drawImage(img,0,0);
        canvas.toBlob(function(blob){
            console.log(blob);
        });
    };
    img.src=url;
}
function sendImgTag(img){
    var sid=1;
    var vid='21503';
    var routeid=2;
    var canvas = document.createElement('CANVAS');
    var ctx = canvas.getContext('2d');
    var touid=null;
    canvas.height=img.height;
    canvas.width=img.width;
    ctx.drawImage(img,0,0);
    canvas.toBlob(function(blob){
        pushclient.pclient.wsclient.blobToArray(blob,function(buffer){
                console.log(buffer);
                    if(!touid)touid=window.fromuid;
                    pushclient.pclient.request.sendbytes(sid,vid,routeid,touid,"dhbyuid","png",buffer,true);
                });
    });
}
function testSendImgTag(img){
    var sid=1;
    var vid='21503';
    var routeid=2;
    var canvas = document.createElement('CANVAS');
    var ctx = canvas.getContext('2d');
    img.crossOrigin='anonymous';
    img.setAttribute('crossOrigin', 'anonymous');
    canvas.height=img.height;
    canvas.width=img.width;
    ctx.drawImage(img,0,0);
    canvas.toBlob(function(blob){
        console.log(blob);
    });
}
function sendText(txt,touid){
    var sid=1;
    var vid='21503';
    var routeid=2;
    if(!touid)touid=window.fromuid;
    var sn = ""+(new Date()).getTime();
    pushclient.pclient.request.send(sid,vid,routeid,touid,"dhbyuid","msg:"+sn+":"+txt);
}
var global_menu_tag=true;
function readyImgs(){
    document.oncontextmenu=function(){
        return global_menu_tag;
    }
    var imgs = document.querySelectorAll('img');
    imgs.forEach(function(item,idx){
        //console.log("idx:"+idx+",item:"+item);
        item.onmousedown=function(evt){
            var e = evt||window.event;
            if(e.button==2){
                if(confirm("要发送该图片吗？")){
                    console.log("发送该图片:"+e.target.src);
                    sendImg(e.target.src);
                }
            }
        };
        item.onmouseover=function(){
            global_menu_tag=false;
            console.log("global_menu_tag:"+global_menu_tag);
        };
        item.onmouseout=function(){
            global_menu_tag=true;
            console.log("global_menu_tag:"+global_menu_tag);
        };
    });
}

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
var audioContext=new AudioContext();
//navigator.getUserMedia = navigator.getUserMedia || navigator.webkitGetUserMedia || navigator.mozGetUserMedia || navigator.msGetUserMedia;
function canPlayVideo(){
    for(var sn in cache){
        var mediaobj = cache[sn];
        var _buffer = mediaobj.buffer;
        var type = mediaobj.type;
        if(!mediaobj._paly&&_buffer.pool.length>0){
        mediaobj._paly=true;
        _buffer.pos=0;
            if("mp4"===type){
                videoobj.sn = sn;
                var stream = _buffer.pool[_buffer.pos++];
                if(stream){
                    videoobj.stream = stream;
                    var bb = new Blob(stream,{"type":"video/mp4"});
                    var source = window.URL.createObjectURL(bb);
                    videoobj.src=source;
                    videoobj.onended=function(evt){


                        var _sn = evt.target.sn;
                        if(_sn){
                            var mediaobj = cache[_sn];
                            var _buffer = mediaobj.buffer;
                            console.log("video next seg,sn:"+_sn+",pos:"+_buffer.pos+",_buffer.pool.length:"+_buffer.pool.length);
                            console.log("videoobj.lastct:"+evt.target.lastct+",ct:"+evt.target.ct);
                            var changed=false;
                            var stream = [];
                            for(var k=0;k<_buffer.pool.length;k++){
                                var nxt_stream = _buffer.pool[k];
                                if(nxt_stream){

                                    for(var i=0;i<nxt_stream.length;i++)stream.push(nxt_stream[i]);
                                    changed=true;

                                }else{
                                     delete cache[videoobj.sn];
                                    videoobj.sn=null;
                                }
                                _buffer.pos=k;
                            }
                            if(changed){
                                console.log("stream changed,sn:"+_sn+",pos:"+_buffer.pos);
                                var bb = new Blob(stream,{"type":"video/mp4"});
                                source = window.URL.createObjectURL(bb);
                                window.URL.revokeObjectURL(videoobj.src);
                                videoobj.src=source;
                                //videoobj.startTime=evt.target.lastct;
                                videoobj.currentTime=evt.target.lastct;
                                videoobj.play();
                            }
                        }




                    };
                    videoobj.ontimeupdate=function(evt){

                        evt.target.lastct = evt.target.ct;
                        evt.target.ct = evt.target.currentTime;

                    };
                    videoobj.onerror=function(evt){
                        for(var k in evt){
                            console.log("onemptied,"+k+":"+evt[k]);
                        }
                        console.log("currentTime:"+videoobj.currentTime+",duration:"+videoobj.duration);
                    };
                    videoobj.onabort=function(evt){
                        for(var k in evt){
                            console.log("onabort,"+k+":"+evt[k]);
                        }
                        console.log("currentTime:"+videoobj.currentTime+",duration:"+videoobj.duration);
                    };
                    videoobj.play();

                }else{
                    videoobj.sn=null;
                    delete cache[sn];
                }
            }

        }
    }

}
pushclient.pclient.response.onmessage=function(kind,msg,params){

    if(kind==Kind.STRING){
        console.log('msg:'+msg);
    }else if(kind==Kind.BYTE){
    var applytype="apply";
        var sn=params[3],fuid=params[0],page=params[2],type=params[1];
        console.log('sn:'+sn+',fuid:'+fuid+',page:'+page+',type:'+type);
        var sid=1;
        var vid=2001;
        var route=2;

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
                    /*
                    window.URL = window.URL||window.webkitURL;
                    var datas = cache[sn];
                    console.log(type+" len:"+datas.len);
                    var clen = 2*1024*1024;
                    var _databuffer = new ArrayBuffer(datas.len);
                    var _data = new Uint8Array(datas.len);
                    var pos = 0;
                    var alldatas = [];
                    for(var i=0;i<page;i++){
                        alldatas[alldatas.length]=datas[i];
                        _data.set(datas[i],pos);
                        pos += datas[i].length;
                    }

                    //delete cache[sn];

                    console.log("all data append over._data len:"+_data.length);
                     */

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
                pushclient.pclient.request.send(sid,vid,route,2000,"dhbyuid",applytype+":"+page+":"+sn);
            }else{
                if(!cache[sn]){
                    cache[sn]={"len":0};
                    cache[sn].type=type;
                    cache[sn].buffer={"c":null,"cl":0,"pool":[]};
                }
                //cbuffer = cache[sn].buffer;
                cache[sn][page]=msg;
                cache[sn].len+=msg.length;
                /*
                cache[sn].lastpos+=msg.length;
                if(!cbuffer.c){
                    cbuffer.c = [msg];
                    cbuffer.cl=msg.length;
                }else{
                    if(cbuffer.cl<cache_size){
                        cbuffer.c[cbuffer.c.length]=msg;
                        cbuffer.cl+=msg.length;
                    }
                }
                if(cbuffer.c&&cbuffer.cl>=cache_size){
                    cbuffer.pool[cbuffer.pool.length]=cbuffer.c;
                    cbuffer.c = null;
                    cbuffer.cl=0;
                }

                canPlayVideo();
                */
                /*
                if(page==0){
                    cache[sn].data=[];
                    cache[sn].data[cache[sn].data.length]=msg;
//                    if("mp4"==type){
//
//                    }else if("mp3"==type){
//                        var bb = new Blob(cache[sn].data,{"type":"video/mp3"});
//                        var source = window.URL.createObjectURL(bb);
//                        audioobj.src=source;
//                        audioobj.play();
//                    }
                }else{
                    cache[sn].data[cache[sn].data.length]=msg;
                }
                if("mp3"==type){

                }else if("mp4"==type){

                }
                */
                /*
                if(cache[sn].lastpos>1024*1024&&!cache[sn].init){
                    cache[sn].lastpos=0;
                    cache[sn].lastpage=page;
                    cache[sn].init=true;
                    var bb = null;
                    if(!cache[sn].lastpage){
                         bb = new Blob(cache[sn].data,{"type":"video/mp3"});
                         var source = window.URL.createObjectURL(bb);
                         audioobj.src=source;
                         audioobj.play();
                         audioobj.addEventListener("progress", function(evt)
                           {
                                for(var k in evt){

                                }
                           }
                         );
                    }
                }
                */
                //sid,vid,routeid,touid,method,val
                //pushclient.pclient.request.send(2,Kind.STRING,"msg="+sid+":"+vid+":"+route+":"+page+":"+sn);
                pushclient.pclient.request.send(sid,vid,route,2000,"dhbyuid",page+":"+sn);
//                pushclient.pclient.request.send(sid,vid,route,2001,"dhbyuid",page+":"+sn);
            }



    }
};
pushclient.pclient.request.host='127.0.0.1';
//pushclient.pclient.request.host='192.168.1.225';
pushclient.pclient.request.port=19999;
//pushclient.pclient.request.port=19009;
pushclient.pclient.request.token='tk00005';
//pushclient.pclient.request.uuid=2000;
pushclient.pclient.request.uuid=2001;
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
        pushclient.pclient.wsclient.blobToArray(window.imgBlob,function(buffer){
            console.log(buffer);
            pushclient.pclient.request.sendbytes(sid,vid,routeid,"2000","dhbyuid","png",buffer);
//            pushclient.pclient.request.sendbytes(sid,vid,routeid,"2001","dhbyuid","png",buffer);
        });
    }
}
/*
d = new Image();
d.src = 'https://www.sogou.com/index/images/weather/multicolor/qing.png';
var canvas = document.createElement('CANVAS');
        var ctx = canvas.getContext('2d');
        canvas.height=d.height;
        canvas.width=d.width;
        ctx.drawImage(d,0,0);
var imgData=ctx.getImageData(0,0,canvas.height,canvas.width);

console.log(imgDate);
if(imgData){
    pushclient.pclient.request.sendbytes("2001","","png",imgData.data);
}
        canvas.toBlob(function(blob){
                console.log(blob);
                pushclient.pclient.request.sendbytes("2001","","png",blob);
        },"image/png",0.85);
*/
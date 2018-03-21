package com.tensynchina.hook.push;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.llx278.exeventbus.ExEventBus;
import com.llx278.exeventbus.Subscriber;
import com.llx278.exeventbus.ThreadModel;
import com.llx278.exeventbus.Type;
import com.llx278.exeventbus.exception.TimeoutException;
import com.orhanobut.logger.Logger;
import com.tensynchina.hook.task.TaskService;
import com.tensynchina.hook.utils.IOUtils;
import com.tensynchina.hook.Message;
import com.tensynchina.push.client.impl.AbstractPResponse;
import com.tensynchina.push.client.impl.PClient;
import com.tensynchina.push.client.impl.PRequest;
import com.tensynchina.push.sdk.android.PSReceiverWrapper;


/**
 *
 * Created by llx on 2018/2/1.
 */

public class MessageService extends Service implements PSReceiverWrapper.TimeoutNotifyDelegate {

    public static final String MESSAGE_SEND_TAG = "com_tensynchina_hook_push_MessageService_sendMessage";
    private static final String MESSAGE_RECEIVE_ACTION = "com.llx278.message.RECEIVE";

    private Receiver mReceiver;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.d("MessageService onCreate");
        new Thread(){
            @Override
            public void run() {
                ExEventBus.create(MessageService.this);
                ExEventBus.getDefault().register(MessageService.this);
            }
        }.start();


        mReceiver = new Receiver();
        mReceiver.registerPSClient();
        mReceiver.getPsReceiver().setOnSendTimeoutListener(this);
        mReceiver.getPsReceiver().setDelegate(new MsgResponseDelegate() {
            @Override
            public void receiveString(PClient<? extends PRequest, ?
                    extends AbstractPResponse> pclient, String[] params, String message) {
                String msg = message;
                String posstr = params[params.length - 1];
                if (posstr != null) {
                    int pos = pclient.parseInt(posstr, -1);
                    if (pos >= 0) {
                        if (pos >= message.length()) {
                            msg = "";
                        } else {
                            msg = message.substring(pos);
                        }
                    }
                }
                if (!TextUtils.isEmpty(msg) && msg.equals(mReceiver.getTag())) {
                    mReceiver.notifyAck();
                    return;
                }
                String uuid = params[0];
                publishMessage(uuid,msg);
            }
        });
    }

    private void publishMessage(String uuid, String msg) {
        Logger.d("收到消息 ： " + msg);
        Message msgObj = new Message(msg,uuid);
        try {
            ExEventBus.getDefault().remotePublish(msgObj, TaskService.RECEIVE_MESSAGE_TAG,
                    void.class.getName(),3000);
            Logger.d("已经发送到PushService的广播");
        } catch (TimeoutException e) {
            Logger.e(e,"");
        }
    }

    private static final String tag1  ="{\"code\":0,\"param\":{\"packageName\":\"com.tencent.mm\",\"taskTag\":1,\"taskId\":\"abc\",\"json\":\"{\\\"keyDes\\\":[\\\"丁香园\\\"]}\"}}";
    private static final String tag2 = "{\"code\":0,\"param\":{\"packageName\":\"com.tencent.mm\",\"taskTag\":2,\"taskId\":\"123\",\"json\":\"{\\\"keyDes\\\":\\\"飞思卡尔\\\",\\\"time\\\":1500652800}\"}}";
    private static final String tag3 = "{\"code\":0,\"param\":{\"packageName\":\"com.tencent.mm\",\"taskTag\":3,\"taskId\":\"1500652800\",\"json\":\"{\\\"keyDes\\\":\\\"丁香园\\\",\\\"time\\\":1512000000}\"}}";
    private static final String tag4 = "{\"code\":0,\"param\":{\"packageName\":\"com.tencent.mm\",\"taskTag\":4,\"taskId\":\"111\",\"json\":\"{\\\"url\\\":\\\"https://mp.weixin.qq.com/s?src=11&timestamp=1521624601&ver=768&signature=IWnjlwNKJ0D8U5jBU-g7at-EKcHrIiwYvV6nMR0YP2zKhen8WTyLvmfVPnitwBGMo15DPQm-JFrm6rnovn7Y-acvIuZd1c7650zRS-XU1f2Oylw-7oZOX8*8OgS5OFJJ&new=1\\\"}\"}}";
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String id = intent.getStringExtra("id");
        if (!TextUtils.isEmpty(id)) {
            switch (id) {
                case "1":
                    new Thread(){
                        @Override
                        public void run() {
                            publishMessage("2001",tag1);
                        }
                    }.start();

                    break;
                case "2":
                    new Thread(){
                        @Override
                        public void run() {
                            publishMessage("2001",tag2);
                        }
                    }.start();
                    break;
                case "3":
                    new Thread(){
                        @Override
                        public void run() {
                            publishMessage("2001",tag3);
                        }
                    }.start();
                    break;
                case "4":
                new Thread(){
                    @Override
                    public void run() {
                        publishMessage("2001",tag4);
                    }
                }.start();
                break;
                default:
            }

        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Logger.d("MessageService onDestroy");
        new Thread(){
            @Override
            public void run() {
                ExEventBus.destroy();
            }
        }.start();
    }

    @Override
    public void onTimeout(int sid, int vid, int route, String touid, String method, String msg) {
        //MLogger.d("msg = " + msg.substring(0, 20) + "timeout");
    }

    @SuppressLint("SdCardPath")
    @Subscriber(tag = MESSAGE_SEND_TAG,model = ThreadModel.MAIN,remote = true,type = Type.DEFAULT)
    public void sendMessage(Message message) {
        String uuid = message.getUuid();
        String msg = message.getMessage();
        Logger.d("MessageService 收到了从pushService发送的消息： uuid:" + uuid + " msg : " + msg);
        try {
            String relMsg = null;
            if (msg.startsWith("/mnt/sdcard/")) {
                relMsg = IOUtils.fileToString(msg);
            } else {
                relMsg = msg;
            }
            mReceiver.send(uuid,relMsg);
            Logger.d("向服务器发送完成!");
        } catch (Exception e) {
            Logger.e(e,"");
        }
    }
}

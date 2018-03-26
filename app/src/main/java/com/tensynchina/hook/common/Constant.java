package com.tensynchina.hook.common;

import android.annotation.SuppressLint;

/**
 *
 * Created by llx on 2018/3/20.
 */

public class Constant {
    @SuppressLint("SdCardPath")
    public static final String RESULT_LOCAL_PATH = "/mnt/sdcard/.llx278/local_result";
    /**
     * 订阅接收{@link com.tensynchina.hook.push.MessageService}发送的消息的事件
     */
    public static final String RECEIVE_MESSAGE_TAG = "com_tensynchina_hook_task_TaskService_receiveMessage";
    /**
     * 订阅杀死当前进程的事件
     */
    public static final String PROCESS_KILL_TAG = "com_tensynchina_hook_task_TaskService_process_kill";
    /**
     * 订阅向{@link com.tensynchina.hook.push.MessageService}发送消息的事件
     */
    public static final String MESSAGE_SEND_TAG = "com_tensynchina_hook_push_MessageService_sendMessage";

    /**
     * 订阅向{@link com.tensynchina.hook.push.MessageService}发送推送消息的事件
     */
    public static final String PUSH_MESSAGE_TAG = "com_tensynchina_hook_push_MessageService_pushMessage";

    public static final String WX_TASK_TAG = "com.tencent.mm_TAG";
}

package com.tensynchina.hook.task;

import android.content.Context;

/**
 * 对一个任务生成不同的处理方式
 * Created by llx on 2018/3/22.
 */

public abstract class TaskHandler {

    public abstract void handle(Context context);
}

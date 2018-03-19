package com.tensynchina.hook.wechat;

import com.llx278.uimocker2.Solo;
import com.tensynchina.hook.task.Param;
import com.tensynchina.hook.task.Result;

/**
 *
 * Created by llx on 2018/3/16.
 */

public abstract class BaseTask {
    public abstract Result execute(Solo solo, Param param);
}

package com.tensynchina.hook.wechat;

import com.orhanobut.logger.Logger;
import com.tensynchina.hook.utils.XLogger;

/**
 *
 * Created by llx on 2018/3/16.
 */

class CreatorForTask {

    static BaseTask create(int taskTag) {
        String task1ClassName = "com.tensynchina.hook.wechat.Task" + taskTag;
        try {
            Class<?> task1Class = Class.forName(task1ClassName);
            return (BaseTask) task1Class.newInstance();
        } catch (Exception e) {
            Logger.e(e,"");
        }
        return null;
    }
}

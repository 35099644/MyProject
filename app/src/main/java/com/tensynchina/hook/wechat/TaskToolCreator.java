package com.tensynchina.hook.wechat;

import com.orhanobut.logger.Logger;

/**
 * Created by llx on 2018/3/19.
 */

public class TaskToolCreator {

    static BaseTask create(int taskTag) {

        String task1ClassName = "com.tensynchina.hook.wechat.TaskTool" + taskTag;
        try {
            Class<?> task1Class = Class.forName(task1ClassName);
            return (BaseTask) task1Class.newInstance();
        } catch (Exception e) {
            Logger.e(e,"");
        }
        return null;
    }

}

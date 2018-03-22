package com.tensynchina.hook.task;

/**
 * 根据task的不同类型，生成不同的任务
 * Created by llx on 2018/3/22.
 */

public class TaskHandlerFactory {

    static TaskHandler parse(Task task){
        switch (task.getCode()) {
            case 0:
                return new Code0Task(task.getParam());
            case 1:
                return new Code1Task(task.getParam());
            default:
        }
        return null;
    }
}

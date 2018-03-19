package com.tensynchina.hook.task;

/**
 *
 * Created by llx on 2018/3/16.
 */

public class Task {
    private int code;
    private Param param;

    public Task() {
    }

    public Task(int code, Param param) {
        this.code = code;
        this.param = param;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Param getParam() {
        return param;
    }

    public void setParam(Param param) {
        this.param = param;
    }
}

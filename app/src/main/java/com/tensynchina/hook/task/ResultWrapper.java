package com.tensynchina.hook.task;

/**
 * Created by llx on 2018/3/16.
 */

public class ResultWrapper {
    int code;
    Result result;

    public ResultWrapper(int code, Result result) {
        this.code = code;
        this.result = result;
    }

    public ResultWrapper() {
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }
}

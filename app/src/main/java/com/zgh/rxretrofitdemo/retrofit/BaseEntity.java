package com.zgh.rxretrofitdemo.retrofit;

/**
 * Created by ZGH on 2017/9/8.
 */

public class BaseEntity<T> {
    private int code;
    private long time;

    private String msg;

    private T      data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}

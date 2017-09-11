package com.zgh.rxretrofitdemo.retrofit;

/**
 * Created by ZGH on 2017/9/8.
 */

public class HttpResult<T> {
    private int count;
    private int start;
    private int total;
    private String title;

    private T subjects;//用来模仿 data,真正的请求中 data 可能是各种各样的，所以用泛型来表示

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public T getSubjects() {
        return subjects;
    }

    public void setSubjects(T subjects) {
        this.subjects = subjects;
    }

    @Override
    public String toString() {
        return "HttpResult{" + "count=" + count + ", start=" + start + ", total=" + total +
               ", title='" + title + '\'' + ", subjects=" + subjects + '}';
    }
}

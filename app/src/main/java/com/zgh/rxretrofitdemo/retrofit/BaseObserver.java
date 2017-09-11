package com.zgh.rxretrofitdemo.retrofit;

import com.blankj.utilcode.util.LogUtils;
import com.zgh.rxretrofitdemo.JsonTools;

import java.io.IOException;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * Created by ZGH on 2017/9/8.
 */

public abstract class BaseObserver<T> implements Observer<BaseEntity<T>>{
    private final int SUCCESS_CODE = 0;

    @Override
    public void onSubscribe(@NonNull Disposable d) {
        onRequestStart();
    }

    @Override
    public void onNext(@NonNull BaseEntity<T> tBaseEntity) {
        onRequestEnd("onNext");
        if (tBaseEntity.getCode() == SUCCESS_CODE) {
            onSuccess(tBaseEntity.getData());
            try {
                LogUtils.json(JsonTools.toJson(tBaseEntity.getData()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            onFailure(tBaseEntity.getCode(),"codeError");
            LogUtils.e("HTTP","codeError--" + tBaseEntity.getCode());
        }
    }

    @Override
    public void onError(@NonNull Throwable e) {
        onRequestEnd("onError");
        onFailure(404,e.toString());
        LogUtils.e("HTTP","onError--" + e.toString());
    }

    @Override
    public void onComplete() {
    }

    /**
     * 请求成功，将数据返回
     */
    protected abstract void onSuccess(T data);

    /**
     * 请求失败，将结果码和信息返回
     */
    protected abstract void onFailure(int code, String msg);

    private void onRequestStart() {
    }

    private void onRequestEnd(String msg) {
        LogUtils.d("HTTP","请求结束--" + msg);
    }
}

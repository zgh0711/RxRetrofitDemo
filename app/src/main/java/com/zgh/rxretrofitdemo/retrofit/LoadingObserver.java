package com.zgh.rxretrofitdemo.retrofit;

import android.content.Context;

import com.blankj.utilcode.util.LogUtils;
import com.zgh.rxretrofitdemo.JsonTools;

import java.io.IOException;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * Created by ZGH on 2017/9/11.
 */

public abstract class LoadingObserver<T> implements Observer<BaseEntity<T>> {
    private final int SUCCESS_CODE = 0;
    private Context context;

    public LoadingObserver(Context context) {
        this.context = context;
    }

    @Override
    public void onSubscribe(@NonNull Disposable d) {
        onRequestStart();
    }

    @Override
    public void onNext(@NonNull BaseEntity<T> tBaseEntity) {
        onRequestEnd("onNext");
        if (tBaseEntity.getCode() == SUCCESS_CODE) {
            try {
                LogUtils.json(JsonTools.toJson(tBaseEntity.getData()));
            } catch (IOException e) {
                e.printStackTrace();
            }

            //有数据则返回成功，否则返回失败并提示
            if (tBaseEntity.getData() != null) {
                onSuccess(tBaseEntity.getData());
            } else {
                onFailure(0,"数据为空");
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
        LogUtils.e("HTTP", "onError--" + e.toString());
    }

    @Override
    public void onComplete() {
        onRequestEnd("onComplete");
    }

    private void onRequestStart() {
        LoadingDialog.show(context);
    }

    private void onRequestEnd(String msg) {
        LoadingDialog.cancel();
        LogUtils.d("HTTP","请求结束--" + msg);
    }

    /**
     * 请求成功，将数据返回
     */
    protected abstract void onSuccess(T data);

    /**
     * 请求失败，将结果码和信息返回
     */
    protected abstract void onFailure(int code, String msg);
}

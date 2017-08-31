package com.zgh.rxretrofitdemo.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.blankj.utilcode.util.LogUtils;
import com.zgh.rxretrofitdemo.R;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.annotations.NonNull;

/**
 * 给初学者的RxJava2.0教程(七)
 * Flowable 控制上下游流速的相关知识
 * http://www.jianshu.com/p/9b1304435564
 */
public class Test5 extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test5);

        Button btn1 = (Button) findViewById(R.id.btn_1);
        Button btn2 = (Button) findViewById(R.id.btn_2);
        Button btn3 = (Button) findViewById(R.id.btn_3);

        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_1:
                baseUse();
                break;
            case R.id.btn_2:
                break;
            case R.id.btn_3:
                break;
        }
    }

    //最基本的用法，上下游工作在同一线程
    private void baseUse() {
        Flowable<Integer> upStream = Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<Integer> e) throws Exception {
                LogUtils.d("emit--1");
                e.onNext(1);
                LogUtils.d("emit--2");
                e.onNext(2);
                LogUtils.d("emit--3");
                e.onNext(3);
                LogUtils.d("emit--4");
                e.onNext(4);
                e.onComplete();
            }
        }, BackpressureStrategy.ERROR);//使用 Flowable 需要传入的一个参数，这个参数是用来选择背压,也就是出现上下游流速不均衡的时候应该怎么处理的办法

        Subscriber<Integer> downStream = new Subscriber<Integer>() {
            @Override
            public void onSubscribe(Subscription s) {
                LogUtils.d("onSubscribe");
                //使用 Flowable 需要增加的代码，作用是下游向上游发出请求，
                //下游要根据自己的处理能力来向上游请求事件个数，具体的原理看博客
                s.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(Integer integer) {
                LogUtils.d("onNext--" + integer);
            }

            @Override
            public void onError(Throwable t) {
                LogUtils.d("onError");
            }

            @Override
            public void onComplete() {
                LogUtils.d("onComplete");
            }
        };

        upStream.subscribe(downStream);
    }
}

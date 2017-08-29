package com.zgh.rxretrofitdemo.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.blankj.utilcode.util.LogUtils;
import com.zgh.rxretrofitdemo.R;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * 给初学者的RxJava2.0教程(一)
 * RxJava 的基础知识和基本用法
 * http://www.jianshu.com/p/464fa025229e
 */
public class Test1 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test1);

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                rxJava1();
                rxJava2();
            }
        });
    }


    //传统写法
    private void rxJava1() {
        //创建一个上游，被观察者，事件发出者
        Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> e) throws Exception {
                //ObservableEmitter 事件发射器，发送事件
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
                e.onComplete();
            }
        });

        //创建一个下游，观察者，事件接受者
        Observer<Integer> observer = new Observer<Integer>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                LogUtils.d("subscribe");
            }

            @Override
            public void onNext(@NonNull Integer integer) {
                LogUtils.d(integer);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                LogUtils.e(e);
            }

            @Override
            public void onComplete() {
                LogUtils.d("onComplete");
            }
        };

        observable.subscribe(observer);
    }

    // RxJava 特色，链式调用，同时使用 Disposable 来切断上下游之间的事件传递
    private void rxJava2() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> e) throws Exception {
                LogUtils.d("Thread", Thread.currentThread().getName());
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
                e.onNext(4);
                e.onNext(5);
                e.onComplete();
            }
        }).subscribe(new Observer<Integer>() {
            private Disposable disposable;
            private int i;

            @Override
            public void onSubscribe(@NonNull Disposable d) {
                LogUtils.d("subscribe");
                disposable = d;
            }

            @Override
            public void onNext(@NonNull Integer integer) {
                LogUtils.d(integer);
                i++;
                if (i == 3) {
                    //切断事件传递
                    disposable.dispose();
                    LogUtils.d(disposable.isDisposed());
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                LogUtils.d(e);
            }

            @Override
            public void onComplete() {
                LogUtils.d("onComplete");
            }
        });
    }
}

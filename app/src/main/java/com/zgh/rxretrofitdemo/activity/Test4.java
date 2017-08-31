package com.zgh.rxretrofitdemo.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.blankj.utilcode.util.LogUtils;
import com.zgh.rxretrofitdemo.R;

import java.io.InterruptedIOException;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

/**
 * 给初学者的RxJava2.0教程(四)
 * 组合操作符 zip
 * http://www.jianshu.com/p/bb58571cdb64
 */
public class Test4 extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test4);

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
                zipTransform();
                break;
            case R.id.btn_2:
                break;
            case R.id.btn_3:
                break;
        }
    }

    private void zipTransform() {
        Observable<Integer> observable1 = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                LogUtils.d("emit 1");
                Thread.sleep(1000);
                e.onNext(2);
                LogUtils.d("emit 2");
                Thread.sleep(1000);
                e.onNext(3);
                LogUtils.d("emit 3");
                Thread.sleep(1000);
                e.onNext(4);
                LogUtils.d("emit 4");
                Thread.sleep(1000);
                e.onComplete();
                LogUtils.d("ob1.complete");
            }
        }).subscribeOn(Schedulers.io());

        Observable<String> observable2 = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                e.onNext("a");
                LogUtils.d("emit a");
                Thread.sleep(1000);
                e.onNext("b");
                LogUtils.d("emit b");
                Thread.sleep(1000);
                e.onNext("c");
                LogUtils.d("emit c");
                Thread.sleep(1000);
                e.onComplete();
                LogUtils.d("ob2.complete");
            }
        }).subscribeOn(Schedulers.io());

        Observable.zip(observable1, observable2, new BiFunction<Integer, String, String>() {
            @Override
            public String apply(@NonNull Integer integer, @NonNull String s) throws Exception {
                return integer + s;
            }
        }).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                LogUtils.d("onSubscribe");
            }

            @Override
            public void onNext(@NonNull String s) {
                LogUtils.d("value--" + s);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                LogUtils.d("error");
            }

            @Override
            public void onComplete() {
                LogUtils.d("onComplete");
            }
        });
    }


    //上面的代码中，当 observable2 的事件发送完毕后会结束线程，
    //而这时候 observable1 的事件还没有发送完，且还在 sleep，这时候去结束线程就会导致报错
    //这段静态代码就是为了解决这个问题
    static {
        RxJavaPlugins.setErrorHandler(new Consumer() {
            @Override
            public void accept(Object o) throws Exception {
                if (o instanceof InterruptedIOException) {
                    LogUtils.d( "Io interrupted");
                }
            }

        });
    }
}

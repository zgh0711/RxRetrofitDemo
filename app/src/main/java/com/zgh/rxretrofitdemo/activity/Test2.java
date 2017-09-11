package com.zgh.rxretrofitdemo.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.zgh.rxretrofitdemo.R;
import com.zgh.rxretrofitdemo.retrofit.MovieEntity;
import com.zgh.rxretrofitdemo.retrofit.MovieService;
import com.zgh.rxretrofitdemo.retrofit.RetrofitManager;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 给初学者的RxJava2.0教程(二)
 * 线程切换及简单的与 Retrofit 结合使用
 * http://www.jianshu.com/p/8818b98c44e2
 */
public class Test2 extends AppCompatActivity implements View.OnClickListener {
    private Disposable disposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test2);

        Button btn1 = (Button) findViewById(R.id.btn_1);
        Button btn2 = (Button) findViewById(R.id.btn_2);

        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_1:
                changeThread();
                break;
            case R.id.btn_2:
                withRetrofit();
                break;
        }
    }

    private void changeThread() {
        Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> e) throws Exception {
                LogUtils.d(Thread.currentThread().getName());
                e.onNext(1);
            }
        });

        //observer 的简便写法，如果只关心 onNext，不关心 onError 和 onComplete 等其他方法，可以这样写
        Consumer<Integer> consumer = new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                LogUtils.d(integer);
            }
        };

        observable.subscribeOn(Schedulers.io())  //指定上游发送事件的线程
                  .observeOn(AndroidSchedulers.mainThread())  //指定下游接收事件的线程
                  .subscribe(consumer);
    }

    private void withRetrofit() {
        String baseUrl = "https://api.douban.com/v2/movie/";
        MovieService service = RetrofitManager.getInstance(baseUrl).create(MovieService.class);

        service.getTopMovies()
               .subscribeOn(Schedulers.io())
               .observeOn(AndroidSchedulers.mainThread())
               .subscribe(new Observer<MovieEntity>() {
                   @Override
                   public void onSubscribe(@NonNull Disposable d) {
                       disposable = d;
                   }

                   @Override
                   public void onNext(@NonNull MovieEntity movieEntity) {
                       LogUtils.d(movieEntity.getCount());
                       LogUtils.d(movieEntity.getSubjects().get(1).getTitle());
                   }

                   @Override
                   public void onError(@NonNull Throwable e) {
                       ToastUtils.showShort("error");
                       LogUtils.d(e.getLocalizedMessage());
                       LogUtils.d(e.getMessage());
                       LogUtils.d(e.toString());
                   }

                   @Override
                   public void onComplete() {
                       ToastUtils.showShort("onComplete");
                   }
               });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //当 Activity 退出时，切断事件传递，以避免继续发送事件到主线程导致程序崩溃
        if (disposable != null) {
            disposable.dispose();
        }

    }
}

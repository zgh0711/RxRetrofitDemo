package com.zgh.rxretrofitdemo.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.blankj.utilcode.util.LogUtils;
import com.zgh.rxretrofitdemo.R;
import com.zgh.rxretrofitdemo.retrofit.ApiService;
import com.zgh.rxretrofitdemo.retrofit.BaseEntity;
import com.zgh.rxretrofitdemo.retrofit.BaseObserver;
import com.zgh.rxretrofitdemo.retrofit.RetrofitManager;
import com.zgh.rxretrofitdemo.retrofit.TestEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * 给初学者的RxJava2.0教程(三)
 * rxjava ,retrofit 结合封装使用，
 * rxjava 转换操作符 map，flatMap，concaatMap
 * http://www.jianshu.com/p/128e662906af
 */
public class Test3 extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test3);

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
                mapTransform();
                break;
            case R.id.btn_2:
                flatMapTransform();
                break;
            case R.id.btn_3:
                requestData();
                break;
        }
    }

    private void requestData() {
        String url = "https://api.zdlot.yinjiahuitong.com/";
        ApiService service = RetrofitManager.getInstance(url).create(ApiService.class);
        service.getData()
                .compose(this.<BaseEntity<TestEntity>>switchThread())
                .subscribe(new BaseObserver<TestEntity>() {
                    @Override
                    protected void onSuccess(TestEntity data) {

                    }

                    @Override
                    protected void onFailure(int code, String msg) {

                    }
                });
    }

    //经过 flatMap 转换后的事件是无序发送的
    private void mapTransform() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
                e.onComplete();
            }
        }).map(new Function<Integer, String>() {
            @Override
            public String apply(@NonNull Integer integer) throws Exception {
                return "This is result--" + integer;
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                LogUtils.d(s);
            }
        });
    }

    //经过 concatMap 转换后的事件是是按顺序发送的，与原事件的顺序一致
    private void flatMapTransform() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
                e.onComplete();
            }
        }).flatMap(new Function<Integer, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(@NonNull Integer integer) throws Exception {
                List<String> list = new ArrayList<String>();
                for (int i = 0; i < 3; i++) {
                    list.add("value--" + integer);
                }
                return Observable.fromIterable(list).delay(50, TimeUnit.MILLISECONDS);
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                LogUtils.d(s);
            }
        });
    }


    /**
     * 封装线程切换，在网络请求时用 compose 操作符调用
     */
    public <T> ObservableTransformer<T,T> switchThread() {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(@NonNull Observable<T> upstream) {
                return upstream.subscribeOn(Schedulers.io())
                               .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }
}

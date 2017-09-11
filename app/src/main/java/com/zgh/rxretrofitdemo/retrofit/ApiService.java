package com.zgh.rxretrofitdemo.retrofit;

import io.reactivex.Observable;
import retrofit2.http.GET;

/**
 * Created by ZGH on 2017/9/8.
 */

public interface ApiService {
    @GET("setting")
    Observable<BaseEntity<TestEntity>> getData();
}

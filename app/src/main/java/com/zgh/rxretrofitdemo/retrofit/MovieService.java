package com.zgh.rxretrofitdemo.retrofit;

import io.reactivex.Observable;
import retrofit2.http.GET;

/**
 * Created by ZGH on 2017/8/23.
 */

public interface MovieService {
    @GET("top250")
    Observable<MovieEntity> getTopMovies();
}

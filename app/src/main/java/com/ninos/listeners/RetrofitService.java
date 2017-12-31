package com.ninos.listeners;

import com.ninos.models.PostResponse;
import com.ninos.models.Profile;
import com.ninos.models.RegisterResponse;
import com.ninos.models.UserCheckResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by FAMILY on 14-12-2017.
 */

public interface RetrofitService {
    @POST("register")
    Call<RegisterResponse> registerChild(@Body Profile profile);

    @GET("register")
    Call<Profile> getProfile(@Header("Authorization") String token);

    @GET("check/{userId}")
    Call<UserCheckResponse> userCheck(@Path("userId") String userId);


    @GET("posts")
    Call<PostResponse> getPosts(@Query("from") int from, @Query("size") int size);
}
